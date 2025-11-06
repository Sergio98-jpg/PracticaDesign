package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



/**
 * Preview del menú de filtros completo.
 */
@Preview(showBackground = true, name = "Menú de Filtros Completo")
@Composable
fun PreviewFilterMenu() {
    Box(modifier = Modifier.background(Color.LightGray)) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = { },
            modifier = Modifier.width(250.dp)
        ) {
            Text(
                text = "Capas del mapa",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF050505),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            FilterMenuItem(
                text = "Mostrar Refugios",
                checked = true,
                onCheckedChange = {}
            )
            FilterMenuItem(
                text = "Mostrar Zonas de Riesgo",
                checked = true,
                onCheckedChange = {}
            )
            HorizontalDivider()
            FilterMenuItem(
                text = "Solo refugios abiertos",
                checked = false,
                onCheckedChange = {}
            )
        }
    }
}

/**
 * Item individual del menú de filtros con un switch.
 * 
 * Muestra un texto y un switch que permite activar/desactivar el filtro.
 * 
 * @param text Texto a mostrar
 * @param checked Estado actual del switch
 * @param onCheckedChange Función a ejecutar cuando cambia el estado del switch
 */
@Composable
fun FilterMenuItem(
    text: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = onCheckedChange,
        leadingIcon = {
            Switch(
                checked = checked,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF0E7490),
                    uncheckedThumbColor = Color(0xFFE2E8F0),
                    uncheckedTrackColor = Color(0xFFF1F5F9),
                    uncheckedBorderColor = Color(0xFFCBD5E1)
                ),
                onCheckedChange = null // El onClick del DropdownMenuItem maneja el cambio
            )
        }
    )
}