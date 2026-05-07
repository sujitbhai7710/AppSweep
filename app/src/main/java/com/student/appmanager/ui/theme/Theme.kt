package com.student.appmanager.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * AppSweep Light Color Scheme
 *
 * We use a light-only theme with a vibrant blue primary color.
 * The design emphasizes clarity, whitespace, and visual hierarchy
 * to make app management feel clean and effortless.
 *
 * Color assignments follow Material 3 conventions:
 * - primary: Main brand color (blue) - used for buttons, FABs, active states
 * - onPrimary: Text on primary color backgrounds (white)
 * - primaryContainer: Lighter blue for filled tonal buttons
 * - onPrimaryContainer: Dark blue text on primary containers
 * - secondary: Accent color (violet) - used for badges, secondary actions
 * - tertiary: Warm accent (orange) - used for CTAs, highlights
 * - surface: Card and surface backgrounds
 * - error: Destructive action color (red) - uninstall buttons
 */
private val AppSweepLightColorScheme = lightColorScheme(
    // Primary
    primary = Blue500,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue700,

    // Secondary
    secondary = Violet500,
    onSecondary = White,
    secondaryContainer = Violet100,
    onSecondaryContainer = Color(0xFF4C1D95),

    // Tertiary
    tertiary = Orange500,
    onTertiary = White,
    tertiaryContainer = Orange100,
    onTertiaryContainer = Color(0xFF9A3412),

    // Error
    error = Red500,
    onError = White,
    errorContainer = Red100,
    onErrorContainer = Color(0xFF991B1B),

    // Success (mapped to surface variant for custom use)
    background = White,
    onBackground = Gray900,

    // Surfaces
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray600,

    // Outlines
    outline = Gray300,
    outlineVariant = Gray200,

    // Inverse
    inverseSurface = Gray900,
    inverseOnSurface = Gray50,
    inversePrimary = Blue100,

    // Scrim
    scrim = Gray900
)

/**
 * Main theme composable for AppSweep.
 *
 * Always uses the light color scheme regardless of system dark mode,
 * since the app is designed as a light, bright, fresh experience.
 * This is intentional - the entire visual identity is built around
 * the light theme.
 *
 * @param content The composable content to be themed
 */
@Composable
fun AppSweepTheme(
    content: @Composable () -> Unit
) {
    // We force light theme - AppSweep is designed for a bright, clean look
    val colorScheme = AppSweepLightColorScheme

    // Set status bar and navigation bar colors to match our theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = White.toArgb()
            window.navigationBarColor = White.toArgb()

            // Use dark icons on light status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppSweepTypography,
        content = content
    )
}

/**
 * Preview theme for Compose previews in Android Studio.
 * Uses the same light theme as the production app.
 */
@Composable
fun AppSweepPreviewTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppSweepLightColorScheme,
        typography = AppSweepTypography,
        content = content
    )
}
