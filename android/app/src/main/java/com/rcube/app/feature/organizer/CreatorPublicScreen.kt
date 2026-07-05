package com.rcube.app.feature.organizer

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rcube.app.data.model.MediaType
import com.rcube.app.data.model.PortfolioItem
import com.rcube.app.data.model.Review
import com.rcube.app.core.designsystem.component.PrimaryButton
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SectionHeader
import com.rcube.app.core.designsystem.component.VerifiedBadge
import com.rcube.app.core.designsystem.component.avatarBrush
import com.rcube.app.core.designsystem.theme.RcubeTheme
import com.rcube.app.core.util.formatInr
import com.rcube.app.data.model.Service
import com.rcube.app.di.LocalAppContainer
import com.rcube.app.feature.common.MediaViewerDialog

@Composable
fun CreatorPublicScreen(
    profileId: String,
    bookable: Boolean,
    onBack: () -> Unit,
    onBook: (serviceId: String) -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    var viewer by remember { mutableStateOf<PortfolioItem?>(null) }
    // Observe both sources so previews of my own profile stay live.
    val mine by repo.myProfiles.collectAsStateWithLifecycle()
    val dir by repo.directory.collectAsStateWithLifecycle()
    val profile = (mine + dir).firstOrNull { it.id == profileId }

    Scaffold(
        topBar = {
            RcubeTopBar(title = if (bookable) "" else "Preview", onBack = onBack)
        },
    ) { padding ->
        if (profile == null) {
            Text("Profile not found", Modifier.padding(padding).padding(24.dp))
            return@Scaffold
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp),
        ) {
            // Cover + avatar
            Box(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))),
                    contentAlignment = Alignment.Center,
                ) { Text(profile.category.emoji, fontSize = 56.sp) }
            }
            Column(Modifier.padding(horizontal = 20.dp)) {
                Box(
                    Modifier
                        .offset(y = (-28).dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(avatarBrush(profile.displayName)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (profile.profilePhotoUrl != null) {
                        AsyncImage(
                            model = profile.profilePhotoUrl, contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Text(profile.category.emoji, fontSize = 32.sp)
                    }
                }

                Row(
                    Modifier.offset(y = (-16).dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(profile.displayName, style = MaterialTheme.typography.displaySmall)
                    if (profile.isVerified) {
                        Spacer(Modifier.width(10.dp))
                        VerifiedBadge()
                    }
                }
                Text(
                    "${profile.category.label} · ${profile.city}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.offset(y = (-12).dp),
                )
                if (profile.ratingCount > 0 && profile.ratingAvg != null) {
                    Row(
                        Modifier.offset(y = (-8).dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null,
                            tint = RcubeTheme.semantic.warning, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "%.1f".format(profile.ratingAvg) +
                                " · ${profile.ratingCount} " +
                                if (profile.ratingCount == 1) "review" else "reviews",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Text(
                    profile.languages.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.offset(y = (-8).dp),
                )

                Spacer(Modifier.height(12.dp))
                SectionHeader("About")
                Spacer(Modifier.height(6.dp))
                Text(profile.bio, style = MaterialTheme.typography.bodyLarge)

                if (profile.instagram != null || profile.youtube != null) {
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        profile.instagram?.let { SocialLink(Icons.Filled.CameraAlt, it) }
                        profile.youtube?.let { SocialLink(Icons.Filled.PlayCircle, it) }
                    }
                }

                if (profile.portfolio.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader("Portfolio")
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        profile.portfolio.forEach { item ->
                            Box(
                                Modifier.size(110.dp).clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .clickable { viewer = item },
                                contentAlignment = Alignment.Center,
                            ) {
                                AsyncImage(
                                    model = item.thumbUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                )
                                if (item.type == MediaType.VIDEO) {
                                    Icon(Icons.Filled.PlayCircle, contentDescription = "Video",
                                        tint = Color.White, modifier = Modifier.size(34.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                SectionHeader("Services")
                Spacer(Modifier.height(10.dp))
                profile.services.forEach { svc ->
                    ServiceRow(svc, bookable = bookable, onBook = { onBook(svc.id) })
                    Spacer(Modifier.height(12.dp))
                }
                if (profile.reviews.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader("Reviews")
                    Spacer(Modifier.height(10.dp))
                    profile.reviews.forEach { review ->
                        ReviewRow(review)
                        Spacer(Modifier.height(12.dp))
                    }
                }

                if (!bookable) {
                    Text(
                        "This is how organizers see your profile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    viewer?.let { item ->
        MediaViewerDialog(type = item.type, url = item.fullUrl, onDismiss = { viewer = null })
    }
}

@Composable
private fun ReviewRow(review: Review) {
    RcubeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(review.organizerName, style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f))
            repeat(5) { i ->
                Icon(
                    Icons.Filled.Star, contentDescription = null,
                    tint = if (i < review.rating) RcubeTheme.semantic.warning
                    else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        if (review.comment.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text("\"${review.comment}\"", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SocialLink(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ServiceRow(svc: Service, bookable: Boolean, onBook: () -> Unit) {
    RcubeCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(svc.title, style = MaterialTheme.typography.titleMedium)
                if (svc.durationMinutes != null) {
                    Text("${svc.durationMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                svc.description?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(formatInr(svc.pricePaise), style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold)
        }
        if (bookable) {
            Spacer(Modifier.height(12.dp))
            PrimaryButton(text = "Book · ${formatInr(svc.pricePaise)}", onClick = onBook)
        }
    }
}
