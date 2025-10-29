package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Shell


@Preview(showBackground = true, name = "Estado Seguro")
@Composable
fun PreviewFloatingSearchButton() {
    FloatingSearchButton()
}
@Composable
fun FloatingSearchButton(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .clickable { onSearchClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ícono de búsqueda (puedes cambiarlo por Lucide.Search si usas esa librería)
        Icon(
            painter = rememberVectorPainter(Lucide.Search),
            contentDescription = "Buscar",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = "Buscar zona o dirección...",
            color = Color(0xFF94A3B8),
            fontSize = 15.sp
        )
    }
}
