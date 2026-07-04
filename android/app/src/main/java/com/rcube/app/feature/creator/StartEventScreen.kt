package com.rcube.app.feature.creator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.OtpBoxes
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
    val bookings by repo.creatorBookings.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }
    val keyboard = LocalSoftwareKeyboardController.current

    var otp by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Scaffold(topBar = { RcubeTopBar(title = "Start Event", onBack = onBack) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
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

            Box {
                OtpBoxes(value = otp, length = 4)
                BasicTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 4 && it.all(Char::isDigit)) {
                            otp = it; error = false
                        }
                    },
                    modifier = Modifier
                        .matchParentSize()
                        .focusRequester(focusRequester)
                        .alpha(0f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                )
            }

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
                text = "Start performance",
                enabled = otp.length == 4,
                onClick = {
                    keyboard?.hide()
                    if (repo.startEventWithOtp(bookingId, otp)) onStarted() else error = true
                },
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Tip: in this demo the code for a confirmed booking is 4291.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
