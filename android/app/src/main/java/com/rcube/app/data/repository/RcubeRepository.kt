package com.rcube.app.data.repository

import com.rcube.app.data.model.Booking
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.data.model.EarningsSummary
import com.rcube.app.data.model.EventType
import com.rcube.app.data.model.Mode
import com.rcube.app.data.model.NotificationItem
import com.rcube.app.data.model.PayoutStatus
import com.rcube.app.data.model.ProfileStatus
import com.rcube.app.data.model.Service
import com.rcube.app.data.remote.ApiException
import com.rcube.app.data.remote.RcubeApi
import com.rcube.app.data.remote.toDomain
import com.rcube.app.data.seed.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    val userName: String = SeedData.MY_NAME,
    val mode: Mode = Mode.CREATOR,
    val needsModeSelection: Boolean = false,
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
class RcubeRepository(private val api: RcubeApi? = null) {

    val isBackendConfigured: Boolean get() = api != null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var token: String? = null

    private val _session = MutableStateFlow(SessionState())
    val session: StateFlow<SessionState> = _session.asStateFlow()

    private val _myProfiles = MutableStateFlow(if (api == null) SeedData.myProfiles else emptyList())
    val myProfiles: StateFlow<List<CreatorProfile>> = _myProfiles.asStateFlow()

    private val _directory = MutableStateFlow(if (api == null) SeedData.directory else emptyList())
    val directory: StateFlow<List<CreatorProfile>> = _directory.asStateFlow()

    private val _creatorBookings = MutableStateFlow(if (api == null) SeedData.creatorBookings else emptyList())
    val creatorBookings: StateFlow<List<Booking>> = _creatorBookings.asStateFlow()

    private val _organizerBookings = MutableStateFlow(if (api == null) SeedData.organizerBookings else emptyList())
    val organizerBookings: StateFlow<List<Booking>> = _organizerBookings.asStateFlow()

    private val _notifications = MutableStateFlow(if (api == null) SeedData.notifications else emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // ---- Auth & mode ----------------------------------------------------------------

    fun startLogin(phone: String) {
        _session.value = _session.value.copy(phone = phone)
    }

    /** Verify OTP and sign in. Returns true on success. */
    suspend fun verifyOtp(otp: String): Boolean {
        val a = api
        if (a == null) {
            _session.value = _session.value.copy(loggedIn = true, needsModeSelection = true)
            return true
        }
        return try {
            val auth = a.authVerify(_session.value.phone, otp)
            token = auth.token
            _session.value = _session.value.copy(
                loggedIn = true,
                needsModeSelection = true,
                userName = auth.user.displayName,
            )
            refreshState()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun selectInitialMode(mode: Mode) {
        _session.value = _session.value.copy(mode = mode, needsModeSelection = false)
    }

    fun switchMode() {
        val next = if (_session.value.mode == Mode.CREATOR) Mode.ORGANIZER else Mode.CREATOR
        _session.value = _session.value.copy(mode = next)
    }

    fun setMode(mode: Mode) {
        _session.value = _session.value.copy(mode = mode)
    }

    fun logout() {
        token = null
        _session.value = SessionState()
        if (api == null) {
            _myProfiles.value = SeedData.myProfiles
            _directory.value = SeedData.directory
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
        _myProfiles.value = state.myProfiles.map { it.toDomain() }
        _creatorBookings.value = state.creatorBookings.map { it.toDomain() }
        _organizerBookings.value = state.organizerBookings.map { it.toDomain() }
        _notifications.value = state.notifications.map { it.toDomain() }
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
        val a = api
        if (a == null || token == null) {
            return _directory.value.filter { p ->
                p.status == ProfileStatus.APPROVED &&
                    (category == null || p.category == category) &&
                    (p.distanceKm ?: 0.0) <= radiusKm
            }.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
        }
        val results = a.searchCreators(token!!, (category ?: CreatorCategory.OTHER).name, radiusKm.toInt())
            .map { it.toDomain() }
        // Cache results so profileById works in the booking flow.
        val existing = _directory.value.associateBy { it.id }.toMutableMap()
        results.forEach { existing[it.id] = it }
        _directory.value = existing.values.toList()
        return results
    }

    // ---- Creator: profiles & services -----------------------------------------------

    fun createProfile(
        displayName: String,
        category: CreatorCategory,
        bio: String,
        city: String,
        languages: List<String>,
    ): String {
        val id = "p_" + UUID.randomUUID().toString().take(6)
        _myProfiles.value = _myProfiles.value + CreatorProfile(
            id = id, ownerUserId = SeedData.MY_USER_ID, displayName = displayName,
            category = category, bio = bio, city = city, languages = languages,
            status = ProfileStatus.DRAFT, services = emptyList(),
        )
        remoteSync { a, t -> a.createProfile(t, displayName, category.name, bio, city, languages) }
        return id
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

    fun completeBooking(bookingId: String) {
        updateOrganizerBooking(bookingId) { it.copy(status = BookingStatus.COMPLETED) }
        remoteSync { a, t -> a.completeBooking(t, bookingId) }
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
