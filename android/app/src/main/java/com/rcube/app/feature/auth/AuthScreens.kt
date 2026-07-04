package com.rcube.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcube.app.core.designsystem.component.OtpBoxes
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.TextActionButton
import com.rcube.app.core.designsystem.theme.RcubePalette
import com.rcube.app.data.model.Mode
import com.rcube.app.di.LocalAppContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun CubeMark(size: androidx.compose.ui.unit.Dp = 56.dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(RcubePalette.Terracotta, RcubePalette.Amber))),
        contentAlignment = Alignment.Center,
    ) {
        Text("\u25C8", color = androidx.compose.ui.graphics.Color.White, fontSize = (size.value / 2).sp)
    }
}

@Composable
fun PhoneScreen(
    initialPhone: String,
    onContinue: (String) -> Unit,
) {
    var phone by remember { mutableStateOf(initialPhone) }
    val valid = phone.filter { it.isDigit() }.length == 10

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(72.dp))
            CubeMark(64.dp)
            Spacer(Modifier.height(16.dp))
            Text("RCube", style = MaterialTheme.typography.displayMedium)
            Text(
                "the recognition cube",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(48.dp))
            Text(
                "Get recognized for what you quietly practice.",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("+91", style = MaterialTheme.typography.titleMedium) }
                Spacer(Modifier.width(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10 && it.all(Char::isDigit)) phone = it },
                    modifier = Modifier.weight(1f).height(56.dp),
                    placeholder = { Text("98765 43210") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                )
            }

            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = "Continue",
                onClick = { onContinue("+91 " + phone) },
                enabled = valid,
                leadingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "By continuing you agree to our Terms & Privacy Policy.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun OtpScreen(
    phone: String,
    onBack: () -> Unit,
    onVerified: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val scope = rememberCoroutineScope()
    var otp by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(30) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    val complete = otp.length == 6

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
    }

    Scaffold(topBar = { RcubeTopBar(title = "", onBack = onBack) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
        ) {
            Text("Enter the code", style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(8.dp))
            Text(
                "Sent to $phone",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))

            Box {
                OtpBoxes(value = otp, length = 6)
                BasicTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) otp = it },
                    modifier = Modifier
                        .matchParentSize()
                        .focusRequester(focusRequester)
                        .alpha(0f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                )
            }

            Spacer(Modifier.height(20.dp))
            if (secondsLeft > 0) {
                Text(
                    "Resend code in 0:${secondsLeft.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                TextActionButton(text = "Resend code", onClick = { secondsLeft = 30 })
            }

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    error!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(32.dp))
            PrimaryButton(
                text = "Verify",
                enabled = complete && !loading,
                loading = loading,
                onClick = {
                    scope.launch {
                        loading = true
                        error = null
                        val ok = repo.verifyOtp(otp)
                        loading = false
                        if (ok) onVerified() else {
                            error = "We couldn't verify that code. Please try again."
                        }
                    }
                },
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Tip: any 6-digit code works while the backend is in demo mode.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun ModeIntroScreen(
    onModeChosen: (Mode) -> Unit,
) {
    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            Spacer(Modifier.height(24.dp))
            Text("Welcome to RCube", style = MaterialTheme.typography.displaySmall)
            Text(
                "How do you want to start?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))

            ModeCard(
                emoji = "\uD83C\uDFA8",
                title = "I'm a Creator",
                subtitle = "Showcase your talent and get recognized by people nearby.",
                onClick = { onModeChosen(Mode.CREATOR) },
            )
            Spacer(Modifier.height(16.dp))
            ModeCard(
                emoji = "\uD83C\uDF89",
                title = "I'm an Organizer",
                subtitle = "Discover and book local talent for your event.",
                onClick = { onModeChosen(Mode.ORGANIZER) },
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "You can switch anytime — it's one account.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ModeCard(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) { Text(emoji, fontSize = 26.sp) }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary)
    }
}
