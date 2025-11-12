package com.example.practicadesign.ui.profile.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Fila de tarjetas de estadísticas que muestra reportes enviados y alertas recibidas.
 * 
 * @param reportsSent Número de reportes enviados
 * @param alertsReceived Número de alertas recibidas
 */
@Composable
fun StatsRow(
    reportsSent: Int,
    alertsReceived: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            value = reportsSent.toString(),
            label = "Reportes enviados",
            color = Color(0xFF00A9E0), // Azul
            icon = Icons.Outlined.Upload
        )
        StatCard(
            value = alertsReceived.toString(),
            label = "Alertas recibidas",
            color = Color(0xFF34B579), // Verde
            icon = Icons.Outlined.Notifications
        )
    }
}

/**
 * Tarjeta de estadística individual.
 * 
 * @param value Valor numérico a mostrar
 * @param label Etiqueta descriptiva
 * @param color Color del número, icono y borde
 * @param icon Ícono a mostrar al lado del número
 */
@Composable
private fun RowScope.StatCard(
    value: String,
    label: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fila con número e ícono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

