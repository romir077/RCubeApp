package com.rcube.app.feature.creator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTextField
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SecondaryButton
import com.rcube.app.data.model.Service
import com.rcube.app.di.LocalAppContainer
import java.util.UUID

@Composable
fun ServiceEditorScreen(
    profileId: String,
    serviceId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val profiles by repo.myProfiles.collectAsStateWithLifecycle()
    val existing = serviceId?.let { sid ->
        profiles.firstOrNull { it.id == profileId }?.services?.firstOrNull { it.id == sid }
    }
    val isEditing = existing != null

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var duration by remember { mutableStateOf(existing?.durationMinutes?.toString() ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var price by remember { mutableStateOf(existing?.let { (it.pricePaise / 100).toString() } ?: "") }

    val priceRupees = price.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val valid = title.isNotBlank() && priceRupees > 0

    Scaffold(
        topBar = { RcubeTopBar(title = if (isEditing) "Edit Service" else "Add Service", onBack = onBack) },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .imePadding(),
        ) {
            RcubeTextField("Title", title, { title = it },
                placeholder = "30 Minute Live Performance")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Duration in minutes (optional)", duration,
                { duration = it.filter(Char::isDigit) }, keyboardType = KeyboardType.Number,
                placeholder = "30")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Description (optional)", description, { description = it },
                singleLine = false, minLines = 3,
                placeholder = "What's included in this service?")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Price", price, { price = it.filter(Char::isDigit) },
                keyboardType = KeyboardType.Number, prefix = "₹ ", placeholder = "2000",
                supportingText = "Fixed price for this service — no hourly rates.")

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = if (isEditing) "Save service" else "Add service",
                enabled = valid,
                onClick = {
                    val svc = Service(
                        id = existing?.id ?: ("s_" + UUID.randomUUID().toString().take(6)),
                        title = title.trim(),
                        pricePaise = priceRupees * 100,
                        durationMinutes = duration.toIntOrNull(),
                        description = description.trim().ifBlank { null },
                    )
                    if (isEditing) repo.updateService(profileId, svc)
                    else repo.addService(profileId, svc)
                    onSaved()
                },
            )
            if (isEditing) {
                Spacer(Modifier.height(12.dp))
                SecondaryButton(
                    text = "Delete service",
                    onClick = { repo.deleteService(profileId, existing!!.id); onSaved() },
                    leadingIcon = Icons.Filled.Delete,
                )
            }
        }
    }
}
