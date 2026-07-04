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
import com.rcube.app.data.seed.SeedData
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
 * Single in-memory store backing the whole app. Mirrors the server-side rules from the
 * Bible (state machine §19, contact reveal BR-18) so the UI behaves realistically.
 * Swap this for API-backed repositories later; the ViewModels won't change.
 */
class RcubeRepository {

    private val _session = MutableStateFlow(SessionState())
    val session: StateFlow<SessionState> = _session.asStateFlow()

    private val _myProfiles = MutableStateFlow(SeedData.myProfiles)
    val myProfiles: StateFlow<List<CreatorProfile>> = _myProfiles.asStateFlow()

    private val _directory = MutableStateFlow(SeedData.directory)
    val directory: StateFlow<List<CreatorProfile>> = _directory.asStateFlow()

    private val _creatorBookings = MutableStateFlow(SeedData.creatorBookings)
    val creatorBookings: StateFlow<List<Booking>> = _creatorBookings.asStateFlow()

    private val _organizerBookings = MutableStateFlow(SeedData.organizerBookings)
    val organizerBookings: StateFlow<List<Booking>> = _organizerBookings.asStateFlow()

    private val _notifications = MutableStateFlow(SeedData.notifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // ---- Auth & mode ----------------------------------------------------------------

    fun startLogin(phone: String) {
        _session.value = _session.value.copy(phone = phone)
    }

    /** Verify OTP. For the demo any 6-digit code succeeds; routes to mode selection. */
    fun verifyOtpAndSignIn() {
        _session.value = _session.value.copy(loggedIn = true, needsModeSelection = true)
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
        _session.value = SessionState()
    }

    // ---- Lookups --------------------------------------------------------------------

    fun profileById(id: String): CreatorProfile? =
        _myProfiles.value.firstOrNull { it.id == id }
            ?: _directory.value.firstOrNull { it.id == id }

    fun creatorBookingById(id: String): Booking? =
        _creatorBookings.value.firstOrNull { it.id == id }

    fun organizerBookingById(id: String): Booking? =
        _organizerBookings.value.firstOrNull { it.id == id }

    fun searchDirectory(category: CreatorCategory?, radiusKm: Float): List<CreatorProfile> =
        _directory.value.filter { p ->
            p.status == ProfileStatus.APPROVED &&
                (category == null || p.category == category) &&
                (p.distanceKm ?: 0.0) <= radiusKm
        }.sortedBy { it.distanceKm ?: Double.MAX_VALUE }

    // ---- Creator: profiles & services -----------------------------------------------

    fun createProfile(
        displayName: String,
        category: CreatorCategory,
        bio: String,
        city: String,
        languages: List<String>,
    ): String {
        val id = "p_" + UUID.randomUUID().toString().take(6)
        val profile = CreatorProfile(
            id = id, ownerUserId = SeedData.MY_USER_ID, displayName = displayName,
            category = category, bio = bio, city = city, languages = languages,
            status = ProfileStatus.DRAFT, services = emptyList(),
        )
        _myProfiles.value = _myProfiles.value + profile
        return id
    }

    fun addService(profileId: String, service: Service) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(services = it.services + service) else it
        }
    }

    fun submitProfile(profileId: String) {
        _myProfiles.value = _myProfiles.value.map {
            if (it.id == profileId) it.copy(status = ProfileStatus.PENDING_REVIEW) else it
        }
    }

    // ---- Booking transitions (mirrors §19.1) ----------------------------------------

    fun createBooking(
        profile: CreatorProfile,
        service: Service,
        eventDate: LocalDate,
        eventType: EventType,
        venue: String,
        notes: String,
    ): String {
        val id = "ob_" + UUID.randomUUID().toString().take(6)
        val booking = Booking(
            id = id, creatorProfileId = profile.id, creatorName = profile.displayName,
            creatorCategory = profile.category, organizerName = _session.value.userName,
            serviceTitle = service.title, pricePaise = service.pricePaise,
            eventDate = eventDate, eventType = eventType, venue = venue, notes = notes,
            status = BookingStatus.PENDING, createdAt = LocalDateTime.now(),
            acceptExpiresAt = LocalDateTime.now().plusHours(24),
        )
        _organizerBookings.value = listOf(booking) + _organizerBookings.value
        return id
    }

    fun acceptRequest(bookingId: String) = updateCreatorBooking(bookingId) {
        it.copy(
            status = BookingStatus.PAYMENT_PENDING,
            paymentExpiresAt = LocalDateTime.now().plusHours(24),
        )
    }

    fun declineRequest(bookingId: String) = updateCreatorBooking(bookingId) {
        it.copy(status = BookingStatus.DECLINED)
    }

    /** Creator enters the organizer's OTP to start the event (Confirmed -> In Progress). */
    fun startEventWithOtp(bookingId: String, otp: String): Boolean {
        val booking = creatorBookingById(bookingId) ?: return false
        if (booking.status != BookingStatus.CONFIRMED) return false
        if (booking.eventOtp != otp.trim()) return false
        updateCreatorBooking(bookingId) { it.copy(status = BookingStatus.IN_PROGRESS) }
        return true
    }

    /** Organizer pays -> Confirmed. Contacts revealed for BOTH parties, Event OTP generated. */
    fun payAndConfirm(bookingId: String) = updateOrganizerBooking(bookingId) {
        it.copy(
            status = BookingStatus.CONFIRMED,
            counterpartyPhone = "+91 90080 " + (10000..99999).random(),
            eventOtp = (1000..9999).random().toString(),
        )
    }

    /** Organizer marks the event completed -> creator earning becomes Pending Transfer. */
    fun completeBooking(bookingId: String) = updateOrganizerBooking(bookingId) {
        it.copy(status = BookingStatus.COMPLETED)
    }

    fun cancelOrganizerBooking(bookingId: String) = updateOrganizerBooking(bookingId) {
        it.copy(status = BookingStatus.CANCELLED)
    }

    // ---- Earnings (creator-side, from completed bookings) ---------------------------

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
