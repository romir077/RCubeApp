package com.rcube.app.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcube.app.core.designsystem.component.BookingStatusPill
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.util.formatCountdown
import com.rcube.app.core.util.formatInr
import com.rcube.app.core.util.formatLong
import com.rcube.app.core.util.relativeLabel
import com.rcube.app.data.model.Booking
import com.rcube.app.data.model.BookingStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun BookingListCard(
    booking: Booking,
    asCreator: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val counterparty = if (asCreator) booking.organizerName else booking.creatorName
    RcubeCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text(booking.creatorCategory.emoji, fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    booking.serviceTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
                Text(
                    (if (asCreator) "from " else "with ") + counterparty,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            BookingStatusPill(booking.status)
        }

        Spacer(Modifier.height(12.dp))
        IconLine(Icons.Filled.CalendarMonth,
            booking.eventDate.formatLong() + " · " + booking.eventDate.relativeLabel())
        Spacer(Modifier.height(4.dp))
        IconLine(Icons.Filled.Place, booking.venue)

        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                formatInr(booking.pricePaise),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            BookingFooterHint(booking)
        }
    }
}

@Composable
private fun BookingFooterHint(booking: Booking) {
    when (booking.status) {
        BookingStatus.PENDING -> {
            val exp = booking.acceptExpiresAt
            if (exp != null) {
                val mins = ChronoUnit.MINUTES.between(LocalDateTime.now(), exp)
                HintChip(Icons.Filled.Timer, "Expires in " + formatCountdown(mins),
                    MaterialTheme.colorScheme.error)
            }
        }
        BookingStatus.PAYMENT_PENDING -> {
            val exp = booking.paymentExpiresAt
            if (exp != null) {
                val mins = ChronoUnit.MINUTES.between(LocalDateTime.now(), exp)
                HintChip(Icons.Filled.Timer, "Pay in " + formatCountdown(mins),
                    MaterialTheme.colorScheme.error)
            }
        }
        BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS ->
            HintChip(Icons.Filled.Phone, "Contact available",
                MaterialTheme.colorScheme.tertiary)
        else -> {}
    }
}

@Composable
private fun HintChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = tint)
    }
}

/** Card revealing counterparty contact after a booking is confirmed (Bible BR-18). */
@Composable
fun ContactCard(
    name: String,
    phone: String,
    modifier: Modifier = Modifier,
) {
    RcubeCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Phone, contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text(phone, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Coordinate the exact timing together over a call or message.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun IconLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
