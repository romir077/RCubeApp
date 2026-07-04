package com.rcube.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rcube.app.core.designsystem.theme.RcubeTheme
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.data.model.PayoutStatus
import com.rcube.app.data.model.ProfileStatus

enum class BadgeTone { NEUTRAL, POSITIVE, WARNING, DANGER, INFO }

@Composable
private fun toneColors(tone: BadgeTone): Pair<Color, Color> = when (tone) {
    BadgeTone.POSITIVE -> RcubeTheme.semantic.successContainer to RcubeTheme.semantic.onSuccessContainer
    BadgeTone.WARNING -> RcubeTheme.semantic.warningContainer to RcubeTheme.semantic.onWarningContainer
    BadgeTone.DANGER -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    BadgeTone.INFO -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    BadgeTone.NEUTRAL -> MaterialTheme.colorScheme.surfaceContainer to MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
fun StatusPill(
    text: String,
    tone: BadgeTone,
    modifier: Modifier = Modifier,
    showDot: Boolean = true,
) {
    val (bg, fg) = toneColors(tone)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showDot) {
            androidx.compose.foundation.layout.Box(
                Modifier.size(7.dp).clip(CircleShape).background(fg)
            )
            Text("  ", style = MaterialTheme.typography.labelSmall)
        }
        Text(text, color = fg, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun BookingStatusPill(status: BookingStatus, modifier: Modifier = Modifier) {
    val tone = when (status) {
        BookingStatus.PENDING, BookingStatus.ACCEPTED, BookingStatus.PAYMENT_PENDING -> BadgeTone.WARNING
        BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS -> BadgeTone.INFO
        BookingStatus.COMPLETED -> BadgeTone.POSITIVE
        BookingStatus.DECLINED, BookingStatus.EXPIRED, BookingStatus.PAYMENT_EXPIRED,
        BookingStatus.CANCELLED -> BadgeTone.DANGER
    }
    StatusPill(status.label, tone, modifier)
}

@Composable
fun ProfileStatusPill(status: ProfileStatus, modifier: Modifier = Modifier) {
    val tone = when (status) {
        ProfileStatus.APPROVED -> BadgeTone.POSITIVE
        ProfileStatus.PENDING_REVIEW -> BadgeTone.WARNING
        ProfileStatus.DRAFT -> BadgeTone.NEUTRAL
        ProfileStatus.REJECTED, ProfileStatus.SUSPENDED -> BadgeTone.DANGER
    }
    StatusPill(status.label, tone, modifier)
}

@Composable
fun PayoutStatusPill(status: PayoutStatus, modifier: Modifier = Modifier) {
    val tone = when (status) {
        PayoutStatus.TRANSFERRED -> BadgeTone.POSITIVE
        PayoutStatus.PENDING_TRANSFER, PayoutStatus.TRANSFER_INITIATED -> BadgeTone.WARNING
        PayoutStatus.FAILED -> BadgeTone.DANGER
    }
    StatusPill(status.label, tone, modifier)
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier, label: String = "Verified") {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(RcubeTheme.semantic.successContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.Verified,
            contentDescription = null,
            tint = RcubeTheme.semantic.verified,
            modifier = Modifier.size(14.dp),
        )
        Text(
            "  $label",
            color = RcubeTheme.semantic.onSuccessContainer,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
