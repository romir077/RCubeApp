package com.rcube.app.navigation

import kotlinx.serialization.Serializable

/** Type-safe navigation routes (Navigation-Compose 2.8 + kotlinx.serialization). */

// ---- Auth ----
@Serializable data object PhoneRoute
@Serializable data object OtpRoute
@Serializable data object NameRoute
@Serializable data object ModeIntroRoute

// ---- Creator tabs ----
@Serializable data object CreatorProfilesRoute
@Serializable data object CreatorRequestsRoute
@Serializable data object EarningsRoute

// ---- Organizer tabs ----
@Serializable data object DiscoverRoute
@Serializable data object MyBookingsRoute
@Serializable data object FiltersRoute

// ---- Shared ----
@Serializable data object AccountRoute
@Serializable data object NotificationsRoute
@Serializable data object SupportRoute
@Serializable data object TermsRoute

// ---- Creator details ----
@Serializable data class RequestDetailRoute(val bookingId: String)
@Serializable data class StartEventRoute(val bookingId: String)
@Serializable data class ProfileEditorRoute(val profileId: String? = null)
@Serializable data class ServiceEditorRoute(
    val profileId: String,
    val serviceId: String? = null,
)
@Serializable data class ProfilePreviewRoute(val profileId: String)

// ---- Organizer details ----
@Serializable data class CreatorPublicRoute(
    val profileId: String,
    val eventType: String? = null,
)
@Serializable data class BookingFormRoute(
    val profileId: String,
    val serviceId: String,
    val eventType: String,
)
@Serializable data class OrganizerBookingDetailRoute(val bookingId: String)
@Serializable data class PaymentRoute(val bookingId: String)
@Serializable data class EventOtpRoute(val bookingId: String)
