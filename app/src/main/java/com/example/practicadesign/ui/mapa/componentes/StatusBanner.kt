package com.example.practicadesign.ui.mapa.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Shield
import com.composables.icons.lucide.Siren

/**
 * Estado del banner de riesgo que muestra el nivel de peligro de la zona actual.
 */
enum class BannerState {
    Safe,
    Warning,
    Danger
}
/**
 * Preview del banner de estado en estado seguro.
 */
@Preview(showBackground = true, name = "Estado Seguro")
@Composable
fun PreviewStatusBannerSafe() {
    StatusBanner(state = BannerState.Safe)
}

/**
 * Preview del banner de estado en estado de precaución.
 */
@Preview(showBackground = true, name = "Estado Precaución")
@Composable
fun PreviewStatusBannerWarning() {
    StatusBanner(state = BannerState.Warning)
}

/**
 * Preview del banner de estado en estado de peligro.
 */
@Preview(showBackground = true, name = "Estado Peligro")
@Composable
fun PreviewStatusBannerDanger() {
    StatusBanner(state = BannerState.Danger)
}

/**
 * Banner de estado que muestra el nivel de riesgo de la zona actual del usuario.
 * 
 * Muestra un gradiente de color y un ícono según el estado:
 * - Safe: Verde con ícono de check y escudo
 * - Warning: Amarillo con ícono de alerta y escudo
 * - Danger: Rojo con ícono de alerta y sirena
 * 
 * @param state Estado actual del banner (Safe, Warning, Danger)
 * @param modifier Modificador de Compose para personalizar el layout
 * @param onClick Función a ejecutar cuando se hace clic en el banner
 */
@Composable
fun StatusBanner(state: BannerState, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val gradient: Brush
    val title: String
    val icon: @Composable () -> Unit
    val trophyIcon: @Composable () -> Unit

    when (state) {
        BannerState.Safe -> {
            title = "Área Segura"
            gradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
            icon = { CheckIcon() }
            trophyIcon = { TrophyIcon() }
        }
        BannerState.Warning -> {
            title = "Precaución"
            gradient = Brush.linearGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
            icon = { AlertIcon() }
            trophyIcon = { ShieldIcon() }
        }
        BannerState.Danger -> {
            title = "Peligro"
            gradient = Brush.linearGradient(listOf(Color(0xFFF87171), Color(0xFFDC2626)))
            icon = { AlertIcon(color = Color.White) }
            trophyIcon = { SkullIcon() }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(
                brush = gradient,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "Estado de tu zona",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                icon()
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            trophyIcon()
        }
    }
}

/**
 * Ícono de check personalizado para el banner de estado seguro.
 */
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

/**
 * Ícono de alerta para el banner de estado de precaución o peligro.
 * 
 * @param color Color del ícono (por defecto blanco)
 */
@Composable
private fun AlertIcon(color: Color = Color.White) {
    Text("⚠️", fontSize = 18.sp)
}

/**
 * Ícono de escudo para el banner de estado seguro.
 */
@Composable
private fun TrophyIcon() {
    Icon(
        imageVector = Lucide.Shield,
        contentDescription = null,
        tint = Color(0xFFFFFFFF),
        modifier = Modifier.size(24.dp)
    )
}

/**
 * Ícono de escudo de alerta para el banner de estado de precaución.
 */
@Composable
private fun ShieldIcon() {
    Icon(
        imageVector = Lucide.CircleAlert,
        contentDescription = null,
        tint = Color(0xFFFFFFFF),
        modifier = Modifier.size(24.dp)
    )
}

/**
 * Ícono de sirena para el banner de estado de peligro.
 */
@Composable
private fun SkullIcon() {
    Icon(
        imageVector = Lucide.Siren,
        contentDescription = null,
        tint = Color(0xFFFFFFFF),
        modifier = Modifier.size(24.dp)
    )
}

