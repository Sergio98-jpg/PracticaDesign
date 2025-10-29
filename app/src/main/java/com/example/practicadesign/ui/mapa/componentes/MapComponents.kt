package com.example.practicadesign.ui.mapa.componentes

import android.widget.Switch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.em



// Reemplaza tu PreviewFilterMenuItem con estas dos
@Preview(showBackground = true, name = "Menú de Filtros Completo")
@Composable
fun PreviewFilterMenu() {
    // Para que el DropdownMenu tenga un ancla y tamaño,
    // lo envolvemos en un Box. Le damos un fondo blanco
    // para que el menú (que también es blanco) no se pierda.
    Box(modifier = Modifier.background(Color.LightGray)) {
        DropdownMenu(
            expanded = true, // 1. Forzamos el menú a estar expandido para la preview
            onDismissRequest = { }, // 2. Función vacía, no se puede cerrar en la preview
            modifier = Modifier.width(250.dp) // Damos un ancho fijo para un look realista
        ) {
            Text(
                text = "Capas del mapa",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF050505),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
           // Spacer(modifier = Modifier.height(4.dp))
            // 3. Añadimos varios FilterMenuItem para simular el menú real
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
            HorizontalDivider() // Un divisor para separar secciones
            FilterMenuItem(
                text = "Solo refugios abiertos",
                checked = false,
                onCheckedChange = {}
            )
        }
    }
}

/*@Preview(showBackground = true, name = "Filter Menu Item - Unchecked")
@Composable
fun PreviewFilterMenuItemUnchecked() {
    Column {
        FilterMenuItem(
            text = "Mostrar Refugios",
            checked = false, // 1. Proporciona el estado "desmarcado"
            onCheckedChange = {}   // 2. Proporciona una función lambda vacía
        )
    }
}*/

@Composable
fun FilterMenuItem(
    text: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = onCheckedChange, // El clic en toda la fila cambia el estado
        leadingIcon = {
            Switch(
                checked = checked,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White, // Color del pulgar cuando está activado
                    checkedTrackColor = Color(0xFF0E7490), // Color de la pista cuando está activado
                    uncheckedThumbColor = Color(0xFFE2E8F0), // Color del pulgar cuando está desactivado
                    uncheckedTrackColor = Color(0xFFF1F5F9), // Color de la pista cuando está desactivado
                    uncheckedBorderColor = Color(0xFFCBD5E1) // Color del borde cuando está desactivado
                ),
                // Puedes dejar onCheckedChange como nulo aquí, ya que el onClick del
                // DropdownMenuItem ya maneja el cambio. Esto evita que el Switch
                // tenga su propia lógica de clic separada.
                onCheckedChange = null
            )
/*            Checkbox(
                checked = checked,
                onCheckedChange = { onCheckedChange() }
            )*/
        }
    )
}