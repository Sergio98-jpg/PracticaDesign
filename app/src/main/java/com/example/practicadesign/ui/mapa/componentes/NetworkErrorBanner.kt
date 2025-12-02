package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practicadesign.ui.theme.PracticaDesignTheme

/**
 * Banner que muestra un mensaje de error cuando no hay conexión al servidor del mapa.
 * 
 * Diseñado para ser discreto pero visible, informando al usuario que la app está
 * trabajando en modo offline o con datos en cache.
 * 
 * @param message Mensaje de error a mostrar. Si es null, el banner no se muestra.
 * @param modifier Modificador de Compose para personalizar el layout
 */
@Preview(showBackground = true, name = "Network Error Banner - Claro")
@Composable
private fun PreviewNetworkErrorBannerLight() {
    PracticaDesignTheme(darkTheme = false) {
        NetworkErrorBanner(
            message = "No hay conexión. No se pudieron cargar los datos del mapa.",
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, name = "Network Error Banner - Oscuro")
@Composable
private fun PreviewNetworkErrorBannerDark() {
    PracticaDesignTheme(darkTheme = true) {
        NetworkErrorBanner(
            message = "No hay conexión. Usando datos en caché.",
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
@Composable
fun NetworkErrorBanner(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if (message != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFEBEE), // Fondo rojo muy claro
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Icono de WiFi desconectado
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "Sin conexión",
                    tint = Color(0xFFDC2626), // Rojo
                    modifier = Modifier.size(18.dp)
                )
                
                // Mensaje de error
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFDC2626), // Rojo
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


