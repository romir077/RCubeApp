package com.rcube.app.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.GradientAvatar
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SecondaryButton
import com.rcube.app.data.model.Mode
import com.rcube.app.di.LocalAppContainer

@Composable
fun AccountScreen(
    onSwitchMode: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit,
    contentPadding: PaddingValues,
) {
    val repo = LocalAppContainer.current.repository
    val session by repo.session.collectAsStateWithLifecycle()
    val otherMode = if (session.mode == Mode.CREATOR) "Organizer" else "Creator"

    Scaffold(topBar = { RcubeTopBar(title = "Account") }) { inner ->
        Column(
            Modifier
                .padding(top = inner.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GradientAvatar(session.userName, size = 64.dp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(session.userName, style = MaterialTheme.typography.headlineSmall)
                    Text(session.phone, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(20.dp))
            RcubeCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("You're in ${if (session.mode == Mode.CREATOR) "Creator" else "Organizer"} mode",
                            style = MaterialTheme.typography.titleMedium)
                        Text("One account, two experiences.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                SecondaryButton(text = "Switch to $otherMode mode", onClick = onSwitchMode)
            }

            Spacer(Modifier.height(20.dp))
            RcubeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                MenuRow(Icons.Filled.NotificationsNone, "Notifications", onOpenNotifications)
                MenuRow(Icons.AutoMirrored.Filled.HelpOutline, "Support", {})
                MenuRow(Icons.Filled.Shield, "Terms & Privacy", {})
            }

            Spacer(Modifier.height(24.dp))
            SecondaryButton(
                text = "Log out",
                onClick = onLogout,
                leadingIcon = Icons.AutoMirrored.Filled.Logout,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "RCube · Recognition before Monetization",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
