package com.rcube.app.feature.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.InfoRow
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.util.formatInr
import com.rcube.app.core.util.formatLong
import com.rcube.app.di.LocalAppContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    bookingId: String,
    onBack: () -> Unit,
    onPaid: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val bookings by repo.organizerBookings.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }
    val scope = rememberCoroutineScope()
    var processing by remember { mutableStateOf(false) }

    Scaffold(topBar = { RcubeTopBar(title = "Confirm & Pay", onBack = onBack) }) { padding ->
        if (booking == null) {
            Text("Booking not found", Modifier.padding(padding).padding(24.dp))
            return@Scaffold
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            RcubeCard(modifier = Modifier.fillMaxWidth()) {
                Text("${booking.creatorName} · ${booking.serviceTitle}",
                    style = MaterialTheme.typography.titleMedium)
                Text(booking.eventDate.formatLong() + " · " + booking.venue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(16.dp))
            RcubeCard(modifier = Modifier.fillMaxWidth()) {
                Text("Price breakdown", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                InfoRow("Service price", formatInr(booking.pricePaise))
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
                    Text("Total payable", style = MaterialTheme.typography.titleMedium)
                    Text(formatInr(booking.pricePaise),
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(Modifier.height(0.dp))
                Text(
                    "  Your payment is held safely and released to the creator after the event. " +
                        "Contact details are shared right after payment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }

            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = "Pay ${formatInr(booking.pricePaise)}",
                loading = processing,
                onClick = {
                    processing = true
                    scope.launch {
                        delay(1200) // simulate Razorpay checkout
                        repo.payAndConfirm(bookingId)
                        processing = false
                        onPaid()
                    }
                },
            )
        }
    }
}
