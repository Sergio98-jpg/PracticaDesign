package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

/* -------------------------
   Stats Cards Row
   ------------------------- */
@Preview(showBackground = true)
@Composable
fun PreviewStatsRow() {
    StatsRow()
}
@Composable
fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(number = "1", label = "Alerta activa", type = StatType.ALERT, modifier = Modifier.weight(1f))
        StatCard(number = "5", label = "Refugios cerca", type = StatType.SHELTER, modifier = Modifier.weight(1f))
        StatCard(number = "12", label = "Zonas seguras", type = StatType.SAFE, modifier = Modifier.weight(1f))
    }
}

enum class StatType { ALERT, SHELTER, SAFE }

@Composable
fun StatCard(number: String, label: String, type: StatType, modifier: Modifier = Modifier) {
    val color = when (type) {
        StatType.ALERT -> Color(0xFFEF4444)
        StatType.SHELTER -> Color(0xFF0891B2)
        StatType.SAFE -> Color(0xFF10B981)
    }

    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(number, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 4.dp), maxLines = 2)
    }
}