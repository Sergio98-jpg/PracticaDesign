package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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

import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector

// --- IMPORTACIÓN CORRECTA PARA LUCIDE ICONS ---
 import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Map
import com.composables.icons.lucide.User


import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import com.composables.icons.lucide.House

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.unit.em


import androidx.compose.foundation.interaction.MutableInteractionSource // <-- AÑADE ESTE IMPORT
import androidx.compose.runtime.remember // <-- AÑADE ESTE IMPORT
import androidx.compose.foundation.shape.CircleShape // <-- AÑADE ESTE IMPORT
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ripple
import androidx.navigation.NavController
import com.composables.icons.lucide.Newspaper
import com.composables.icons.lucide.Pencil

import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontFamily
import com.example.practicadesign.ui.navegacion.Screen

/* -------------------------
   Bottom Navigation
   ------------------------- */
@Preview(showBackground = true)
@Composable
fun PreviewBottomNav() {
    BottomNav(
        navController = TODO(),
        userRole = TODO()
    )
}
//@Composable
// fun BottomNav( modifier: Modifier = Modifier) {
@Composable
fun BottomNav(
    modifier: Modifier = Modifier,
    navController: NavController, // ✅ 1. Recibe el NavController para poder navegar
    userRole: String? // ✅ 2. Recibe el rol del usuario (puede ser null)
){
    // ✅ 3. Obtenemos la ruta actual para saber qué ítem está activo
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
/*        BottomNavItem(active = true,  label = "Mapa",      icon = Icons.Filled.Place)
        BottomNavItem(active = false, label = "Alertas",   icon = Icons.Filled.Notifications, badge = "1")
        BottomNavItem(active = false, label = "Refugios",  icon = Icons.Filled.Home)
        BottomNavItem(active = false, label = "Perfil",    icon = Icons.Filled.Person)
        BottomNavItem(active = true,  label = "Mapa",      icon = Lucide.Map)
        BottomNavItem(active = false, label = "Alertas",   icon = Lucide.CircleAlert, badge = "1")
        BottomNavItem(active = false, label = "Refugios",  icon = Lucide.House)
        BottomNavItem(active = false, label = "Reporte",  icon = Lucide.Pencil)
        BottomNavItem(active = false, label = "Perfil",    icon = Lucide.User)*/

    // Ítem del Mapa
    BottomNavItem(
        active = currentRoute == Screen.Mapa.route, // Activo si la ruta actual es la del mapa
        label = "Mapa",
        icon = Lucide.Map,
        onClick = { navController.navigate(Screen.Mapa.route){
            // ✅ Evita crear una nueva copia del mapa si ya está en la pila
            launchSingleTop = true
            // ✅ Restaura el estado al volver a esta pantalla
            restoreState = true
        } } // Navega al mapa
    )

    // Ítem de Alertas (Supongamos que es solo para usuarios logueados)
    if (userRole != null) {
        BottomNavItem(
            active =  false, //currentRoute == Screen.Alerts.route,
            label = "Alertas",
            icon = Lucide.CircleAlert,
            badge = "1",
            onClick = { /*navController.navigate(Screen.Alerts.route)*/ }
        )
    }

    // Ítem de Refugios
    BottomNavItem(
        active = currentRoute == Screen.Shelters.route,
        label = "Refugios",
        icon = Lucide.House,
        onClick = { navController.navigate(Screen.Shelters.route){
            // ✅ Evita crear una nueva copia del mapa si ya está en la pila
            launchSingleTop = true
            // ✅ Restaura el estado al volver a esta pantalla
            restoreState = true
        } }
    )

    // Ítem de Reporte (El que inicia el login si no se ha hecho)
    BottomNavItem(
        active = false, // Este botón nunca está "activo" como una pantalla
        label = "Reporte",
        icon = Lucide.Pencil,
        onClick = {
            // LÓGICA CLAVE: Si no hay usuario, va al Login. Si hay, va a la pantalla de crear reporte.
            if (userRole == null) {
                navController.navigate(Screen.Login.route)
            } else {
                // navController.navigate(Screen.CreateReport.route) // Futura pantalla
            }
        }
    )

    // Ítem de Perfil (Supongamos que es solo para usuarios logueados)
    if (userRole != null) {
        BottomNavItem(
            active = false,//currentRoute == Screen.Profile.route,
            label = "Perfil",
            icon = Lucide.User,
            onClick = { /*navController.navigate(Screen.Profile.route) */}
        )
    }
    }
}

@Composable
fun BottomNavItem(active: Boolean, label: String, badge: String? = null, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center, // Centrará el contenido visual (icono y texto)
        modifier = modifier
            // 2. DEFINE EL ÁREA DE TOQUE GRANDE Y EL RIPPLE AQUÍ.
            .size(width = 64.dp, height = 56.dp) // Un área generosa pero no exagerada
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = false, // ¡Esencial para el efecto de halo!
                    radius = 30.dp,  // Un radio grande y agradable
                    color = Color(0xFF0891B2)
                ),
                onClick = onClick
            )
    )  {
        // 3. DENTRO DEL BOX DE TOQUE, COLOCA EL CONTENIDO VISUAL.
        //    Este Column ya no tiene el clickable.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // Vertical arrangement para que no haya espacio extra
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (active) Color(0xFF0891B2) else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )

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
                fontWeight = FontWeight.Medium,
                // Puedes añadir un padding mínimo si lo ves necesario
                // modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
