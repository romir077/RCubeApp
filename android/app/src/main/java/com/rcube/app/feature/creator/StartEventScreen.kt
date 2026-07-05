package com.rcube.app.feature.creator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.OtpInput
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.util.formatLong
import com.rcube.app.di.LocalAppContainer

@Composable
fun StartEventScreen(
    bookingId: String,
    onBack: () -> Unit,
    onStarted: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val scope = rememberCoroutineScope()
    val bookings by repo.creatorBookings.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }
    val keyboard = LocalSoftwareKeyboardController.current

    var otp by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var starting by remember { mutableStateOf(false) }

    Scaffold(topBar = { RcubeTopBar(title = "Start Event", onBack = onBack) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .imePadding(),
        ) {
            if (booking != null) {
                Text(booking.serviceTitle, style = MaterialTheme.typography.headlineSmall)
                Text(
                    booking.eventDate.formatLong() + " · " + booking.venue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "Ask the organizer for the event code and enter it to begin your performance.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(24.dp))

            OtpInput(
                value = otp,
                onValueChange = { otp = it; error = false },
                length = 4,
            )

            if (error) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "That code didn't match. Double-check with the organizer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = "Let's go",
                enabled = otp.length == 4 && !starting,
                loading = starting,
                onClick = {
                    keyboard?.hide()
                    scope.launch {
                        starting = true
                        val ok = repo.startEventWithOtp(bookingId, otp)
                        starting = false
                        if (ok) {
                            onStarted()
                        } else {
                            error = true
                            otp = "" // wrong code: clear and return cursor to the first box
                        }
                    }
                },
            )
            Spacer(Modifier.height(12.dp))
            Text(
                if (repo.isBackendConfigured) {
                    "The organizer sees this 4-digit code in their app."
                } else {
                    "Tip: in demo mode the code for the confirmed booking is 4291."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
