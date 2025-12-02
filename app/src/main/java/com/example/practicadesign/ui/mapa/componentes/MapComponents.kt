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
import com.example.practicadesign.ui.theme.PracticaDesignTheme
import com.example.practicadesign.ui.theme.*



/**
 * Preview del menú de filtros completo.
 */
@Preview(showBackground = true, name = "Menú de Filtros Completo")
@Composable
fun PreviewFilterMenu() {
    PracticaDesignTheme(darkTheme = false) {
        Box(modifier = Modifier.background(Color.LightGray)) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = { },
                modifier = Modifier.width(250.dp)
            ) {
                Text(
                    text = "Capas del mapa",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                FilterMenuItem(
                    text = "Mostrar Refugios",
                    checked = true,
                    onCheckedChange = {}
                )
                FilterMenuItem(
                    text = "Mostrar Zonas de Riesgo",
                    checked = false,
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
                    checkedThumbColor = checkCirculoColor(),
                    checkedTrackColor = checkBagColor(),
                    uncheckedThumbColor = checkBagColor(),
                    uncheckedTrackColor = checkCirculoColor(),
                    uncheckedBorderColor = outlineVariantColor()
                ),
                onCheckedChange = null // El onClick del DropdownMenuItem maneja el cambio
            )
        }
    )
}