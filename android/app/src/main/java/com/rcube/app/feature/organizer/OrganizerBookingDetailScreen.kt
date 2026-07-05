package com.rcube.app.feature.organizer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.BookingStatusPill
import com.rcube.app.core.designsystem.component.InfoRow
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTextField
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SecondaryButton
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.designsystem.theme.RcubeTheme
import com.rcube.app.core.util.formatInr
import com.rcube.app.core.util.formatLong
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.di.LocalAppContainer
import com.rcube.app.feature.common.ContactCard

@Composable
fun OrganizerBookingDetailScreen(
    bookingId: String,
    onBack: () -> Unit,
    onPay: (String) -> Unit,
    onShowEventCode: (String) -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val bookings by repo.organizerBookings.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }
    var showRating by remember { mutableStateOf(false) }

    Scaffold(topBar = { RcubeTopBar(title = "Booking", onBack = onBack) }) { padding ->
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
            BookingStatusPill(booking.status)
            Spacer(Modifier.height(16.dp))

            RcubeCard(modifier = Modifier.fillMaxWidth()) {
                Text(booking.creatorName + " · " + booking.creatorCategory.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
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
                    Text("\"${booking.notes}\"", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (booking.status.contactRevealed && booking.counterpartyPhone != null) {
                Spacer(Modifier.height(16.dp))
                SectionHeader("Creator contact")
                Spacer(Modifier.height(8.dp))
                ContactCard(name = booking.creatorName, phone = booking.counterpartyPhone)
            }

            Spacer(Modifier.height(24.dp))
            when (booking.status) {
                BookingStatus.PENDING -> {
                    InfoNote("Waiting for ${booking.creatorName} to accept your request.")
                    Spacer(Modifier.height(12.dp))
                    SecondaryButton("Cancel request",
                        onClick = { repo.cancelOrganizerBooking(booking.id); onBack() })
                }
                BookingStatus.PAYMENT_PENDING -> PrimaryButton(
                    text = "Pay ${formatInr(booking.pricePaise)} to confirm",
                    onClick = { onPay(booking.id) },
                    leadingIcon = Icons.Filled.Payment,
                )
                BookingStatus.CONFIRMED -> PrimaryButton(
                    text = "Show event code",
                    onClick = { onShowEventCode(booking.id) },
                    leadingIcon = Icons.Filled.Password,
                )
                BookingStatus.IN_PROGRESS -> PrimaryButton(
                    text = "Mark as completed",
                    onClick = { showRating = true },
                    leadingIcon = Icons.Filled.CheckCircle,
                )
                BookingStatus.COMPLETED -> InfoNote(
                    "This event is complete. Thanks for recognizing local talent \uD83C\uDF89")
                else -> {}
            }
        }
    }

    if (showRating && booking != null) {
        RatingDialog(
            creatorName = booking.creatorName,
            onDismiss = { showRating = false },
            onSubmit = { rating, comment ->
                repo.completeBooking(booking.id, rating, comment)
                showRating = false
            },
        )
    }
}

@Composable
private fun RatingDialog(
    creatorName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit,
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate $creatorName") },
        text = {
            Column {
                Text(
                    "Your rating and comment appear on their profile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    (1..5).forEach { star ->
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "$star star",
                            tint = if (star <= rating) RcubeTheme.semantic.warning
                            else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(36.dp).clickable { rating = star },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                RcubeTextField(
                    "Comment (optional)", comment, { comment = it },
                    singleLine = false, minLines = 2,
                    placeholder = "How was the experience?",
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(rating, comment.trim()) }) { Text("Submit & complete") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun InfoNote(text: String) {
    RcubeCard(modifier = Modifier.fillMaxWidth()) {
        Text(text, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
