package com.rcube.app.core.designsystem.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * RCube palette — warm, premium, creator-first (see Bible §11.2).
 * Terracotta primary, amber accent, paper-like background.
 */
object RcubePalette {
    val Terracotta = Color(0xFFE4572E)
    val TerracottaBright = Color(0xFFEA6A45)
    val TerracottaDark = Color(0xFFB23E1C)
    val TerracottaContainer = Color(0xFFFBE3D9)
    val OnTerracottaContainer = Color(0xFF5A1B08)

    val Amber = Color(0xFFF2A65A)
    val AmberContainer = Color(0xFFFDEBD6)
    val OnAmberContainer = Color(0xFF5A3A12)

    val Paper = Color(0xFFFBF9F6)
    val Cream = Color(0xFFFFF7EF)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF4EEE7)
    val SurfaceVariant = Color(0xFFF1EAE1)

    val Ink = Color(0xFF1E1B18)
    val InkMuted = Color(0xFF6B6560)
    val InkSubtle = Color(0xFF9A938B)

    val Outline = Color(0xFFE6DFD6)
    val OutlineStrong = Color(0xFFD8D0C6)

    val Success = Color(0xFF2E7D5B)
    val SuccessContainer = Color(0xFFD7EEE2)
    val OnSuccessContainer = Color(0xFF0C3D28)

    val Warning = Color(0xFFC9A227)
    val WarningContainer = Color(0xFFF7EDCB)
    val OnWarningContainer = Color(0xFF4A3B00)

    val Danger = Color(0xFFB23A48)
    val DangerContainer = Color(0xFFF7DEE1)
    val OnDangerContainer = Color(0xFF4A0E15)

    val Verified = Color(0xFF2E7D5B)
}

val RcubeLightColorScheme = lightColorScheme(
    primary = RcubePalette.Terracotta,
    onPrimary = Color.White,
    primaryContainer = RcubePalette.TerracottaContainer,
    onPrimaryContainer = RcubePalette.OnTerracottaContainer,

    secondary = RcubePalette.Amber,
    onSecondary = RcubePalette.Ink,
    secondaryContainer = RcubePalette.AmberContainer,
    onSecondaryContainer = RcubePalette.OnAmberContainer,

    tertiary = RcubePalette.Success,
    onTertiary = Color.White,
    tertiaryContainer = RcubePalette.SuccessContainer,
    onTertiaryContainer = RcubePalette.OnSuccessContainer,

    background = RcubePalette.Paper,
    onBackground = RcubePalette.Ink,

    surface = RcubePalette.Surface,
    onSurface = RcubePalette.Ink,
    surfaceVariant = RcubePalette.SurfaceVariant,
    onSurfaceVariant = RcubePalette.InkMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = RcubePalette.Cream,
    surfaceContainer = RcubePalette.SurfaceMuted,
    surfaceContainerHigh = RcubePalette.SurfaceVariant,

    outline = RcubePalette.Outline,
    outlineVariant = RcubePalette.OutlineStrong,

    error = RcubePalette.Danger,
    onError = Color.White,
    errorContainer = RcubePalette.DangerContainer,
    onErrorContainer = RcubePalette.OnDangerContainer,

    scrim = Color(0x66000000),
)
