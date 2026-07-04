package com.rcube.app.feature.organizer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcube.app.core.designsystem.component.EmptyState
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SkeletonCard
import com.rcube.app.core.designsystem.component.VerifiedBadge
import com.rcube.app.core.util.formatInr
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.data.model.EventType
import com.rcube.app.di.LocalAppContainer

@Composable
fun SearchResultsScreen(
    category: CreatorCategory,
    eventType: EventType,
    radiusKm: Int,
    onBack: () -> Unit,
    onOpenProfile: (String) -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val resultsState = produceState<List<CreatorProfile>?>(
        initialValue = null, category, radiusKm,
    ) {
        value = runCatching { repo.searchCreators(category, radiusKm.toFloat()) }
            .getOrDefault(emptyList())
    }
    val results = resultsState.value

    Scaffold(
        topBar = {
            RcubeTopBar(title = "${category.label}s · ${eventType.label}", onBack = onBack)
        },
    ) { inner ->
        if (results == null) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = inner.calculateTopPadding() + 4.dp, bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(3) { SkeletonCard() }
            }
        } else if (results.isEmpty()) {
            Box(Modifier.padding(inner)) {
                EmptyState(
                    icon = Icons.Filled.SearchOff,
                    title = "No verified creators yet",
                    message = "Try a wider radius or a different category.",
                    actionLabel = "Adjust search",
                    onAction = onBack,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = inner.calculateTopPadding() + 4.dp, bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Text(
                        "${results.size} verified within $radiusKm km · Bengaluru",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(results, key = { it.id }) { profile ->
                    ResultCard(profile, onClick = { onOpenProfile(profile.id) })
                }
            }
        }
    }
}

@Composable
private fun ResultCard(profile: CreatorProfile, onClick: () -> Unit) {
    RcubeCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text(profile.category.emoji, fontSize = 26.sp) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(profile.displayName, style = MaterialTheme.typography.titleLarge)
                    if (profile.isVerified) {
                        Spacer(Modifier.width(8.dp))
                        VerifiedBadge()
                    }
                }
                Text(
                    profile.category.label +
                        (profile.distanceKm?.let { " · %.1f km".format(it) } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            profile.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
        )
        Spacer(Modifier.height(12.dp))
        profile.startingPricePaise?.let {
            Text(
                "from ${formatInr(it)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
