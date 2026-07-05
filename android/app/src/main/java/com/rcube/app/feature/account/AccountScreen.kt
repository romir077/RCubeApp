package com.rcube.app.feature.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rcube.app.core.util.uriToBase64Jpeg
import kotlinx.coroutines.launch
import com.rcube.app.core.designsystem.component.BadgeTone
import com.rcube.app.core.designsystem.component.GradientAvatar
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTextField
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SecondaryButton
import com.rcube.app.core.designsystem.component.StatusPill
import com.rcube.app.core.designsystem.theme.RcubeTheme
import com.rcube.app.data.model.AadhaarStatus
import com.rcube.app.data.model.Mode
import com.rcube.app.di.LocalAppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onSwitchMode: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenTerms: () -> Unit,
    onLogout: () -> Unit,
    contentPadding: PaddingValues,
) {
    val repo = LocalAppContainer.current.repository
    val session by repo.session.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val otherMode = if (session.mode == Mode.CREATOR) "Organizer" else "Creator"
    val verified = session.aadhaarStatus == AadhaarStatus.VERIFIED

    var showNameDialog by remember { mutableStateOf(false) }
    var showAadhaarDialog by remember { mutableStateOf(false) }
    var refreshing by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                uriToBase64Jpeg(context, uri)?.let { repo.setUserPhoto(it, uri.toString()) }
            }
        }
    }

    Scaffold(topBar = { RcubeTopBar(title = "Account") }) { inner ->
      PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { scope.launch { refreshing = true; repo.refresh(); refreshing = false } },
        modifier = Modifier.padding(top = inner.calculateTopPadding()).fillMaxSize(),
      ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = contentPadding.calculateBottomPadding() + 24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        Modifier.size(64.dp).clip(CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (session.profilePhotoUrl != null) {
                            AsyncImage(
                                model = session.profilePhotoUrl, contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                            )
                        } else {
                            GradientAvatar(session.userName, size = 64.dp)
                        }
                    }
                    Box(
                        Modifier.size(24.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                photoPicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly,
                                    ),
                                )
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Change photo",
                            tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(session.userName, style = MaterialTheme.typography.headlineSmall)
                        if (verified) {
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.Filled.Verified, contentDescription = "Verified",
                                tint = RcubeTheme.semantic.verified, modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Text(session.phone, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (!verified) {
                    IconButton(onClick = { showNameDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit name")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            IdentityCard(
                status = session.aadhaarStatus,
                onVerify = { showAadhaarDialog = true },
            )

            Spacer(Modifier.height(20.dp))
            RcubeCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("You're in ${if (session.mode == Mode.CREATOR) "Creator" else "Organizer"} mode",
                            style = MaterialTheme.typography.titleMedium)
                        Text("One account, two experiences.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                SecondaryButton(text = "Switch to $otherMode mode", onClick = onSwitchMode)
            }

            Spacer(Modifier.height(20.dp))
            RcubeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                MenuRow(Icons.Filled.NotificationsNone, "Notifications", onOpenNotifications)
                MenuRow(Icons.AutoMirrored.Filled.HelpOutline, "Support", onOpenSupport)
                MenuRow(Icons.Filled.Shield, "Terms & Privacy", onOpenTerms)
            }

            Spacer(Modifier.height(24.dp))
            SecondaryButton(
                text = "Log out",
                onClick = onLogout,
                leadingIcon = Icons.AutoMirrored.Filled.Logout,
            )
            Spacer(Modifier.height(16.dp))
        }
      }
    }

    if (showNameDialog) {
        NameEditDialog(
            initialFirst = session.firstName,
            initialLast = session.lastName,
            onDismiss = { showNameDialog = false },
            onSave = { f, l -> repo.setName(f, l); showNameDialog = false },
        )
    }
    if (showAadhaarDialog) {
        AadhaarDialog(
            onDismiss = { showAadhaarDialog = false },
            onSubmit = { front, back ->
                repo.submitAadhaar(front, back)
                showAadhaarDialog = false
            },
        )
    }
}

@Composable
private fun IdentityCard(status: AadhaarStatus, onVerify: () -> Unit) {
    val tone = when (status) {
        AadhaarStatus.VERIFIED -> BadgeTone.POSITIVE
        AadhaarStatus.PENDING_REVIEW -> BadgeTone.WARNING
        AadhaarStatus.REJECTED -> BadgeTone.DANGER
        AadhaarStatus.NOT_SUBMITTED -> BadgeTone.NEUTRAL
    }
    RcubeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Shield, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Identity verification", style = MaterialTheme.typography.titleMedium)
                Text("Aadhaar — required to book and to be booked",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusPill(status.label, tone)
        }
        Spacer(Modifier.height(12.dp))
        when (status) {
            AadhaarStatus.NOT_SUBMITTED -> {
                Text(
                    "Verify your identity to unlock discovering, booking, and receiving bookings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "Verify with Aadhaar", onClick = onVerify)
            }
            AadhaarStatus.REJECTED -> {
                Text(
                    "Your last submission needs changes (often the name not matching Aadhaar). " +
                        "Update your name if needed and resubmit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "Resubmit Aadhaar", onClick = onVerify)
            }
            AadhaarStatus.PENDING_REVIEW -> Text(
                "Your Aadhaar is under review. We usually verify within 24 hours — pull to refresh later.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AadhaarStatus.VERIFIED -> Text(
                "Your identity is verified. Your name is now locked to match your Aadhaar.",
                style = MaterialTheme.typography.bodyMedium,
                color = RcubeTheme.semantic.onSuccessContainer,
            )
        }
    }
}

@Composable
private fun NameEditDialog(
    initialFirst: String,
    initialLast: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var first by remember { mutableStateOf(initialFirst) }
    var last by remember { mutableStateOf(initialLast) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit your name") },
        text = {
            Column {
                Text(
                    "Make sure this matches your Aadhaar — it locks once verified.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                RcubeTextField("First name", first, { first = it })
                Spacer(Modifier.height(12.dp))
                RcubeTextField("Last name", last, { last = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(first, last) },
                enabled = first.isNotBlank() && last.isNotBlank(),
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun AadhaarDialog(
    onDismiss: () -> Unit,
    onSubmit: (frontBase64: String?, backBase64: String?) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var frontUri by remember { mutableStateOf<Uri?>(null) }
    var backUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    val frontPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> uri?.let { frontUri = it } }
    val backPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> uri?.let { backUri = it } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Verify with Aadhaar") },
        text = {
            Column {
                Text(
                    "Add clear photos of the front and back of your Aadhaar. An admin verifies " +
                        "that your name matches — used only for verification.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ImagePickTile(
                        label = "Aadhaar Front", uri = frontUri, modifier = Modifier.weight(1f),
                        onClick = {
                            frontPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                    )
                    ImagePickTile(
                        label = "Aadhaar Back", uri = backUri, modifier = Modifier.weight(1f),
                        onClick = {
                            backPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = frontUri != null && backUri != null && !uploading,
                onClick = {
                    scope.launch {
                        uploading = true
                        val f = uriToBase64Jpeg(context, frontUri!!)
                        val b = uriToBase64Jpeg(context, backUri!!)
                        uploading = false
                        onSubmit(f, b)
                    }
                },
            ) { Text(if (uploading) "Uploading…" else "Submit") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun ImagePickTile(
    label: String,
    uri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (uri != null) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(14.dp),
            )
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.CloudUpload, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Text(label, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
