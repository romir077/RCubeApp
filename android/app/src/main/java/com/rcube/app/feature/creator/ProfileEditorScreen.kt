package com.rcube.app.feature.creator

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeTextField
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SecondaryButton
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.designsystem.theme.RcubeTheme
import com.rcube.app.core.location.LocationEffect
import com.rcube.app.core.util.formatInr
import com.rcube.app.core.util.uriToBase64Jpeg
import com.rcube.app.core.util.uriToUploadPayload
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.MAX_PORTFOLIO
import com.rcube.app.data.model.MediaType
import com.rcube.app.data.model.PortfolioItem
import com.rcube.app.data.model.ProfileStatus
import com.rcube.app.data.model.Service
import com.rcube.app.di.LocalAppContainer
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ProfileEditorScreen(
    profileId: String?,
    onBack: () -> Unit,
    onOpenService: (profileId: String, serviceId: String?) -> Unit,
    onPreview: (String) -> Unit,
    onProfileCreated: (String) -> Unit,
    onSaved: () -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profiles by repo.myProfiles.collectAsStateWithLifecycle()
    val existing = profileId?.let { id -> profiles.firstOrNull { it.id == id } }

    LocationEffect { repo.setUserLocation(it.lat, it.lng) }

    var category by remember { mutableStateOf(existing?.category ?: CreatorCategory.SINGER) }
    var bio by remember { mutableStateOf(existing?.bio ?: "") }
    var city by remember { mutableStateOf(existing?.city ?: "Bengaluru") }
    var languages by remember { mutableStateOf(existing?.languages?.joinToString(", ") ?: "") }
    var instagram by remember { mutableStateOf(existing?.instagram ?: "") }
    var youtube by remember { mutableStateOf(existing?.youtube ?: "") }
    var pendingPhoto by remember { mutableStateOf<Uri?>(null) }
    var pickError by remember { mutableStateOf<String?>(null) }

    // Buffered services while creating a brand-new profile (persisted on save).
    val newServices = remember { mutableStateListOf<Service>() }
    var serviceDialog by remember { mutableStateOf<Service?>(null) }
    var showServiceDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val isNew = existing == null
    val status = existing?.status ?: ProfileStatus.DRAFT
    val claimEditable = isNew || status == ProfileStatus.DRAFT || status == ProfileStatus.REJECTED
    val categoryEditable = isNew || status != ProfileStatus.APPROVED
    val originalIg = existing?.instagram.orEmpty()
    val originalYt = existing?.youtube.orEmpty()
    val igEditable = claimEditable || (status == ProfileStatus.APPROVED && originalIg.isBlank())
    val ytEditable = claimEditable || (status == ProfileStatus.APPROVED && originalYt.isBlank())
    val handleAdded = status == ProfileStatus.APPROVED &&
        ((originalIg.isBlank() && instagram.isNotBlank()) || (originalYt.isBlank() && youtube.isNotBlank()))
    val services = if (isNew) newServices else existing!!.services

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) {
            if (existing != null) {
                scope.launch {
                    uriToBase64Jpeg(context, uri)?.let {
                        repo.setProfilePhoto(existing.id, it, uri.toString())
                    }
                }
            } else {
                pendingPhoto = uri
            }
        }
    }

    val portfolioPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null && existing != null) {
            scope.launch {
                val payload = uriToUploadPayload(context, uri)
                if (payload == null) {
                    pickError = "Couldn't add that file. Videos must be under ~10MB."
                } else {
                    pickError = null
                    repo.addPortfolioMedia(existing.id, payload.type, payload.base64, uri.toString())
                }
            }
        }
    }

    fun langList() = languages.split(",").map { it.trim() }.filter { it.isNotBlank() }
    fun saveEdits(id: String) = repo.updateProfile(
        id, category, bio, city, langList(),
        instagram.ifBlank { null }, youtube.ifBlank { null },
    )

    Scaffold(
        topBar = { RcubeTopBar(title = if (isNew) "Create Profile" else category.label, onBack = onBack) },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .imePadding(),
        ) {
            if (!isNew) StatusBanner(status, existing!!.rejectionReason, existing.ownerVerified, existing.active)

            ProfilePhotoPicker(
                photoUrl = existing?.profilePhotoUrl ?: pendingPhoto?.toString(),
                emoji = category.emoji,
                onEdit = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            )
            Spacer(Modifier.height(16.dp))

            CategoryDropdown(category, enabled = categoryEditable, onSelect = { category = it })
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Bio", bio, { bio = it }, singleLine = false, minLines = 3,
                placeholder = "Tell organizers about your craft…")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("City", city, { city = it })
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Languages", languages, { languages = it },
                placeholder = "English, Hindi", supportingText = "Comma separated")
            Spacer(Modifier.height(16.dp))
            RcubeTextField("Instagram", instagram, { instagram = it }, enabled = igEditable,
                placeholder = "@yourhandle",
                supportingText = if (!igEditable && instagram.isNotBlank()) "Locked after review" else null)
            Spacer(Modifier.height(16.dp))
            RcubeTextField("YouTube", youtube, { youtube = it }, enabled = ytEditable,
                supportingText = if (!ytEditable && youtube.isNotBlank()) "Locked after review" else null)

            if (!isNew) {
                Spacer(Modifier.height(24.dp))
                PortfolioSection(
                    items = existing!!.portfolio,
                    canAdd = existing.portfolio.size < MAX_PORTFOLIO,
                    onAdd = {
                        portfolioPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo),
                        )
                    },
                    onDelete = { slot -> repo.deletePortfolioMedia(existing.id, slot) },
                )
                pickError?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(24.dp))
            ServicesSection(
                services = services,
                onAdd = {
                    if (isNew) { serviceDialog = null; showServiceDialog = true }
                    else onOpenService(existing!!.id, null)
                },
                onEdit = { sid ->
                    if (isNew) {
                        serviceDialog = newServices.firstOrNull { it.id == sid }
                        showServiceDialog = true
                    } else onOpenService(existing!!.id, sid)
                },
                onDelete = { sid ->
                    if (isNew) newServices.removeAll { it.id == sid }
                    else repo.deleteService(existing!!.id, sid)
                },
            )
            if (isNew) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Add your portfolio photos/videos after saving.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(28.dp))
            if (isNew) {
                PrimaryButton(
                    text = "Save profile",
                    enabled = bio.isNotBlank() && city.isNotBlank(),
                    onClick = {
                        scope.launch {
                            val id = repo.createProfile(
                                displayName = repo.session.value.userName,
                                category = category, bio = bio, city = city,
                                languages = langList(),
                                instagram = instagram.ifBlank { null },
                                youtube = youtube.ifBlank { null },
                                services = newServices.toList(),
                            )
                            pendingPhoto?.let { uri ->
                                uriToBase64Jpeg(context, uri)?.let {
                                    repo.setProfilePhoto(id, it, uri.toString())
                                }
                            }
                            onProfileCreated(id)
                        }
                    },
                )
            } else {
                val id = existing!!.id
                if (claimEditable) {
                    PrimaryButton(
                        text = "Submit for review",
                        enabled = existing.services.isNotEmpty(),
                        onClick = { saveEdits(id); repo.submitProfile(id); onSaved() },
                    )
                    Spacer(Modifier.height(12.dp))
                } else if (handleAdded) {
                    PrimaryButton(
                        text = "Submit updated details for review",
                        onClick = { saveEdits(id); repo.submitProfile(id); onSaved() },
                    )
                    Spacer(Modifier.height(12.dp))
                }
                SecondaryButton(text = "Save changes", onClick = { saveEdits(id); onSaved() })
                Spacer(Modifier.height(12.dp))

                if (status == ProfileStatus.APPROVED) {
                    SecondaryButton(
                        text = if (existing.active) "Deactivate profile" else "Activate profile",
                        onClick = { repo.setProfileActive(id, !existing.active) },
                    )
                    Spacer(Modifier.height(12.dp))
                }
                SecondaryButton(
                    text = "Preview as organizer",
                    onClick = { onPreview(id) },
                    leadingIcon = Icons.Filled.Visibility,
                )
                if (status == ProfileStatus.DRAFT || status == ProfileStatus.REJECTED) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Delete profile", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showServiceDialog) {
        ServiceDialog(
            initial = serviceDialog,
            onDismiss = { showServiceDialog = false },
            onSave = { svc ->
                val idx = newServices.indexOfFirst { it.id == svc.id }
                if (idx >= 0) newServices[idx] = svc else newServices.add(svc)
                showServiceDialog = false
            },
        )
    }

    if (showDeleteConfirm && existing != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this profile?") },
            text = { Text("This removes the draft and its services. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    repo.deleteProfile(existing.id)
                    onSaved()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun ProfilePhotoPicker(photoUrl: String?, emoji: String, onEdit: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(
                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),
            )),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl, contentDescription = "Profile photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                )
            } else {
                Text(emoji, fontSize = 40.sp)
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            PhotoPill(onClick = onEdit)
        }
    }
}

@Composable
private fun PhotoPill(onClick: () -> Unit) {
    Row(
        Modifier
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.PhotoCamera, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text("Photo", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun StatusBanner(
    status: ProfileStatus,
    reason: String?,
    ownerVerified: Boolean,
    active: Boolean,
) {
    when (status) {
        ProfileStatus.REJECTED -> Banner(
            RcubeTheme.semantic.warningContainer, RcubeTheme.semantic.onWarningContainer,
            "Changes requested",
            reason?.takeIf { it.isNotBlank() }
                ?: "Please update your profile and submit it again for review.",
        )
        ProfileStatus.PENDING_REVIEW -> Banner(
            MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer,
            "Under review", "We're reviewing your skill and links. We usually respond within 24 hours.",
        )
        ProfileStatus.APPROVED -> if (!active) Banner(
            MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurface,
            "Deactivated", "This profile is hidden from organizers. Activate it to be discoverable again.",
        ) else Banner(
            RcubeTheme.semantic.successContainer, RcubeTheme.semantic.onSuccessContainer, "Approved",
            if (ownerVerified) "Your profile is live and discoverable."
            else "Approved. It goes live once your identity is verified in Account.",
        )
        else -> {}
    }
    if (status != ProfileStatus.DRAFT) Spacer(Modifier.height(16.dp))
}

@Composable
private fun Banner(bg: Color, fg: Color, title: String, body: String) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(bg).padding(14.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = fg,
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(body, style = MaterialTheme.typography.bodySmall, color = fg)
    }
}

@Composable
private fun PortfolioSection(
    items: List<PortfolioItem>,
    canAdd: Boolean,
    onAdd: () -> Unit,
    onDelete: (Int) -> Unit,
) {
    SectionHeader("Portfolio")
    Spacer(Modifier.height(4.dp))
    Text(
        "Up to 3 photos or videos of your work (shown to organizers).",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(12.dp))
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEach { item ->
            Box(Modifier.size(96.dp)) {
                Box(
                    Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = item.thumbUrl, contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                    )
                    if (item.type == MediaType.VIDEO) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = "Video",
                            tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
                Box(
                    Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp)
                        .clip(CircleShape).background(Color.Black.copy(alpha = 0.55f))
                        .clickable { onDelete(item.slot) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Delete",
                        tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
        if (canAdd) {
            Column(
                Modifier.size(96.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable(onClick = onAdd),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add media",
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text("Add", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    category: CreatorCategory,
    enabled: Boolean,
    onSelect: (CreatorCategory) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Category", style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp))
        ExposedDropdownMenuBox(
            expanded = expanded && enabled,
            onExpandedChange = { if (enabled) expanded = it },
        ) {
            OutlinedTextField(
                value = "${category.emoji}  ${category.label}",
                onValueChange = {}, readOnly = true, enabled = enabled,
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded && enabled) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
            )
            ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
                CreatorCategory.entries.forEach { c ->
                    DropdownMenuItem(text = { Text("${c.emoji}  ${c.label}") },
                        onClick = { onSelect(c); expanded = false })
                }
            }
        }
    }
}

@Composable
private fun ServicesSection(
    services: List<Service>,
    onAdd: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    SectionHeader("Services")
    Spacer(Modifier.height(4.dp))
    if (services.isEmpty()) {
        Text("Add at least one service to submit for review.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    services.forEach { svc ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(svc.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    formatInr(svc.pricePaise) +
                        (svc.durationMinutes?.let { " · $it min" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = { onEdit(svc.id) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = { onDelete(svc.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onAdd).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text("Add service", color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ServiceDialog(
    initial: Service?,
    onDismiss: () -> Unit,
    onSave: (Service) -> Unit,
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var duration by remember { mutableStateOf(initial?.durationMinutes?.toString() ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var price by remember { mutableStateOf(initial?.let { (it.pricePaise / 100).toString() } ?: "") }
    val priceRupees = price.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val valid = title.isNotBlank() && priceRupees > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add service" else "Edit service") },
        text = {
            Column {
                RcubeTextField("Title", title, { title = it },
                    placeholder = "30 Minute Live Performance")
                Spacer(Modifier.height(12.dp))
                RcubeTextField("Price", price, { price = it.filter(Char::isDigit) },
                    keyboardType = KeyboardType.Number, prefix = "₹ ", placeholder = "2000")
                Spacer(Modifier.height(12.dp))
                RcubeTextField("Duration in minutes (optional)", duration,
                    { duration = it.filter(Char::isDigit) }, keyboardType = KeyboardType.Number,
                    placeholder = "30")
                Spacer(Modifier.height(12.dp))
                RcubeTextField("Description (optional)", description, { description = it },
                    singleLine = false, minLines = 2)
            }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = {
                    onSave(
                        Service(
                            id = initial?.id ?: ("s_" + UUID.randomUUID().toString().take(6)),
                            title = title.trim(),
                            pricePaise = priceRupees * 100,
                            durationMinutes = duration.toIntOrNull(),
                            description = description.trim().ifBlank { null },
                        ),
                    )
                },
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
