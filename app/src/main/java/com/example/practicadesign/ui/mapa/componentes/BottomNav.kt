package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Map
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.User
import com.example.practicadesign.ui.navegacion.Screen

/**
 * Componente de navegación inferior (Bottom Navigation Bar).
 * 
 * Muestra los elementos principales de navegación de la aplicación:
 * - Mapa (siempre visible)
 * - Alertas (solo para usuarios logueados)
 * - Refugios (siempre visible)
 * - Reporte (siempre visible, redirige a Login si no hay usuario)
 * - Perfil (siempre visible, redirige a Login)
 * 
 * @param modifier Modificador de Compose para personalizar el layout
 * @param navController Controlador de navegación para moverse entre pantallas
 * @param userRole Rol del usuario actual (null si no está logueado)
 */
@Preview(showBackground = true, name = "BottomNav - Usuario Invitado")
@Composable
fun PreviewBottomNavGuest() {
    BottomNav(
        modifier = Modifier,
        navController = rememberNavController(),
        userRole = null
    )
}

@Preview(showBackground = true, name = "BottomNav - Usuario Logueado")
@Composable
fun PreviewBottomNavLoggedIn() {
    BottomNav(
        modifier = Modifier,
        navController = rememberNavController(),
        userRole = "user"
    )
}

@Composable
fun BottomNav(
    modifier: Modifier = Modifier,
    navController: NavController,
    userRole: String?
) {
    // Obtiene la ruta actual para saber qué ítem está activo
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp)
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ítem del Mapa
        BottomNavItem(
            active = currentRoute == Screen.Mapa.route,
            label = "Mapa",
            icon = Lucide.Map,
            onClick = {
                navController.navigate(Screen.Mapa.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Ítem de Alertas (solo para usuarios logueados)
        if (userRole != null) {
            BottomNavItem(
                active = false,
                label = "Alertas",
                icon = Lucide.CircleAlert,
                badge = "1",
                onClick = {
                    // TODO: Implementar navegación a pantalla de alertas cuando esté disponible
                }
            )
        }

        // Ítem de Refugios
        BottomNavItem(
            active = currentRoute == Screen.Shelters.route,
            label = "Refugios",
            icon = Lucide.House,
            onClick = {
                navController.navigate(Screen.Shelters.route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Ítem de Reporte
        BottomNavItem(
            active = false,
            label = "Reporte",
            icon = Lucide.Pencil,
            onClick = {
                // Si no hay usuario, va al Login. Si hay, va a la pantalla de crear reporte
                if (userRole == null) {
                    navController.navigate(Screen.Login.route)
                } else {
                    navController.navigate(Screen.Report.route)
                }
            }
        )

        // Ítem de Perfil
        // Si el usuario está logueado, va a Profile. Si no, va a Login.
        BottomNavItem(
            active = currentRoute == Screen.Profile.route,
            label = "Perfil",
            icon = Lucide.User,
            onClick = {
                if (userRole == null) {
                    // Si no hay usuario, va al Login
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                } else {
                    // Si hay usuario (admin o user), va a Profile
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}

/**
 * Item individual de la barra de navegación inferior.
 * 
 * Muestra un ícono, una etiqueta y opcionalmente un badge.
 * Cambia de color según si está activo o no.
 * 
 * @param active Indica si este ítem está activo (pantalla actual)
 * @param label Texto a mostrar debajo del ícono
 * @param badge Texto opcional del badge a mostrar en la esquina superior derecha
 * @param icon Ícono a mostrar
 * @param onClick Función a ejecutar cuando se hace clic en el ítem
 * @param modifier Modificador de Compose para personalizar el layout
 */
@Composable
fun BottomNavItem(
    active: Boolean,
    label: String,
    badge: String? = null,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(width = 64.dp, height = 56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = false,
                    radius = 30.dp,
                    color = Color(0xFF0891B2)
                ),
                onClick = onClick
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (active) Color(0xFF0891B2) else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )

                // Badge opcional para mostrar notificaciones o contadores
                if (badge != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .offset(x = 12.dp, y = (-6).dp)
                            .shadow(1.dp, RoundedCornerShape(10.dp))
                            .background(Color(0xFFEF4444), shape = RoundedCornerShape(10.dp))
                            .widthIn(min = 18.dp)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            badge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Default,
                            lineHeight = 1.em
                        )
                    }
                }
            }

            Text(
                label,
                fontSize = 11.sp,
                color = if (active) Color(0xFF0891B2) else Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
