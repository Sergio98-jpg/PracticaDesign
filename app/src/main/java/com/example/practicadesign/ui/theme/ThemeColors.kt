package com.example.practicadesign.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Extensiones y funciones de utilidad para acceder a los colores del tema.
 *
 * NOTA: Este archivo ha sido adaptado para consumir las extensiones creadas en Theme.kt
 * (como MaterialTheme.riskColors) cuando es posible, garantizando consistencia.
 */

// =============================================================================
// COLORES ESTÁNDAR (Material Design)
// =============================================================================

@Composable
fun primaryColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun backgroundColor(): Color = MaterialTheme.colorScheme.background

@Composable
fun surfaceColor(): Color = MaterialTheme.colorScheme.surface

@Composable
fun onBackgroundColor(): Color = MaterialTheme.colorScheme.onBackground

@Composable
fun onSurfaceColor(): Color = MaterialTheme.colorScheme.onSurface

@Composable
fun onSurfaceVariantColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun errorColor(): Color = MaterialTheme.colorScheme.error

@Composable
fun outlineColor(): Color = MaterialTheme.colorScheme.outline

@Composable
fun outlineVariantColor(): Color = MaterialTheme.colorScheme.outlineVariant

// =============================================================================
// COLORES DE RIESGO (Conectados a Theme.kt)
// =============================================================================
// Aquí ya NO usamos "if (isSystemInDarkTheme())" manualmente.
// Usamos la extensión .riskColors que creamos en Theme.kt, la cual ya sabe
// qué color entregar automáticamente.

@Composable
fun riskHighColor(): Color = MaterialTheme.riskColors.high

@Composable
fun riskHighContainerColor(): Color = MaterialTheme.riskColors.highContainer

@Composable
fun riskMediumColor(): Color = MaterialTheme.riskColors.medium

@Composable
fun riskMediumContainerColor(): Color = MaterialTheme.riskColors.mediumContainer

@Composable
fun riskLowColor(): Color = MaterialTheme.riskColors.low

@Composable
fun riskLowContainerColor(): Color = MaterialTheme.riskColors.lowContainer

// =============================================================================
// OTROS COLORES SEMÁNTICOS (Manuales)
// =============================================================================
// Como estos (Success, Warning, Info) NO los agregamos a la clase RiskColors en Theme.kt,
// debemos mantener la lógica manual aquí.

/**
 * Obtiene el color de éxito del tema actual.
 */
@Composable
fun successColor(): Color {
    return if (isSystemInDarkTheme()) SuccessDark else SuccessLight
}

/**
 * Obtiene el color de advertencia del tema actual.
 */
@Composable
fun warningColor(): Color {
    return if (isSystemInDarkTheme()) WarningDark else WarningLight
}

/**
 * Obtiene el color de información del tema actual.
 */
@Composable
fun infoColor(): Color {
    return if (isSystemInDarkTheme()) InfoDark else InfoLight
}

// =============================================================================
// COLORES ESPECÍFICOS / LEGACY
// =============================================================================

@Composable
fun checkBagColor(): Color {
    // Si quieres que reaccione al modo oscuro:
    return if (isSystemInDarkTheme()) PrimaryVariantDark else PrimaryVariantLight
}

@Composable
fun checkCirculoColor(): Color {
    // Si quieres que reaccione al modo oscuro:
    return if (isSystemInDarkTheme()) OutlineDark else OutlineLight
}