package com.rcube.app.data.model

import java.time.LocalDate
import java.time.LocalDateTime

/** Portfolio media (photo/video), max 3 per profile, stored in slots 1..3. */
data class PortfolioItem(
    val slot: Int,
    val type: MediaType,
    val thumbUrl: String,
    val fullUrl: String,
)

const val MAX_PORTFOLIO = 3

/** An organizer's rating + comment left after an event completes. */
data class Review(
    val organizerName: String,
    val rating: Int,
    val comment: String,
)

/** A fixed-price offering under a profile (Bible §16.3 — no hourly pricing). */
data class Service(
    val id: String,
    val title: String,
    val pricePaise: Long,
    val durationMinutes: Int? = null,
    val description: String? = null,
)

/** A single creator identity; a user may own several (Bible FR-PROF-1). */
data class CreatorProfile(
    val id: String,
    val ownerUserId: String,
    val displayName: String,
    val category: CreatorCategory,
    val bio: String,
    val city: String,
    val languages: List<String>,
    val status: ProfileStatus,
    val services: List<Service>,
    val portfolio: List<PortfolioItem> = emptyList(),
    val profilePhotoUrl: String? = null,
    val instagram: String? = null,
    val youtube: String? = null,
    val rejectionReason: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val distanceKm: Double? = null,
    val completedBookings: Int = 0,
    val ownerVerified: Boolean = false,
    val active: Boolean = true,
    val ratingAvg: Double? = null,
    val ratingCount: Int = 0,
    val reviews: List<Review> = emptyList(),
) {
    /** Identity (Aadhaar) verified — drives the "Verified" tick. */
    val isVerified: Boolean get() = ownerVerified

    /** Published and discoverable by organizers (subject also to owner verification). */
    val isLive: Boolean get() = status == ProfileStatus.APPROVED && active
    val startingPricePaise: Long? get() = services.minOfOrNull { it.pricePaise }
}

/** The core lifecycle object (Bible §23.4). */
data class Booking(
    val id: String,
    val creatorProfileId: String,
    val creatorName: String,
    val creatorCategory: CreatorCategory,
    val organizerName: String,
    val serviceTitle: String,
    val pricePaise: Long,
    val eventDate: LocalDate,
    val eventType: EventType,
    val venue: String,
    val notes: String,
    val status: BookingStatus,
    val createdAt: LocalDateTime,
    val acceptExpiresAt: LocalDateTime? = null,
    val paymentExpiresAt: LocalDateTime? = null,
    val eventOtp: String? = null,
    val counterpartyPhone: String? = null,
    val payoutStatus: PayoutStatus? = null,
    val commissionPct: Int = 15,
) {
    val commissionPaise: Long get() = pricePaise * commissionPct / 100
    val netPaise: Long get() = pricePaise - commissionPaise
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timeLabel: String,
    val read: Boolean,
)

data class EarningsSummary(
    val pendingTransferPaise: Long,
    val transferredTotalPaise: Long,
    val items: List<Booking>,
)
