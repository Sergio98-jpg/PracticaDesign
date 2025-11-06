package com.example.practicadesign.ui.refugios.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Un componente reutilizable para mostrar un estado de error en la UI.
 *
 * Proporciona al usuario retroalimentación visual clara sobre un problema
 * y una acción para intentar resolverlo (como un botón de reintento).
 *
 * @param message El mensaje de error que se mostrará al usuario.
 * @param onRetry Una función lambda que se ejecutará cuando el usuario pulse el botón "Reintentar".
 * @param modifier Un [Modifier] opcional para personalizar el layout.
 */
@Composable
fun ErrorStateComponent(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icono para llamar la atención visualmente
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Icono de error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            // Mensaje de error
            if (message != null) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botón para que el usuario pueda reintentar la acción
            Button(onClick = onRetry) {
                Text(text = "Reintentar")
            }
        }
    }
}

/**
 * Preview para ver el diseño del ErrorStateComponent en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun ErrorStateComponentPreview() {
    MaterialTheme {
        ErrorStateComponent(
            message = "No se pudo conectar con el servidor. Por favor, revisa tu conexión a internet.",
            onRetry = {} // En el preview, la acción no hace nada.
        )
    }
}


