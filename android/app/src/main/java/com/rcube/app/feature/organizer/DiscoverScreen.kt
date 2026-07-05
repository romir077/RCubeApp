package com.rcube.app.feature.organizer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.EmptyState
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SkeletonCard
import com.rcube.app.core.location.LocationEffect
import com.rcube.app.data.model.AadhaarStatus
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.di.LocalAppContainer
import com.rcube.app.feature.common.NotificationBell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onOpenCreator: (String) -> Unit,
    onOpenFilters: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAccount: () -> Unit,
    contentPadding: PaddingValues,
) {
    val repo = LocalAppContainer.current.repository
    val session by repo.session.collectAsStateWithLifecycle()
    val filter by repo.discoverFilter.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var refreshTick by remember { mutableIntStateOf(0) }
    var refreshing by remember { mutableStateOf(false) }

    LocationEffect { repo.setUserLocation(it.lat, it.lng) }

    // Auto-refresh the browse list every 5 seconds.
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            refreshTick++
        }
    }

    Scaffold(
        topBar = {
            RcubeTopBar(title = "Discover talent", actions = {
                NotificationBell(onClick = onOpenNotifications)
            })
        },
    ) { inner ->
        if (session.aadhaarStatus != AadhaarStatus.VERIFIED) {
            Box(Modifier.padding(inner)) {
                val pending = session.aadhaarStatus == AadhaarStatus.PENDING_REVIEW
                EmptyState(
                    icon = Icons.Filled.Shield,
                    title = if (pending) "Verification in progress" else "Verify your identity",
                    message = if (pending) {
                        "We're reviewing your Aadhaar. Once verified you can discover and book creators."
                    } else {
                        "Verify your Aadhaar to discover and book local creators."
                    },
                    actionLabel = "Go to Account",
                    onAction = onOpenAccount,
                )
            }
            return@Scaffold
        }

        val resultsState = produceState<List<CreatorProfile>?>(
            initialValue = null, filter.category, filter.radiusKm, refreshTick,
        ) {
            value = runCatching {
                repo.searchCreators(filter.category, filter.radiusKm.toFloat())
            }.getOrDefault(emptyList())
        }
        val all = resultsState.value

        Column(Modifier.padding(top = inner.calculateTopPadding())) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search creators") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                )
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = onOpenFilters) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filters",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Text(
                (filter.category?.label ?: "All creators") + " · within ${filter.radiusKm} km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
            )

            val filtered = all?.filter {
                query.isBlank() ||
                    it.displayName.contains(query, ignoreCase = true) ||
                    it.category.label.contains(query, ignoreCase = true)
            }

            PullToRefreshBox(
                isRefreshing = refreshing,
                onRefresh = {
                    scope.launch { refreshing = true; refreshTick++; delay(700); refreshing = false }
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    filtered == null -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp, 4.dp, 20.dp, 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) { items(3) { SkeletonCard() } }

                    filtered.isEmpty() -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp, 40.dp, 20.dp, 24.dp),
                    ) {
                        item {
                            EmptyState(
                                icon = Icons.Filled.SearchOff,
                                title = "No creators found",
                                message = "Try a different search, or widen the filters.",
                                actionLabel = "Open filters",
                                onAction = onOpenFilters,
                            )
                        }
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp, end = 20.dp, top = 4.dp,
                            bottom = contentPadding.calculateBottomPadding() + 24.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(filtered, key = { it.id }) { profile ->
                            CreatorResultCard(profile, onClick = { onOpenCreator(profile.id) })
                        }
                    }
                }
            }
        }
    }
}
