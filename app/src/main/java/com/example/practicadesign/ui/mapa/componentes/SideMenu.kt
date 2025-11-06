package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
@Preview(showBackground = true)
@Composable
fun PreviewSideMenu() {
    SideMenu(open = true, onClose = {})
}

/**
 * Menú lateral deslizable (drawer) de la aplicación.
 * 
 * El menú puede estar en estado abierto o cerrado, y se anima suavemente
 * hacia adentro y hacia afuera de la pantalla. Contiene un encabezado,
 * un botón para cerrar y una lista de elementos de menú.
 *
 * @param open Valor booleano que determina si el menú está visible (true) u oculto (false).
 *             Este estado controla la animación de deslizamiento.
 * @param onClose Función lambda que se invoca cuando el usuario hace clic en el botón de cierre.
 *                Es responsabilidad del llamador cambiar el estado `open` a `false`.
 * @param modifier Modificador de Compose opcional para aplicar al contenedor principal del menú.
 */
@Composable
fun SideMenu(open: Boolean, onClose: () -> Unit, modifier: Modifier = Modifier) {
    // Anima el desplazamiento horizontal del menú
    // Si `open` es true, el offset es 0.dp (totalmente visible)
    // Si `open` es false, se desplaza 320.dp hacia la derecha, ocultándose fuera de la pantalla
    val offsetX by animateDpAsState(
        targetValue = if (open) 0.dp else 320.dp,
        label = "sideMenuOffsetX",
        animationSpec = tween(
            durationMillis = 200,
            easing = androidx.compose.animation.core.EaseInOut
        )
    )


    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .offset(x = offsetX)
            .shadow(8.dp)
            .background(Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Cabecera del menú con el título y el botón de cierre
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Menú",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                // Botón de cierre
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", fontSize = 18.sp)
                }
            }
            Spacer(Modifier.height(16.dp))

            // Lista de elementos del menú
            val items = listOf(
                "Mi Perfil",
                "Configuración",
                "Notificaciones",
                "Historial de Alertas",
                "Acerca de",
                "Ayuda",
                "Cerrar Sesión"
            )
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable {
                            // TODO: Implementar la navegación para cada elemento del menú
                        }
                ) {
                    Text(
                        text = item,
                        fontSize = 16.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }
    }
}
