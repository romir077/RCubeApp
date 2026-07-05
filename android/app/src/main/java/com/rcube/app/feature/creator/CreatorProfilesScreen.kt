package com.rcube.app.feature.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.rcube.app.core.designsystem.theme.RcubeTheme
import com.rcube.app.core.designsystem.component.ProfileStatusPill
import com.rcube.app.feature.common.NotificationBell
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.VerifiedBadge
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.data.model.ProfileStatus
import com.rcube.app.di.LocalAppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorProfilesScreen(
    onOpenProfile: (String) -> Unit,
    onCreateProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAccount: () -> Unit,
    contentPadding: PaddingValues,
) {
    val repo = LocalAppContainer.current.repository
    val profiles by repo.myProfiles.collectAsStateWithLifecycle()
    val session by repo.session.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            RcubeTopBar(title = "My Profiles", actions = {
                NotificationBell(onClick = onOpenNotifications)
            })
        },
    ) { inner ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                scope.launch { refreshing = true; repo.refresh(); refreshing = false }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = inner.calculateTopPadding() + 4.dp,
                    bottom = contentPadding.calculateBottomPadding() + 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (!session.isVerified) {
                    item { VerifyIdentityBanner(onClick = onOpenAccount) }
                }
                item {
                    Text(
                        "Each profile is one craft. Published profiles are discoverable once your identity is verified.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                items(profiles, key = { it.id }) { profile ->
                    ProfileCard(profile, onClick = { onOpenProfile(profile.id) })
                }
                item {
                    CreateProfileButton(onClick = onCreateProfile)
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(profile: CreatorProfile, onClick: () -> Unit) {
    RcubeCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text(profile.category.emoji, fontSize = 26.sp) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(profile.category.label, style = MaterialTheme.typography.titleLarge)
                Text(
                    profile.city + " · " + profile.languages.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileStatusPill(profile.status)
            if (profile.isVerified) {
                Spacer(Modifier.width(8.dp))
                VerifiedBadge()
            }
            Spacer(Modifier.weight(1f))
            Text(
                "${profile.services.size} " + if (profile.services.size == 1) "service" else "services",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (profile.status == ProfileStatus.REJECTED && profile.rejectionReason != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                profile.rejectionReason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun VerifyIdentityBanner(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(RcubeTheme.semantic.warningContainer)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Shield, contentDescription = null,
            tint = RcubeTheme.semantic.onWarningContainer)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Verify your identity", style = MaterialTheme.typography.titleSmall,
                color = RcubeTheme.semantic.onWarningContainer)
            Text("Verify your Aadhaar in Account to start receiving bookings.",
                style = MaterialTheme.typography.bodySmall,
                color = RcubeTheme.semantic.onWarningContainer)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null,
            tint = RcubeTheme.semantic.onWarningContainer)
    }
}

@Composable
private fun CreateProfileButton(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp),
            )
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(
            "Create new profile",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
