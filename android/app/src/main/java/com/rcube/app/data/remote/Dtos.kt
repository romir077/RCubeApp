package com.rcube.app.data.remote

import kotlinx.serialization.Serializable

/** Wire models mirroring the Google Apps Script backend JSON (see backend/Code.gs). */

@Serializable
data class AuthDto(
    val token: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val phone: String,
    val displayName: String,
    val defaultMode: String = "creator",
)

@Serializable
data class ServiceDto(
    val id: String,
    val title: String,
    val pricePaise: Long,
    val durationMinutes: Int? = null,
    val description: String? = null,
)

@Serializable
data class ProfileDto(
    val id: String,
    val ownerUserId: String,
    val displayName: String,
    val category: String,
    val bio: String = "",
    val city: String = "",
    val languages: List<String> = emptyList(),
    val status: String,
    val services: List<ServiceDto> = emptyList(),
    val instagram: String? = null,
    val youtube: String? = null,
    val rejectionReason: String? = null,
    val distanceKm: Double? = null,
    val completedBookings: Int = 0,
)

@Serializable
data class BookingDto(
    val id: String,
    val creatorProfileId: String,
    val creatorName: String,
    val creatorCategory: String,
    val organizerName: String,
    val serviceTitle: String,
    val pricePaise: Long,
    val eventDate: String,
    val eventType: String,
    val venue: String = "",
    val notes: String = "",
    val status: String,
    val createdAt: Long,
    val acceptExpiresAt: Long? = null,
    val paymentExpiresAt: Long? = null,
    val eventOtp: String? = null,
    val counterpartyPhone: String? = null,
    val payoutStatus: String? = null,
    val commissionPct: Int = 15,
)

@Serializable
data class NotificationDto(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val read: Boolean = false,
    val createdAt: Long,
)

@Serializable
data class StateDto(
    val myProfiles: List<ProfileDto> = emptyList(),
    val creatorBookings: List<BookingDto> = emptyList(),
    val organizerBookings: List<BookingDto> = emptyList(),
    val notifications: List<NotificationDto> = emptyList(),
)
