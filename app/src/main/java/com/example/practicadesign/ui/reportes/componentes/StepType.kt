package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Paso 1: Selección del tipo de reporte.
 * 
 * Muestra una cuadrícula con los diferentes tipos de reportes disponibles.
 * El usuario puede seleccionar uno de los tipos para continuar.
 * 
 * @param selectedType Tipo de reporte actualmente seleccionado (null si ninguno)
 * @param onSelect Callback cuando se selecciona un tipo de reporte
 */
@Preview(showBackground = true)
@Composable
fun StepTypeScreenPreview() {
    StepType(selectedType = null, onSelect = {})
}

@Composable
fun StepType(
    selectedType: String?,
    onSelect: (String) -> Unit
) {
    val types = listOf(
        "inundacion" to "Inundación" to "💧",
        "calle-bloqueada" to "Calle Bloqueada" to "🚧",
        "refugio-lleno" to "Refugio Lleno" to "🏠",
        "dano-infraestructura" to "Daño a Infraestructura" to "⚠️",
        "persona-riesgo" to "Persona en Riesgo" to "🆘",
        "otro" to "Otro" to "📝"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "¿Qué deseas reportar?",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Selecciona el tipo de incidente que quieres reportar",
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                items(types) { triple ->
                    val key = triple.first.first
                    val name = triple.first.second
                    val emoji = triple.second
                    val selected = selectedType == key

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clickable { onSelect(key) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) Color(0xFFF0FDFA) else Color.White
                        ),
                        border = BorderStroke(
                            2.dp,
                            if (selected) Color(0xFF0891B2) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) Brush.linearGradient(
                                            listOf(
                                                Color(0xFF0891B2),
                                                Color(0xFF06B6D4)
                                            )
                                        ) else SolidColor(Color(0xFFF1F5F9))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = name,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(120.dp))
    }
}
