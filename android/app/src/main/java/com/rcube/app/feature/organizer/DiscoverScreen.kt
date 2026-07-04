package com.rcube.app.feature.organizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.designsystem.component.SelectableChip
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.EventType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiscoverScreen(
    onSearch: (category: CreatorCategory, eventType: EventType, radiusKm: Int) -> Unit,
    onOpenNotifications: () -> Unit,
    contentPadding: PaddingValues,
) {
    var eventType by remember { mutableStateOf(EventType.BIRTHDAY) }
    var category by remember { mutableStateOf(CreatorCategory.GUITARIST) }
    var radius by remember { mutableFloatStateOf(10f) }

    Scaffold(
        topBar = {
            RcubeTopBar(title = "Discover talent", actions = {
                IconButton(onClick = onOpenNotifications) {
                    Icon(Icons.Filled.NotificationsNone, contentDescription = "Notifications")
                }
            })
        },
    ) { inner ->
        Column(
            Modifier
                .padding(top = inner.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp),
        ) {
            Text(
                "Find verified local creators for your event.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))

            SectionHeader("Event type")
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EventType.entries.forEach { et ->
                    SelectableChip("${et.emoji} ${et.label}", selected = eventType == et,
                        onClick = { eventType = et })
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionHeader("Category")
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CreatorCategory.entries.forEach { c ->
                    SelectableChip("${c.emoji} ${c.label}", selected = category == c,
                        onClick = { category = c })
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Within", style = MaterialTheme.typography.titleMedium)
                Text("${radius.toInt()} km", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Slider(value = radius, onValueChange = { radius = it }, valueRange = 1f..25f)

            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = "Search creators",
                onClick = { onSearch(category, eventType, radius.toInt()) },
                leadingIcon = Icons.Filled.Search,
            )
        }
    }
}
