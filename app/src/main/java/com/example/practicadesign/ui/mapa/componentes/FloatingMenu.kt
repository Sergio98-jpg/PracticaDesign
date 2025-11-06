package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Preview del botón de menú flotante.
 */
@Preview(showBackground = true)
@Composable
fun PreviewFloatingMenu() {
    FloatingMenu(onClick = {})
}

/**
 * Botón de menú flotante que muestra tres líneas horizontales.
 * 
 * Se utiliza para abrir el menú lateral de la aplicación.
 * 
 * @param onClick Función a ejecutar cuando se hace clic en el botón
 * @param modifier Modificador de Compose para personalizar el layout
 */
@Composable
fun FloatingMenu(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .size(48.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.size(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(Modifier.height(2.dp).fillMaxWidth().background(Color(0xFF0F172A), shape = RoundedCornerShape(2.dp)))
            Box(Modifier.height(2.dp).fillMaxWidth().background(Color(0xFF0F172A), shape = RoundedCornerShape(2.dp)))
            Box(Modifier.height(2.dp).fillMaxWidth().background(Color(0xFF0F172A), shape = RoundedCornerShape(2.dp)))
        }
    }
}
