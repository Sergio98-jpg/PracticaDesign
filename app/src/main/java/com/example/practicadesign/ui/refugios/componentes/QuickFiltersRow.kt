package com.example.practicadesign.ui.refugios.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practicadesign.ui.mapa.MapScreen
//import com.example.practicadesign.ui.refugios.ShelterFilter


// ... (imports)

// --- VISTAS PREVIAS (PREVIEWS) ---

@Preview(showBackground = true, name = "Filtros (Todos seleccionados)")
@Composable
private fun QuickFiltersRowAllPreview() {
    QuickFiltersRow(
        selectedFilter = ShelterFilter.ALL,
        onFilterSelected = {}
    )
}

@Preview(showBackground = true, name = "Filtros (Disponibles seleccionados)")
@Composable
private fun QuickFiltersRowAvailablePreview() {
    QuickFiltersRow(
        selectedFilter = ShelterFilter.AVAILABLE,
        onFilterSelected = {}
    )
}

@Composable
fun QuickFiltersRow(
    selectedFilter: ShelterFilter,
    onFilterSelected: (ShelterFilter) -> Unit
) {
    val filters = listOf(
        ShelterFilter.ALL to "Todos",
        ShelterFilter.OPEN to "Abiertos",
        ShelterFilter.NEAREST to "Más cercanos",
        ShelterFilter.AVAILABLE to "Con espacio"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (filter, label) ->
            val active = selectedFilter == filter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (active) Color(0xFF0891B2) else Color.White)
                    .border(
                        width = 2.dp,
                        color = if (active) Color(0xFF0891B2) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (active) Color.White else Color(0xFF475569),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

/* -------------------------------------------------
   ENUM DE FILTROS Y VIEWMODEL
------------------------------------------------- */
enum class ShelterFilter { ALL, OPEN, NEAREST, AVAILABLE }