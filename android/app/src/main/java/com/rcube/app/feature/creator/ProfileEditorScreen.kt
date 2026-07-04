package com.rcube.app.feature.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTextField
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SecondaryButton
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.util.formatInr
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.data.model.ProfileStatus
import com.rcube.app.di.LocalAppContainer

@Composable
fun ProfileEditorScreen(
    profileId: String?,
    onBack: () -> Unit,
    onAddService: (String) -> Unit,
    onPreview: (String) -> Unit,
    onSaved: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val profiles by repo.myProfiles.collectAsStateWithLifecycle()
    val existing = profileId?.let { id -> profiles.firstOrNull { it.id == id } }

    var category by remember { mutableStateOf(existing?.category ?: CreatorCategory.SINGER) }
    var bio by remember { mutableStateOf(existing?.bio ?: "") }
    var city by remember { mutableStateOf(existing?.city ?: "Bengaluru") }
    var languages by remember { mutableStateOf(existing?.languages?.joinToString(", ") ?: "") }
    var instagram by remember { mutableStateOf(existing?.instagram ?: "") }
    var youtube by remember { mutableStateOf(existing?.youtube ?: "") }
    var aadhaarFront by remember { mutableStateOf(existing != null) }
    var aadhaarBack by remember { mutableStateOf(existing != null) }

    val isNew = existing == null
    val title = if (isNew) "Create Profile" else category.label

    Scaffold(topBar = { RcubeTopBar(title = title, onBack = onBack) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            // Decorative cover + avatar
            Box(Modifier.fillMaxWidth().height(120.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )), contentAlignment = Alignment.Center) {
                Text(category.emoji, fontSize = 44.sp)
            }
            Spacer(Modifier.height(16.dp))

            CategoryDropdown(category = category, onSelect = { category = it })
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Bio", bio, { bio = it }, singleLine = false, minLines = 3,
                placeholder = "Tell organizers about your craft…")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("City", city, { city = it })
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Languages", languages, { languages = it },
                placeholder = "English, Hindi", supportingText = "Comma separated")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Instagram (optional)", instagram, { instagram = it },
                placeholder = "@yourhandle")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("YouTube (optional)", youtube, { youtube = it })

            Spacer(Modifier.height(24.dp))
            SectionHeader("Identity")
            Text(
                "Private — used only to verify you. Never shown to organizers.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UploadTile("Aadhaar Front", aadhaarFront, { aadhaarFront = true }, Modifier.weight(1f))
                UploadTile("Aadhaar Back", aadhaarBack, { aadhaarBack = true }, Modifier.weight(1f))
            }

            if (!isNew) {
                Spacer(Modifier.height(24.dp))
                ServicesSection(profile = existing!!, onAddService = { onAddService(existing.id) })
            }

            Spacer(Modifier.height(28.dp))
            if (isNew) {
                PrimaryButton(
                    text = "Save profile",
                    enabled = bio.isNotBlank() && city.isNotBlank() && aadhaarFront && aadhaarBack,
                    onClick = {
                        repo.createProfile(
                            displayName = repo.session.value.userName,
                            category = category,
                            bio = bio,
                            city = city,
                            languages = languages.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        )
                        onSaved()
                    },
                )
            } else {
                val canSubmit = existing!!.services.isNotEmpty() &&
                    (existing.status == ProfileStatus.DRAFT || existing.status == ProfileStatus.REJECTED)
                if (canSubmit) {
                    PrimaryButton(text = "Submit for review", onClick = {
                        repo.submitProfile(existing.id)
                        onSaved()
                    })
                    Spacer(Modifier.height(12.dp))
                } else if (existing.status == ProfileStatus.PENDING_REVIEW) {
                    InfoBanner("Your profile is under review. We usually approve within 24 hours.")
                    Spacer(Modifier.height(12.dp))
                }
                SecondaryButton(
                    text = "Preview as organizer",
                    onClick = { onPreview(existing.id) },
                    leadingIcon = Icons.Filled.Visibility,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(category: CreatorCategory, onSelect: (CreatorCategory) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            "Category",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = "${category.emoji}  ${category.label}",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CreatorCategory.entries.forEach { c ->
                    DropdownMenuItem(
                        text = { Text("${c.emoji}  ${c.label}") },
                        onClick = { onSelect(c); expanded = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadTile(
    label: String,
    uploaded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (uploaded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(14.dp),
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            if (uploaded) Icons.Filled.CheckCircle else Icons.Filled.CloudUpload,
            contentDescription = null,
            tint = if (uploaded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (uploaded) "Uploaded" else label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ServicesSection(profile: CreatorProfile, onAddService: () -> Unit) {
    SectionHeader("Services")
    Spacer(Modifier.height(4.dp))
    if (profile.services.isEmpty()) {
        Text(
            "Add at least one service to submit for review.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    profile.services.forEach { svc ->
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(svc.title, style = MaterialTheme.typography.titleSmall)
                if (svc.durationMinutes != null) {
                    Text("${svc.durationMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(formatInr(svc.pricePaise), style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onAddService)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text("Add service", color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun InfoBanner(text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}
