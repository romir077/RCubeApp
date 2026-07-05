package com.rcube.app.feature.creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.rcube.app.core.designsystem.component.EmptyState
import com.rcube.app.core.designsystem.component.PayoutStatusPill
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.util.formatInr
import com.rcube.app.core.util.formatShort
import com.rcube.app.data.model.Booking
import com.rcube.app.data.model.PayoutStatus
import com.rcube.app.di.LocalAppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(contentPadding: PaddingValues) {
    val repo = LocalAppContainer.current.repository
    // Recompute when bookings change.
    val bookings by repo.creatorBookings.collectAsStateWithLifecycle()
    val summary = rememberEarnings(repo, bookings)
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    Scaffold(topBar = { RcubeTopBar(title = "Earnings") }) { inner ->
      PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { scope.launch { refreshing = true; repo.refresh(); refreshing = false } },
        modifier = Modifier.padding(top = inner.calculateTopPadding()).fillMaxSize(),
      ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    SummaryTile(
                        "Pending transfer",
                        formatInr(summary.pendingTransferPaise),
                        Modifier.weight(1f),
                        highlight = true,
                    )
                    SummaryTile(
                        "Transferred",
                        formatInr(summary.transferredTotalPaise),
                        Modifier.weight(1f),
                    )
                }
            }
            item {
                Text(
                    "Recognition first — earnings follow. Payments are released after each completed event.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item { SectionHeader("Recent") }
            if (summary.items.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Payments,
                        title = "No earnings yet",
                        message = "Your earnings will show here after your first completed event.",
                    )
                }
            } else {
                items(summary.items, key = { it.id }) { EarningRow(it) }
            }
        }
      }
    }
}

@Composable
private fun rememberEarnings(
    repo: com.rcube.app.data.repository.RcubeRepository,
    bookings: List<Booking>,
) = androidx.compose.runtime.remember(bookings) { repo.earnings() }

@Composable
private fun SummaryTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    RcubeCard(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun EarningRow(booking: Booking) {
    RcubeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(booking.serviceTitle, style = MaterialTheme.typography.titleMedium)
                Text(
                    booking.eventDate.formatShort() + " · " + booking.organizerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(formatInr(booking.netPaise), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                booking.payoutStatus?.let { PayoutStatusPill(it) }
            }
        }
    }
}
