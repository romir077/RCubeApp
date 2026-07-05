package com.rcube.app.feature.organizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.designsystem.component.SecondaryButton
import com.rcube.app.core.designsystem.component.SelectableChip
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.repository.DiscoverFilter
import com.rcube.app.di.LocalAppContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersScreen(onApply: () -> Unit, onBack: () -> Unit) {
    val repo = LocalAppContainer.current.repository
    val current by repo.discoverFilter.collectAsStateWithLifecycle()

    // Nothing is pre-selected: category starts as null ("Any").
    var category by remember { mutableStateOf(current.category) }
    var radius by remember { mutableFloatStateOf(current.radiusKm.toFloat()) }

    Scaffold(topBar = { RcubeTopBar(title = "Filters", onBack = onBack) }) { inner ->
        Column(
            Modifier
                .padding(top = inner.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            SectionHeader("Type of creator")
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip("Any", selected = category == null, onClick = { category = null })
                CreatorCategory.entries.forEach { c ->
                    SelectableChip("${c.emoji} ${c.label}", selected = category == c,
                        onClick = { category = if (category == c) null else c })
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Within", style = MaterialTheme.typography.titleMedium)
                Text("${radius.toInt()} km", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Slider(value = radius, onValueChange = { radius = it }, valueRange = 1f..50f)

            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = "Show creators",
                onClick = {
                    repo.setDiscoverFilter(DiscoverFilter(category = category, radiusKm = radius.toInt()))
                    onApply()
                },
                leadingIcon = Icons.Filled.Check,
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = "Reset",
                onClick = {
                    category = null
                    radius = 25f
                    repo.setDiscoverFilter(DiscoverFilter())
                    onApply()
                },
            )
        }
    }
}
