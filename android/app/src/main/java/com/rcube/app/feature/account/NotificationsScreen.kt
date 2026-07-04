package com.rcube.app.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.EmptyState
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.data.model.NotificationItem
import com.rcube.app.data.model.NotificationType
import com.rcube.app.di.LocalAppContainer

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val notifications by repo.notifications.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { repo.markAllNotificationsRead() }

    Scaffold(topBar = { RcubeTopBar(title = "Notifications", onBack = onBack) }) { inner ->
        if (notifications.isEmpty()) {
            Box(Modifier.padding(inner)) {
                EmptyState(
                    icon = Icons.Filled.NotificationsNone,
                    title = "You're all caught up",
                    message = "Updates about your bookings and profile will appear here.",
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = inner.calculateTopPadding() + 4.dp, bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(notifications, key = { it.id }) { NotificationRow(it) }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationItem) {
    RcubeCard(modifier = Modifier.fillMaxWidth()) {
        Row {
            Box(
                Modifier.size(40.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(iconFor(item.type), contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.size(2.dp))
                Text(item.body, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.size(4.dp))
                Text(item.timeLabel, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun iconFor(type: NotificationType): ImageVector = when (type) {
    NotificationType.BOOKING -> Icons.Filled.CalendarMonth
    NotificationType.PAYMENT -> Icons.Filled.Payment
    NotificationType.PROFILE -> Icons.Filled.Person
    NotificationType.PAYOUT -> Icons.Filled.Payments
    NotificationType.REMINDER -> Icons.Filled.Schedule
}
