package com.rcube.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rcube.app.core.util.initialsOf

private val avatarGradients: List<Pair<Color, Color>> = listOf(
    Color(0xFFE4572E) to Color(0xFFF2A65A),
    Color(0xFF2E7D5B) to Color(0xFF7BC29A),
    Color(0xFF3A6EA5) to Color(0xFF6FB1D6),
    Color(0xFF8E4585) to Color(0xFFC97FB6),
    Color(0xFFB5651D) to Color(0xFFE9A56B),
    Color(0xFF4B6858) to Color(0xFF89A894),
    Color(0xFF9C3848) to Color(0xFFD98594),
    Color(0xFF5B5F97) to Color(0xFF9AA0D4),
)

fun avatarBrush(seed: String): Brush {
    val idx = ((seed.hashCode() % avatarGradients.size) + avatarGradients.size) % avatarGradients.size
    val (a, b) = avatarGradients[idx]
    return Brush.linearGradient(listOf(a, b))
}

@Composable
fun GradientAvatar(
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = (size.value / 2.6f).sp,
) {
    val brush = remember(name) { avatarBrush(name) }
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(brush),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsOf(name),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
        )
    }
}

/** Decorative cover banner (used where a real cover photo would go). */
@Composable
fun CoverBanner(
    seed: String,
    emoji: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 0.dp,
) {
    val brush = remember(seed) { avatarBrush(seed) }
    Box(
        modifier = modifier.clip(RoundedCornerShape(cornerRadius)).background(brush),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = 40.sp)
    }
}
