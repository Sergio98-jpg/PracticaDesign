package com.example.practicadesign.ui.profile.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Sección de opciones de configuración del perfil.
 * 
 * @param onLogout Callback cuando se presiona el botón de cerrar sesión
 */
@Composable
fun SettingsSection(onLogout: () -> Unit) {
    val settingItems = listOf(
        SettingItemData(
            icon = Icons.Outlined.AccountCircle,
            title = "Información personal",
            color = Color(0xFF3B82F6) // Azul
        ),
        SettingItemData(
            icon = Icons.Outlined.Notifications,
            title = "Notificaciones",
            color = Color(0xFF34B579) // Verde
        ),
        SettingItemData(
            icon = Icons.Outlined.Security,
            title = "Seguridad",
            color = Color(0xFF8B5CF6) // Morado
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Opciones de configuración
        settingItems.forEach { item ->
            SettingItem(
                item = item,
                onClick = { /* TODO: Implementar navegación a cada opción */ }
            )
        }

        // Botón de cerrar sesión
        LogoutButton(onClick = onLogout)
    }
}

/**
 * Data class que representa un ítem de configuración.
 * 
 * @param icon Ícono a mostrar
 * @param title Título del ítem
 * @param color Color del ícono y del círculo de fondo
 */
private data class SettingItemData(
    val icon: ImageVector,
    val title: String,
    val color: Color
)

/**
 * Ítem individual de configuración.
 * 
 * @param item Datos del ítem a mostrar
 * @param onClick Callback cuando se presiona el ítem
 */
@Composable
private fun SettingItem(
    item: SettingItemData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Círculo con ícono de color
                Box(
                    modifier = Modifier
                        .background(
                            item.color.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A)
                )
            }
            // Chevron derecho
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Botón de cerrar sesión con estilo especial (rojo con borde).
 * 
 * @param onClick Callback cuando se presiona el botón
 */
@Composable
private fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFFE53E3E)
        ),
        border = BorderStroke(1.dp, Color(0xFFE53E3E))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = Color(0xFFE53E3E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Cerrar Sesión",
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE53E3E)
                )
            }
            // Chevron derecho también en el botón de cerrar sesión
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFE53E3E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

