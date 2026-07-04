package com.rcube.app.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Semantic colors Material3 doesn't model (success/warning/verified). */
@Immutable
data class RcubeSemanticColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val verified: Color,
    val inkSubtle: Color,
)

private val LightSemanticColors = RcubeSemanticColors(
    success = RcubePalette.Success,
    onSuccess = Color.White,
    successContainer = RcubePalette.SuccessContainer,
    onSuccessContainer = RcubePalette.OnSuccessContainer,
    warning = RcubePalette.Warning,
    warningContainer = RcubePalette.WarningContainer,
    onWarningContainer = RcubePalette.OnWarningContainer,
    verified = RcubePalette.Verified,
    inkSubtle = RcubePalette.InkSubtle,
)

/** 8pt spacing grid (Bible §11.2). */
@Immutable
data class RcubeSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 64.dp,
    val screen: Dp = 20.dp,
)

val LocalRcubeSemanticColors = staticCompositionLocalOf { LightSemanticColors }
val LocalRcubeSpacing = staticCompositionLocalOf { RcubeSpacing() }

/** Ergonomic accessors: `RcubeTheme.semantic.success`, `RcubeTheme.spacing.md`. */
object RcubeTheme {
    val semantic: RcubeSemanticColors
        @Composable @ReadOnlyComposable get() = LocalRcubeSemanticColors.current
    val spacing: RcubeSpacing
        @Composable @ReadOnlyComposable get() = LocalRcubeSpacing.current
}

@Composable
fun RcubeTheme(
    // MVP is light-only by design (warm, paper-like). Param kept for future dark mode.
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    @Suppress("UNUSED_EXPRESSION")
    isSystemInDarkTheme() // referenced so future dark support is a one-line change

    CompositionLocalProvider(
        LocalRcubeSemanticColors provides LightSemanticColors,
        LocalRcubeSpacing provides RcubeSpacing(),
    ) {
        MaterialTheme(
            colorScheme = RcubeLightColorScheme,
            typography = RcubeTypography,
            shapes = RcubeShapes,
            content = content,
        )
    }
}
