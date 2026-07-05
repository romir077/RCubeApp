package com.rcube.app.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Labeled outlined text field with RCube styling. */
@Composable
fun RcubeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    Column(modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            placeholder = placeholder?.let { { Text(it) } },
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(14.dp),
            prefix = prefix?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
        )
        if (supportingText != null) {
            Text(
                supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
            )
        }
    }
}

/**
 * Segmented OTP input. A single [BasicTextField] renders the boxes via its
 * decorationBox, so tapping anywhere focuses it and the keyboard appears reliably.
 * Auto-focuses and shows the IME on first display.
 */
@Composable
fun OtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = true,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var focused by remember { mutableStateOf(false) }

    val caretAlpha by rememberInfiniteTransition(label = "otpCaret").animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "otpCaretAlpha",
    )

    LaunchedEffect(Unit) {
        if (autoFocus) {
            focusRequester.requestFocus()
            delay(150) // let layout settle so the IME actually opens
            keyboard?.show()
        }
    }

    BasicTextField(
        value = value,
        onValueChange = { new -> onValueChange(new.filter(Char::isDigit).take(length)) },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focused = it.isFocused },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
        ),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(length) { index ->
                    val ch = value.getOrNull(index)?.toString() ?: ""
                    val filled = ch.isNotEmpty()
                    val isActive = focused && index == value.length
                    Box(
                        modifier = Modifier
                            .size(width = 46.dp, height = 56.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .border(
                                width = if (filled || isActive) 2.dp else 1.dp,
                                color = if (filled || isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (filled) {
                            Text(ch, style = MaterialTheme.typography.headlineSmall)
                        } else if (isActive) {
                            // Blinking caret in the box awaiting input.
                            Box(
                                Modifier
                                    .alpha(caretAlpha)
                                    .size(width = 2.dp, height = 26.dp)
                                    .background(MaterialTheme.colorScheme.primary),
                            )
                        }
                    }
                }
            }
        },
    )
}
