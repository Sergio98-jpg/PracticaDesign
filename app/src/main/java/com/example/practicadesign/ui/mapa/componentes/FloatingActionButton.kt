package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Preview del botón de acción flotante.
 */
@Preview(showBackground = true)
@Composable
fun PreviewFloatingActionButton() {
    FloatingActionButton(icon = null, contentDescription = "Centrar") {}
}

/**
 * Botón de acción flotante (FAB) personalizado.
 * 
 * Puede mostrar un ícono (ImageVector) o un Painter, o un placeholder si no se proporciona ninguno.
 * 
 * @param icon Ícono vectorial a mostrar (tiene prioridad sobre painter)
 * @param painter Painter a mostrar si no se proporciona icon
 * @param contentDescription Descripción del contenido para accesibilidad
 * @param onClick Función a ejecutar cuando se hace clic en el botón
 */
@Composable
fun FloatingActionButton(
    icon: ImageVector? = null,
    painter: Painter? = null,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            icon != null -> Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color(0xFF0891B2),
                modifier = Modifier.size(24.dp)
            )

            painter != null -> Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )

            else -> Text("◎", fontSize = 18.sp)
        }
    }
}