package com.rcube.app.feature.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.util.formatLong
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.di.LocalAppContainer

@Composable
fun EventOtpScreen(
    bookingId: String,
    onBack: () -> Unit,
    onCompleted: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val bookings by repo.organizerBookings.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }

    Scaffold(topBar = { RcubeTopBar(title = "Event Code", onBack = onBack) }) { padding ->
        if (booking == null) {
            Text("Booking not found", Modifier.padding(padding).padding(24.dp))
            return@Scaffold
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Share this code with the creator to start the event.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))

            Row(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                (booking.eventOtp ?: "----").forEach { ch ->
                    Text(
                        ch.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "${booking.creatorName} · ${booking.serviceTitle}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                booking.eventDate.formatLong() + " · " + booking.venue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.weight(1f))
            if (booking.status == BookingStatus.CONFIRMED || booking.status == BookingStatus.IN_PROGRESS) {
                Text(
                    "After the performance:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                PrimaryButton(text = "Mark as completed", onClick = {
                    repo.completeBooking(bookingId)
                    onCompleted()
                })
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
