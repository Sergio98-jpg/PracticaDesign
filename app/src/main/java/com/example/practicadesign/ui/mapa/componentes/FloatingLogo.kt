package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Preview del logo flotante.
 */
@Preview(showBackground = true)
@Composable
fun PreviewFloatingLogo() {
    FloatingLogo()
}

/**
 * Componente de logo flotante que muestra el nombre de la aplicación y la ubicación actual.
 * 
 * Se muestra en la esquina superior izquierda del mapa con un gradiente de fondo
 * y la ubicación actual del usuario.
 * 
 * @param modifier Modificador de Compose para personalizar el layout
 * @param location Nombre de la ubicación actual a mostrar
 */
@Composable
fun FloatingLogo(modifier: Modifier = Modifier, location: String = "Ubicación de Prueba") {
    Row(
        modifier = modifier
            .wrapContentSize()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFF0891B2), Color(0xFF06B6D4))),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Reemplazar emoji con un asset SVG/vectorial del logo cuando esté disponible
            Text("💧", fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Yáanal Ha'",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = location,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
