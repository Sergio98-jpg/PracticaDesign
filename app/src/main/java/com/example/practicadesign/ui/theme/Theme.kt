package com.example.practicadesign.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// =============================================================================
// 1. DEFINICIÓN DE COLORES ESTÁNDAR (Material Design)
// =============================================================================

/**
 * Esquema de colores para el tema claro.
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnBackgroundLight,

    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryVariantLight,
    onSecondaryContainer = OnPrimaryLight,

    tertiary = AccentLight,
    onTertiary = OnPrimaryLight,
    tertiaryContainer = AccentVariantLight,
    onTertiaryContainer = OnBackgroundLight,

    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnBackgroundLight,

    background = BackgroundLight,
    onBackground = OnBackgroundLight,

    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

/**
 * Esquema de colores para el tema oscuro.
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnBackgroundDark,

    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryVariantDark,
    onSecondaryContainer = OnPrimaryDark,

    tertiary = AccentDark,
    onTertiary = OnPrimaryDark,
    tertiaryContainer = AccentVariantDark,
    onTertiaryContainer = OnBackgroundDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnBackgroundDark,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

// =============================================================================
// 2. DEFINICIÓN DE COLORES PERSONALIZADOS (Risk Colors)
// =============================================================================

/**
 * Clase inmutable para contener tus colores semánticos personalizados.
 * Esto permite que el compilador de Compose optimice las recomposiciones.
 */
@Immutable
data class RiskColors(
    val high: Color,
    val highContainer: Color,
    val medium: Color,
    val mediumContainer: Color,
    val low: Color,
    val lowContainer: Color
)

/**
 * CompositionLocal para pasar los colores de riesgo a través del árbol de UI.
 */
val LocalRiskColors = staticCompositionLocalOf {
    // Valores por defecto en caso de error (normalmente no deberían verse)
    RiskColors(
        high = Color.Unspecified,
        highContainer = Color.Unspecified,
        medium = Color.Unspecified,
        mediumContainer = Color.Unspecified,
        low = Color.Unspecified,
        lowContainer = Color.Unspecified
    )
}

// =============================================================================
// 3. TEMA PRINCIPAL
// =============================================================================

/**
 * Tema principal de la aplicación.
 */
@Composable
fun PracticaDesignTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // A. Selección del esquema de color estándar (Material)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // B. Selección del esquema de color personalizado (Risk)
    // Aquí conectamos con las variables que definiste en Color.kt
    val riskColors = if (darkTheme) {
        RiskColors(
            high = RiskHighDark,
            highContainer = RiskHighContainerDark,
            medium = RiskMediumDark,
            mediumContainer = RiskMediumContainerDark,
            low = RiskLowDark,
            lowContainer = RiskLowContainerDark
        )
    } else {
        RiskColors(
            high = RiskHighLight,
            highContainer = RiskHighContainerLight,
            medium = RiskMediumLight,
            mediumContainer = RiskMediumContainerLight,
            low = RiskLowLight,
            lowContainer = RiskLowContainerLight
        )
    }

    // C. Configuración de la barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // D. Proveer ambos esquemas al contenido
    CompositionLocalProvider(LocalRiskColors provides riskColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// =============================================================================
// 4. EXTENSIÓN PARA USO FÁCIL
// =============================================================================

/**
 * Extensión para acceder a los colores de riesgo fácilmente desde el código.
 * Uso: MaterialTheme.riskColors.high
 */
val MaterialTheme.riskColors: RiskColors
    @Composable
    get() = LocalRiskColors.current
