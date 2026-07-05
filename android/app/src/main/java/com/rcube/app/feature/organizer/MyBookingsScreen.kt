package com.rcube.app.feature.organizer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.rcube.app.core.designsystem.component.EmptyState
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SelectableChip
import com.rcube.app.data.model.BookingGroup
import com.rcube.app.feature.common.BookingListCard
import com.rcube.app.feature.common.NotificationBell
import com.rcube.app.di.LocalAppContainer

private enum class BookingTab(val label: String, val group: BookingGroup?) {
    ALL("All", null),
    UPCOMING("Upcoming", BookingGroup.PENDING),
    ACTIVE("Active", BookingGroup.ACTIVE),
    PAST("Past", BookingGroup.DONE),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onOpenBooking: (String) -> Unit,
    onDiscover: () -> Unit,
    onOpenNotifications: () -> Unit,
    contentPadding: PaddingValues,
) {
    val repo = LocalAppContainer.current.repository
    val bookings by repo.organizerBookings.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(BookingTab.ALL) }
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    val filtered = remember(bookings, tab) {
        bookings.filter { tab.group == null || it.status.group == tab.group }
            .sortedByDescending { it.createdAt }
    }

    Scaffold(
        topBar = {
            RcubeTopBar(title = "My Bookings", actions = {
                NotificationBell(onClick = onOpenNotifications)
            })
        },
    ) { inner ->
        Column(Modifier.padding(top = inner.calculateTopPadding())) {
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BookingTab.entries.forEach { t ->
                    SelectableChip(t.label, selected = tab == t, onClick = { tab = t })
                }
            }
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
                        start = 20.dp, end = 20.dp, top = 4.dp,
                        bottom = contentPadding.calculateBottomPadding() + 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (filtered.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.AutoMirrored.Filled.EventNote,
                                title = "No bookings yet",
                                message = "Pull down to refresh, or discover local talent to book.",
                                actionLabel = "Discover talent",
                                onAction = onDiscover,
                            )
                        }
                    } else {
                        items(filtered, key = { it.id }) { booking ->
                            BookingListCard(booking, asCreator = false,
                                onClick = { onOpenBooking(booking.id) })
                        }
                    }
                }
            }
        }
    }
}
