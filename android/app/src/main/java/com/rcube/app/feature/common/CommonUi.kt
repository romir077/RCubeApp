package com.rcube.app.feature.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.di.LocalAppContainer

/** Notification bell with an unread-count badge. */
@Composable
fun NotificationBell(onClick: () -> Unit) {
    val repo = LocalAppContainer.current.repository
    val notifications by repo.notifications.collectAsStateWithLifecycle()
    val unread = notifications.count { !it.read }

    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (unread > 0) Badge { Text(if (unread > 9) "9+" else unread.toString()) }
            },
        ) {
            Icon(Icons.Filled.NotificationsNone, contentDescription = "Notifications")
        }
    }
}
