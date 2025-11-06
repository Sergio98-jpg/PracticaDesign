package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.draw.clip // <-- AÑADE ESTE IMPORT
import androidx.compose.runtime.remember // <-- AÑADE ESTE IMPORT
import androidx.compose.foundation.shape.CircleShape // <-- AÑADE ESTE IMPORT
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import com.composables.icons.lucide.AlarmClock
import com.composables.icons.lucide.CircleAlert

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Shell
import com.composables.icons.lucide.Shield
import com.composables.icons.lucide.Siren

//Zona de riesgo
import com.google.android.gms.maps.model.LatLng

//Web socket
import kotlinx.serialization.Serializable


/* -------------------------
   Estado del Banner
   ------------------------- */
enum class BannerState {
    Safe,
    Warning,
    Danger
}

/* -------------------------
   Zona de riesgo (Estructura)
   ------------------------- *//*
@Serializable
data class RiskZone(
    val id: String,
    val area: List<LatLng>, // La forma de la zona (una lista de coordenadas)
    val state: BannerState  // El tipo de zona (Warning o Danger)
)

@Serializable
data class LatLngDto(val lat: Double, val lng: Double)

@Serializable
data class RiskZoneDto(
    val id: String,
    val area: List<LatLngDto>,
    val state: BannerState
)*/
/* -------------------------
   Refugios (Estructura)
   ------------------------- *//*

data class Shelter(
    val id: String,
    val position: LatLng,
    val name: String,
    val isOpen: Boolean,
    val address: String,
    val capacity: Int,
    val currentOccupancy: Int
)*/

/* -------------------------
   Calles (Estructura)
   ------------------------- *//*

data class FloodedStreet(
    val id: String,
    val path: List<LatLng> // El camino de la calle
)*/
/* -------------------------
   Status Banner
   ------------------------- */
@Preview(showBackground = true, name = "Estado Seguro")
@Composable
fun PreviewStatusBannerSafe() {
    StatusBanner(state = BannerState.Safe)
}

@Preview(showBackground = true, name = "Estado Precaución")
@Composable
fun PreviewStatusBannerWarning() {
    StatusBanner(state = BannerState.Warning)
}

@Preview(showBackground = true, name = "Estado Peligro")
@Composable
fun PreviewStatusBannerDanger() {
    StatusBanner(state = BannerState.Danger)
}

@Composable
fun StatusBanner(state: BannerState, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {

    // 2. DEFINE LAS VARIABLES QUE CAMBIARÁN
    val gradient: Brush
    val title: String
    val icon: @Composable () -> Unit // Usamos un Composable para máxima flexibilidad
    val trophyIcon: @Composable () -> Unit

    // 3. USA 'when' PARA ASIGNAR VALORES SEGÚN EL ESTADO
    when (state) {
        BannerState.Safe -> {
            title = "Área Segura"
            gradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
            icon = { CheckIcon() } // Ícono de Check
            trophyIcon = { TrophyIcon() } // Ícono de Trofeo
        }
        BannerState.Warning -> {
            title = "Precaución"
            gradient = Brush.linearGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
            icon = { AlertIcon() } // Ícono de Alerta
            trophyIcon = { ShieldIcon() } // Ícono de Escudo
        }
        BannerState.Danger -> {
            title = "Peligro"
            gradient = Brush.linearGradient(listOf(Color(0xFFF87171), Color(0xFFDC2626)))
            icon = { AlertIcon(color = Color.White) } // Ícono de Alerta en blanco
            trophyIcon = { SkullIcon() } // Ícono de Calavera
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            // 1. AÑADE EL CLIP AQUÍ. Es crucial para contener el ripple.
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick) // 2. USA EL CLICKABLE MÁS SIMPLE.
            .background(
                brush = gradient,
                shape = RoundedCornerShape(16.dp) // El background mantiene su forma
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Estado de tu zona", fontSize = 12.sp, color = Color.White.copy(alpha = 0.95f), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold) // <-- Usa la variable
                Spacer(Modifier.width(8.dp))
                icon() // <-- Usa el Composable del ícono
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            trophyIcon() // <-- Usa el Composable del ícono de la derecha
        }
    }
}

@Composable
private fun CheckIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        drawPath(
            path = Path().apply {
                moveTo(size.width * 0.85f, size.height * 0.25f)
                lineTo(size.width * 0.4f, size.height * 0.75f)
                lineTo(size.width * 0.2f, size.height * 0.55f)
            },
            color = Color.White,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun AlertIcon(color: Color = Color.White) {
    Text("⚠️", fontSize = 18.sp) // Simple y efectivo, o usa un Canvas si prefieres
}

@Composable
private fun TrophyIcon() {
    Icon(
        imageVector = Lucide.Shield,
        contentDescription = null,
        tint = Color(0xFFFFFFFF),
        modifier = Modifier.size(24.dp)
    )
   // Text("🏆", fontSize = 20.sp)
}

@Composable
private fun ShieldIcon() {
    Icon(
        imageVector = Lucide.CircleAlert,
        contentDescription = null,
        tint = Color(0xFFFFFFFF),
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun SkullIcon() {
    Icon(
        imageVector = Lucide.Siren,
        contentDescription = null,
        tint = Color(0xFFFFFFFF),
        modifier = Modifier.size(24.dp)
    )
}

