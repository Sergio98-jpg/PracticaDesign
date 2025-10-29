package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* -------------------------
   Floating Logo (top-left)
   ------------------------- */
@Preview(showBackground = true)
@Composable
fun PreviewFloatingLogo() {
    FloatingLogo()
}
@Composable
fun FloatingLogo(modifier: Modifier = Modifier, location: String = "Ubicación de Prueba") {
    Row(
        modifier = modifier
            .wrapContentSize()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(brush = Brush.linearGradient(listOf(Color(0xFF0891B2), Color(0xFF06B6D4))), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Replace with your svg asset if available
            // Example: Icon(painter = painterResource(R.drawable.ic_logo), contentDescription = null, tint = Color.White)
            Text("💧", fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = "Yáanal Ha'", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          //  Text(text = "Mérida, YUC", fontSize = 12.sp, color = Color(0xFF64748B))
            Text(text = location, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}
