package com.rcube.app.data.repository

import com.rcube.app.data.model.AadhaarStatus
import com.rcube.app.data.model.Booking
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.data.model.EarningsSummary
import com.rcube.app.data.model.EventType
import com.rcube.app.data.model.MAX_PORTFOLIO
import com.rcube.app.data.model.MediaType
import com.rcube.app.data.model.Mode
import com.rcube.app.data.model.NotificationItem
import com.rcube.app.data.model.PayoutStatus
import com.rcube.app.data.model.PortfolioItem
import com.rcube.app.data.model.ProfileStatus
import com.rcube.app.data.model.Service
import com.rcube.app.core.util.DEFAULT_LAT
import com.rcube.app.core.util.DEFAULT_LNG
import com.rcube.app.core.util.haversineKm
import com.rcube.app.data.local.SessionStore
import com.rcube.app.data.remote.ApiException
import com.rcube.app.data.remote.RcubeApi
import com.rcube.app.data.remote.toDomain
import com.rcube.app.data.seed.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class SessionState(
    val loggedIn: Boolean = false,
    val phone: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val aadhaarStatus: AadhaarStatus = AadhaarStatus.NOT_SUBMITTED,
    val profilePhotoUrl: String? = null,
    val mode: Mode = Mode.CREATOR,
    val needsModeSelection: Boolean = false,
    val needsName: Boolean = false,
) {
    val userName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }
            .joinToString(" ").ifBlank { "You" }

    val isVerified: Boolean get() = aadhaarStatus == AadhaarStatus.VERIFIED
}

/** Discover browse filters, shared between the Discover list and the Filters screen. */
data class DiscoverFilter(
    val category: CreatorCategory? = null,
    val radiusKm: Int = 25,
)

/**
 * Single source of truth for the app.
 *
 * Two interchangeable modes:
 *  - Offline demo (api == null): seeded in-memory data, synchronous mutations.
 *  - Live backend (api != null): data comes from the Apps Script API; mutations
 *    update the UI optimistically, then sync + refresh from the server.
 *
 * The UI depends only on the StateFlows and the public methods, so it is identical
 * in both modes.
 */
class RcubeRepository(
    private val api: RcubeApi? = null,
    private val sessionStore: SessionStore? = null,
) {

    val isBackendConfigured: Boolean get() = api != null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var token: String? = null

    // Device location, used as the organizer's search origin and a new creator's coords.
    private var userLat: Double? = null
    private var userLng: Double? = null

    fun setUserLocation(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
    }

    private val _session = MutableStateFlow(SessionState())
    val session: StateFlow<SessionState> = _session.asStateFlow()

    private val _myProfiles = MutableStateFlow(if (api == null) verifiedSeed(SeedData.myProfiles) else emptyList())
    val myProfiles: StateFlow<List<CreatorProfile>> = _myProfiles.asStateFlow()

    private val _directory = MutableStateFlow(if (api == null) verifiedSeed(SeedData.directory) else emptyList())
    val directory: StateFlow<List<CreatorProfile>> = _directory.asStateFlow()

    private val _creatorBookings = MutableStateFlow(if (api == null) SeedData.creatorBookings else emptyList())
    val creatorBookings: StateFlow<List<Booking>> = _creatorBookings.asStateFlow()

    private val _organizerBookings = MutableStateFlow(if (api == null) SeedData.organizerBookings else emptyList())
    val organizerBookings: StateFlow<List<Booking>> = _organizerBookings.asStateFlow()

    private val _notifications = MutableStateFlow(if (api == null) SeedData.notifications else emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _discoverFilter = MutableStateFlow(DiscoverFilter())
    val discoverFilter: StateFlow<DiscoverFilter> = _discoverFilter.asStateFlow()

    fun setDiscoverFilter(filter: DiscoverFilter) { _discoverFilter.value = filter }

    init {
        val saved = sessionStore?.load()
        if (saved != null) {
            // In backend mode a token is required to be a usable session.
            val usable = if (api != null) !saved.token.isNullOrEmpty() else true
            if (usable) {
                token = saved.token
                _session.value = SessionState(
                    loggedIn = true,
                    phone = saved.phone,
                    firstName = saved.firstName,
                    lastName = saved.lastName,
                    aadhaarStatus = parseAadhaar(saved.aadhaarStatus),
                    mode = saved.mode,
                    needsModeSelection = saved.needsModeSelection,
                    needsName = saved.firstName.isBlank(),
                )
                if (api != null) scope.launch { runCatching { refreshState() } }
            } else {
                sessionStore?.clear()
            }
        }
    }

    private fun persistSession() {
        val s = _session.value
        if (!s.loggedIn) {
            sessionStore?.clear()
            return
        }
        sessionStore?.save(
            phone = s.phone,
            firstName = s.firstName,
            lastName = s.lastName,
            aadhaarStatus = s.aadhaarStatus.name,
            mode = s.mode,
            needsModeSelection = s.needsModeSelection,
            token = token,
        )
    }

    private fun parseAadhaar(name: String): AadhaarStatus =
        runCatching { AadhaarStatus.valueOf(name) }.getOrDefault(AadhaarStatus.NOT_SUBMITTED)

    private fun verifiedSeed(list: List<CreatorProfile>): List<CreatorProfile> =
        list.map { it.copy(ownerVerified = true) }

    // ---- Auth & mode ----------------------------------------------------------------

    fun startLogin(phone: String) {
        _session.value = _session.value.copy(phone = phone)
    }

    /** Verify OTP and sign in. Returns true on success. */
    suspend fun verifyOtp(otp: String): Boolean {
        val a = api
        if (a == null) {
            // Demo: skip identity friction so the seeded experience is rich.
            _session.value = _session.value.copy(
                loggedIn = true,
                firstName = "Arjun",
                lastName = "R.",
                aadhaarStatus = AadhaarStatus.VERIFIED,
                needsModeSelection = true,
                needsName = false,
            )
            persistSession()
            return true
        }
        return try {
            val auth = a.authVerify(_session.value.phone, otp)
            token = auth.token
            val u = auth.user
            _session.value = _session.value.copy(
                loggedIn = true,
                firstName = u.firstName,
                lastName = u.lastName,
                aadhaarStatus = parseAadhaar(u.aadhaarStatus),
                profilePhotoUrl = u.profilePhotoUrl,
                needsModeSelection = true,
                needsName = u.firstName.isBlank(),
            )
            persistSession()
            refreshState()
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Save the user's name (allowed only while identity is not yet verified). */
    fun setName(firstName: String, lastName: String) {
        _session.value = _session.value.copy(
            firstName = firstName.trim(), lastName = lastName.trim(), needsName = false,
        )
        persistSession()
        remoteSync { a, t -> a.setName(t, firstName.trim(), lastName.trim()) }
    }

    /** Set the account profile photo (base64 JPEG). */
    fun setUserPhoto(base64: String, localUri: String) {
        _session.value = _session.value.copy(profilePhotoUrl = localUri)
        remoteSync { a, t -> a.setUserPhoto(t, base64) }
    }

    /** Submit Aadhaar images (base64 JPEG) for identity verification (admin approves). */
    fun submitAadhaar(frontBase64: String?, backBase64: String?) {
        _session.value = _session.value.copy(aadhaarStatus = AadhaarStatus.PENDING_REVIEW)
        persistSession()
        remoteSync { a, t -> a.submitAadhaar(t, frontBase64, backBase64) }
    }

    fun selectInitialMode(mode: Mode) {
        _session.value = _session.value.copy(mode = mode, needsModeSelection = false)
        persistSession()
    }

    fun switchMode() {
        val next = if (_session.value.mode == Mode.CREATOR) Mode.ORGANIZER else Mode.CREATOR
        _session.value = _session.value.copy(mode = next)
        persistSession()
    }

    fun setMode(mode: Mode) {
        _session.value = _session.value.copy(mode = mode)
        persistSession()
    }

    fun logout() {
        token = null
        sessionStore?.clear()
        _session.value = SessionState()
        if (api == null) {
            _myProfiles.value = verifiedSeed(SeedData.myProfiles)
            _directory.value = verifiedSeed(SeedData.directory)
            _creatorBookings.value = SeedData.creatorBookings
            _organizerBookings.value = SeedData.organizerBookings
            _notifications.value = SeedData.notifications
        } else {
            _myProfiles.value = emptyList()
            _directory.value = emptyList()
            _creatorBookings.value = emptyList()
            _organizerBookings.value = emptyList()
            _notifications.value = emptyList()
        }
    }

    private suspend fun refreshState() {
        val a = api ?: return
        val t = token ?: return
        val state = a.getState(t)
        state.me?.let { me ->
            _session.value = _session.value.copy(
                firstName = me.firstName,
                lastName = me.lastName,
                aadhaarStatus = parseAadhaar(me.aadhaarStatus),
                profilePhotoUrl = me.profilePhotoUrl,
                needsName = me.firstName.isBlank(),
            )
            persistSession()
        }
        _myProfiles.value = state.myProfiles.map { it.toDomain() }
        _creatorBookings.value = state.creatorBookings.map { it.toDomain() }
        _organizerBookings.value = state.organizerBookings.map { it.toDomain() }
        _notifications.value = state.notifications.map { it.toDomain() }
    }

    /** Manual refresh (pull-to-refresh). No-op with a brief delay in demo mode. */
    suspend fun refresh() {
        if (api == null) {
            delay(500)
            return
        }
        runCatching { refreshState() }
    }

    /** Fire a server call in the background, then refresh all state from the server. */
    private fun remoteSync(block: suspend (RcubeApi, String) -> Unit) {
        val a = api ?: return
        val t = token ?: return
        scope.launch {
            runCatching { block(a, t) }
            runCatching { refreshState() }
        }
    }

    // ---- Lookups --------------------------------------------------------------------

    fun profileById(id: String): CreatorProfile? =
        _myProfiles.value.firstOrNull { it.id == id }
            ?: _directory.value.firstOrNull { it.id == id }

    fun creatorBookingById(id: String): Booking? =
        _creatorBookings.value.firstOrNull { it.id == id }

    fun organizerBookingById(id: String): Booking? =
        _organizerBookings.value.firstOrNull { it.id == id }

    suspend fun searchCreators(category: CreatorCategory?, radiusKm: Float): List<CreatorProfile> {
        val originLat = userLat ?: DEFAULT_LAT
        val originLng = userLng ?: DEFAULT_LNG
        val a = api
        if (a == null || token == null) {
            // Demo: compute real distance from the device to each seeded creator.
            return _directory.value
                .filter {
                    it.status == ProfileStatus.APPROVED && it.ownerVerified &&
                        (category == null || it.category == category)
                }
                .mapNotNull { p ->
                    if (p.lat == null || p.lng == null) {
                        p // no coords -> keep, unknown distance
                    } else {
                        val d = haversineKm(originLat, originLng, p.lat, p.lng)
                        if (d <= radiusKm) p.copy(distanceKm = d) else null
                    }
                }
                .sortedBy { it.distanceKm ?: Double.MAX_VALUE }
        }
        val results = a.searchCreators(
            token!!, (category ?: CreatorCategory.OTHER).name, radiusKm.toInt(),
            userLat, userLng,
        ).map { it.toDomain() }
        // Cache results so profileById works in the booking flow.
        val existing = _directory.value.associateBy { it.id }.toMutableMap()
        results.forEach { existing[it.id] = it }
        _directory.value = existing.values.toList()
        return results
    }

    // ---- Creator: profiles & services -----------------------------------------------

    suspend fun createProfile(
        displayName: String,
        category: CreatorCategory,
        bio: String,
        city: String,
        languages: List<String>,
        instagram: String?,
        youtube: String?,
        services: List<Service> = emptyList(),
    ): String {
        val a = api
        val t = token
        if (a != null && t != null) {
            val profile = a.createProfile(
                t, displayName, category.name, bio, city, languages,
                instagram, youtube, userLat, userLng, services,
            ).toDomain()
            _myProfiles.value = _myProfiles.value + profile
            return profile.id
        }
        val id = "p_" + UUID.randomUUID().toString().take(6)
        _myProfiles.value = _myProfiles.value + CreatorProfile(
            id = id, ownerUserId = SeedData.MY_USER_ID, displayName = displayName,
            category = category, bio = bio, city = city, languages = languages,
            status = ProfileStatus.DRAFT, services = services,
            instagram = instagram?.ifBlank { null }, youtube = youtube?.ifBlank { null },
            lat = userLat, lng = userLng, ownerVerified = _session.value.isVerified,
        )
        return id
    }

    /** Activate / deactivate a live profile (deactivated = not discoverable). */
    fun setProfileActive(profileId: String, active: Boolean) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(active = active) else it
        }
        remoteSync { a, t -> a.setProfileActive(t, profileId, active) }
    }

    /** Delete a draft/rejected profile. */
    fun deleteProfile(profileId: String) {
        _myProfiles.value = _myProfiles.value.filter { it.id != profileId }
        remoteSync { a, t -> a.deleteProfile(t, profileId) }
    }

    /** Save edits to a profile's reviewable fields. */
    fun updateProfile(
        profileId: String,
        category: CreatorCategory,
        bio: String,
        city: String,
        languages: List<String>,
        instagram: String?,
        youtube: String?,
    ) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(
                category = category, bio = bio, city = city, languages = languages,
                instagram = instagram?.ifBlank { null }, youtube = youtube?.ifBlank { null },
            ) else it
        }
        remoteSync { a, t ->
            a.updateProfile(t, profileId, category.name, bio, city, languages, instagram, youtube)
        }
    }

    fun addService(profileId: String, service: Service) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(services = it.services + service) else it
        }
        remoteSync { a, t ->
            a.addService(t, profileId, service.title, service.pricePaise,
                service.durationMinutes, service.description)
        }
    }

    fun updateService(profileId: String, service: Service) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) {
                it.copy(services = it.services.map { s -> if (s.id == service.id) service else s })
            } else it
        }
        remoteSync { a, t ->
            a.updateService(t, profileId, service.id, service.title, service.pricePaise,
                service.durationMinutes, service.description)
        }
    }

    fun deleteService(profileId: String, serviceId: String) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(services = it.services.filter { s -> s.id != serviceId }) else it
        }
        remoteSync { a, t -> a.deleteService(t, profileId, serviceId) }
    }

    /** Set the profile photo (base64 JPEG). */
    fun setProfilePhoto(profileId: String, base64: String, localUri: String) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(profilePhotoUrl = localUri) else it
        }
        remoteSync { a, t -> a.setProfilePhoto(t, profileId, base64) }
    }

    /** Add a portfolio photo/video (base64, max 3) supporting the skill claim. */
    fun addPortfolioMedia(profileId: String, type: MediaType, base64: String, localUrl: String) {
        _myProfiles.value = _myProfiles.value.map { p ->
            if (p.id == profileId && p.portfolio.size < MAX_PORTFOLIO) {
                val used = p.portfolio.map { it.slot }.toSet()
                val slot = (1..MAX_PORTFOLIO).first { it !in used }
                p.copy(portfolio = p.portfolio + PortfolioItem(slot, type, localUrl, localUrl))
            } else p
        }
        remoteSync { a, t -> a.addPortfolioMedia(t, profileId, type.name, base64) }
    }

    fun deletePortfolioMedia(profileId: String, slot: Int) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(portfolio = it.portfolio.filter { m -> m.slot != slot }) else it
        }
        remoteSync { a, t -> a.deletePortfolioMedia(t, profileId, slot) }
    }

    /** Submit a profile for admin skill/socials review. */
    fun submitProfile(profileId: String) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(status = ProfileStatus.PENDING_REVIEW) else it
        }
        remoteSync { a, t -> a.submitProfile(t, profileId) }
    }

    // ---- Booking transitions --------------------------------------------------------

    suspend fun createBooking(
        profile: CreatorProfile,
        service: Service,
        eventDate: LocalDate,
        eventType: EventType,
        venue: String,
        notes: String,
    ): String {
        val a = api
        val t = token
        if (a != null && t != null) {
            val booking = a.createBooking(
                t, profile.id, service.id, eventDate.toString(),
                eventType.name, venue, notes,
            ).toDomain()
            _organizerBookings.value = listOf(booking) + _organizerBookings.value
            return booking.id
        }
        val id = "ob_" + UUID.randomUUID().toString().take(6)
        _organizerBookings.value = listOf(
            Booking(
                id = id, creatorProfileId = profile.id, creatorName = profile.displayName,
                creatorCategory = profile.category, organizerName = _session.value.userName,
                serviceTitle = service.title, pricePaise = service.pricePaise,
                eventDate = eventDate, eventType = eventType, venue = venue, notes = notes,
                status = BookingStatus.PENDING, createdAt = LocalDateTime.now(),
                acceptExpiresAt = LocalDateTime.now().plusHours(24),
            ),
        ) + _organizerBookings.value
        return id
    }

    fun acceptRequest(bookingId: String) {
        updateCreatorBooking(bookingId) {
            it.copy(status = BookingStatus.PAYMENT_PENDING,
                paymentExpiresAt = LocalDateTime.now().plusHours(24))
        }
        remoteSync { a, t -> a.acceptRequest(t, bookingId) }
    }

    fun declineRequest(bookingId: String) {
        updateCreatorBooking(bookingId) { it.copy(status = BookingStatus.DECLINED) }
        remoteSync { a, t -> a.declineRequest(t, bookingId) }
    }

    /** Creator enters the organizer's OTP to start the event. Returns true on success. */
    suspend fun startEventWithOtp(bookingId: String, otp: String): Boolean {
        val a = api
        val t = token
        if (a != null && t != null) {
            return try {
                val booking = a.startEventWithOtp(t, bookingId, otp).toDomain()
                updateCreatorBooking(bookingId) { booking }
                true
            } catch (e: ApiException) {
                false
            }
        }
        val booking = creatorBookingById(bookingId) ?: return false
        if (booking.status != BookingStatus.CONFIRMED) return false
        if (booking.eventOtp != otp.trim()) return false
        updateCreatorBooking(bookingId) { it.copy(status = BookingStatus.IN_PROGRESS) }
        return true
    }

    fun payAndConfirm(bookingId: String) {
        updateOrganizerBooking(bookingId) {
            it.copy(
                status = BookingStatus.CONFIRMED,
                counterpartyPhone = it.counterpartyPhone ?: ("+91 90080 " + (10000..99999).random()),
                eventOtp = it.eventOtp ?: (1000..9999).random().toString(),
            )
        }
        remoteSync { a, t -> a.payAndConfirm(t, bookingId) }
    }

    fun completeBooking(bookingId: String, rating: Int = 0, comment: String = "") {
        updateOrganizerBooking(bookingId) { it.copy(status = BookingStatus.COMPLETED) }
        remoteSync { a, t -> a.completeBooking(t, bookingId, rating, comment) }
    }

    fun cancelOrganizerBooking(bookingId: String) {
        updateOrganizerBooking(bookingId) { it.copy(status = BookingStatus.CANCELLED) }
        remoteSync { a, t -> a.cancelBooking(t, bookingId) }
    }

    // ---- Earnings -------------------------------------------------------------------

    fun earnings(): EarningsSummary {
        val completed = _creatorBookings.value.filter { it.status == BookingStatus.COMPLETED }
        val pending = completed
            .filter { it.payoutStatus == PayoutStatus.PENDING_TRANSFER || it.payoutStatus == PayoutStatus.TRANSFER_INITIATED }
            .sumOf { it.netPaise }
        val transferred = completed
            .filter { it.payoutStatus == PayoutStatus.TRANSFERRED }
            .sumOf { it.netPaise }
        return EarningsSummary(pending, transferred, completed.sortedByDescending { it.eventDate })
    }

    // ---- Notifications --------------------------------------------------------------

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(read = true) }
        remoteSync { a, t -> a.markNotificationsRead(t) }
    }

    val unreadCount: Int get() = _notifications.value.count { !it.read }

    // ---- helpers --------------------------------------------------------------------

    private fun updateCreatorBooking(id: String, block: (Booking) -> Booking) {
        _creatorBookings.value = _creatorBookings.value.map { if (it.id == id) block(it) else it }
    }

    private fun updateOrganizerBooking(id: String, block: (Booking) -> Booking) {
        _organizerBookings.value = _organizerBookings.value.map { if (it.id == id) block(it) else it }
    }
}
