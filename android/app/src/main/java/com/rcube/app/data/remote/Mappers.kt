package com.rcube.app.data.remote

import com.rcube.app.data.model.Booking
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.data.model.EventType
import com.rcube.app.data.model.NotificationItem
import com.rcube.app.data.model.NotificationType
import com.rcube.app.data.model.PayoutStatus
import com.rcube.app.data.model.ProfileStatus
import com.rcube.app.data.model.Service
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

private fun parseCategory(name: String): CreatorCategory =
    runCatching { CreatorCategory.valueOf(name) }.getOrDefault(CreatorCategory.OTHER)

private fun parseEventType(name: String): EventType =
    runCatching { EventType.valueOf(name) }.getOrDefault(EventType.OTHER)

private fun parseBookingStatus(name: String): BookingStatus =
    runCatching { BookingStatus.valueOf(name) }.getOrDefault(BookingStatus.PENDING)

private fun parseProfileStatus(name: String): ProfileStatus =
    runCatching { ProfileStatus.valueOf(name) }.getOrDefault(ProfileStatus.DRAFT)

fun ServiceDto.toDomain() = Service(
    id = id,
    title = title,
    pricePaise = pricePaise,
    durationMinutes = durationMinutes,
    description = description,
)

fun ProfileDto.toDomain() = CreatorProfile(
    id = id,
    ownerUserId = ownerUserId,
    displayName = displayName,
    category = parseCategory(category),
    bio = bio,
    city = city,
    languages = languages,
    status = parseProfileStatus(status),
    services = services.map { it.toDomain() },
    instagram = instagram,
    youtube = youtube,
    rejectionReason = rejectionReason,
    distanceKm = distanceKm,
    completedBookings = completedBookings,
)

fun BookingDto.toDomain() = Booking(
    id = id,
    creatorProfileId = creatorProfileId,
    creatorName = creatorName,
    creatorCategory = parseCategory(creatorCategory),
    organizerName = organizerName,
    serviceTitle = serviceTitle,
    pricePaise = pricePaise,
    eventDate = runCatching { LocalDate.parse(eventDate) }.getOrElse { LocalDate.now() },
    eventType = parseEventType(eventType),
    venue = venue,
    notes = notes,
    status = parseBookingStatus(status),
    createdAt = createdAt.toLocalDateTime(),
    acceptExpiresAt = acceptExpiresAt?.toLocalDateTime(),
    paymentExpiresAt = paymentExpiresAt?.toLocalDateTime(),
    eventOtp = eventOtp,
    counterpartyPhone = counterpartyPhone,
    payoutStatus = payoutStatus?.let { runCatching { PayoutStatus.valueOf(it) }.getOrNull() },
    commissionPct = commissionPct,
)

fun NotificationDto.toDomain() = NotificationItem(
    id = id,
    type = runCatching { NotificationType.valueOf(type) }.getOrDefault(NotificationType.BOOKING),
    title = title,
    body = body,
    timeLabel = relativeTime(createdAt),
    read = read,
)

private fun relativeTime(millis: Long): String {
    val now = System.currentTimeMillis()
    val diffMin = ChronoUnit.MINUTES.between(
        Instant.ofEpochMilli(millis), Instant.ofEpochMilli(now),
    )
    return when {
        diffMin < 1 -> "just now"
        diffMin < 60 -> "${diffMin}m ago"
        diffMin < 1440 -> "${diffMin / 60}h ago"
        else -> "${diffMin / 1440}d ago"
    }
}
