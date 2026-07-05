package com.rcube.app.feature.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.VerifiedBadge
import com.rcube.app.core.util.formatInr
import com.rcube.app.data.model.CreatorProfile

@Composable
fun CreatorResultCard(profile: CreatorProfile, onClick: () -> Unit) {
    RcubeCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (profile.profilePhotoUrl != null) {
                    AsyncImage(
                        model = profile.profilePhotoUrl, contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                    )
                } else {
                    Text(profile.category.emoji, fontSize = 26.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(profile.displayName, style = MaterialTheme.typography.titleLarge)
                    if (profile.isVerified) {
                        Spacer(Modifier.width(8.dp))
                        VerifiedBadge()
                    }
                }
                Text(
                    profile.category.label +
                        (profile.distanceKm?.let { " · %.1f km".format(it) } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            profile.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
        )
        profile.startingPricePaise?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                "from ${formatInr(it)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
