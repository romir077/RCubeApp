package com.rcube.app.feature.creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.BookingStatusPill
import com.rcube.app.core.designsystem.component.DualActionRow
import com.rcube.app.core.designsystem.component.InfoRow
import com.rcube.app.core.designsystem.component.PayoutStatusPill
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.util.formatInr
import com.rcube.app.core.util.formatLong
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.di.LocalAppContainer
import com.rcube.app.feature.common.ContactCard

@Composable
fun RequestDetailScreen(
    bookingId: String,
    onBack: () -> Unit,
    onStartEvent: (String) -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val bookings by repo.creatorBookings.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }

    Scaffold(topBar = { RcubeTopBar(title = "Booking Request", onBack = onBack) }) { padding ->
        if (booking == null) {
            Text("Booking not found", Modifier.padding(padding).padding(24.dp))
            return@Scaffold
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                BookingStatusPill(booking.status)
            }
            Spacer(Modifier.height(16.dp))

            RcubeCard(modifier = Modifier.fillMaxWidth()) {
                Text("Service", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(booking.serviceTitle, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(formatInr(booking.pricePaise),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(16.dp))
            SectionHeader("Event")
            Spacer(Modifier.height(8.dp))
            RcubeCard(modifier = Modifier.fillMaxWidth()) {
                InfoRow("Date", booking.eventDate.formatLong())
                InfoRow("Type", booking.eventType.label)
                InfoRow("Venue", booking.venue)
                if (booking.notes.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Notes", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("\"${booking.notes}\"", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionHeader("Organizer")
            Spacer(Modifier.height(8.dp))
            if (booking.status.contactRevealed && booking.counterpartyPhone != null) {
                ContactCard(name = booking.organizerName, phone = booking.counterpartyPhone)
            } else {
                RcubeCard(modifier = Modifier.fillMaxWidth()) {
                    Text(booking.organizerName, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Contact is shared once you accept and the organizer pays.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (booking.payoutStatus != null) {
                Spacer(Modifier.height(16.dp))
                SectionHeader("Earning")
                Spacer(Modifier.height(8.dp))
                RcubeCard(modifier = Modifier.fillMaxWidth()) {
                    InfoRow("You earn (after 15% fee)", formatInr(booking.netPaise))
                    Spacer(Modifier.height(8.dp))
                    PayoutStatusPill(booking.payoutStatus)
                }
            }

            Spacer(Modifier.height(24.dp))
            when (booking.status) {
                BookingStatus.PENDING -> DualActionRow(
                    secondaryText = "Decline",
                    onSecondary = { repo.declineRequest(booking.id); onBack() },
                    primaryText = "Accept",
                    onPrimary = { repo.acceptRequest(booking.id) },
                )
                BookingStatus.PAYMENT_PENDING -> InfoNote(
                    "You've accepted. Waiting for the organizer to pay — you'll get their contact once they do.")
                BookingStatus.CONFIRMED -> PrimaryButton(
                    text = "Start event with OTP",
                    onClick = { onStartEvent(booking.id) },
                    leadingIcon = Icons.Filled.PlayArrow,
                )
                BookingStatus.IN_PROGRESS -> InfoNote(
                    "The event is in progress. The organizer will mark it complete afterwards.")
                else -> {}
            }
        }
    }
}

@Composable
private fun InfoNote(text: String) {
    RcubeCard(modifier = Modifier.fillMaxWidth()) {
        Text(text, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
