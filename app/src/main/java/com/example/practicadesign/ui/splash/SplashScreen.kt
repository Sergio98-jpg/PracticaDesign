package com.example.practicadesign.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.practicadesign.R
import kotlin.random.Random

//Version 3
@Composable
fun SplashYaanalHaHybrid() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1a2980), Color(0xFF26d0ce))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Fondo animado combinado
        Box(Modifier.fillMaxSize()) {
            RainBackground()   // 🌧️ Lluvia del diseño 1
            Ripples()          // 🌊 Ondas del diseño 2
            RisingBubbles()    // 🫧 Burbujas del diseño 2
        }

        // Contenido principal
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .zIndex(10f)
                .padding(16.dp)
        ) {
            LogoSectionHybrid()
        }
    }
}
@Composable
fun RainBackground() {
    val drops = remember { List(100) { RainDrop() } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        drops.forEach { drop ->
            AnimatedRainDrop(drop)
        }
    }
}

data class RainDrop(
    val left: Float = Random.nextFloat(),
    val delay: Float = Random.nextFloat() * 2f,
    val duration: Float = Random.nextFloat() * 1000f + 500f
)

@Composable
fun AnimatedRainDrop(drop: RainDrop) {
    val infinite = rememberInfiniteTransition(label = "rain")
    val y by infinite.animateFloat(
        initialValue = -100f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(drop.duration.toInt(), easing = LinearEasing, delayMillis = (drop.delay * 1000).toInt())
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .absoluteOffset(x = (drop.left * LocalConfiguration.current.screenWidthDp).dp, y = y.dp)
            .width(2.dp)
            .height(60.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.8f))
                ),
                alpha = 0.6f
            )
    )
}
@Composable
fun Ripples() {
    val infinite = rememberInfiniteTransition(label = "")
    val delays = listOf(0, 500, 1000)

    delays.forEachIndexed { index, delay ->
        val size by infinite.animateFloat(
            initialValue = 20f,
            targetValue = 250f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing, delayMillis = delay)
            ),
            label = "ripple$index"
        )

        val opacity by infinite.animateFloat(
            initialValue = 0.8f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, delayMillis = delay)
            ),
            label = "opacity$index"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(size.dp, (size * 0.25f).dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-80).dp)
                    // --- SOLUCIÓN: Reemplazar CircleShape por una forma de Óvalo ---
                    .clip(GenericShape { size, _ ->
                        addOval(Rect(0f, 0f, size.width, size.height))
                    })
                    .background(Color(0xFFA0D2FF).copy(alpha = 0.6f * opacity))
            )
        }
    }
}
@Composable
fun RisingBubbles() {
    val bubbles = remember { List(5) { BubbleData() } }
    val infinite = rememberInfiniteTransition(label = "")

    bubbles.forEachIndexed { i, bubble ->
        val offsetY by infinite.animateFloat(
            initialValue = 1000f,
            targetValue = -100f,
            animationSpec = infiniteRepeatable(
                animation = tween(bubble.duration, easing = LinearEasing)
            ),
            label = "bubble$i"
        )

        Box(
            modifier = Modifier
                .absoluteOffset(
                    x = (bubble.xPercent * LocalConfiguration.current.screenWidthDp).dp,
                    y = offsetY.dp
                )
                .size(bubble.size.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
        )
    }
}

data class BubbleData(
    val xPercent: Float = listOf(0.1f, 0.25f, 0.45f, 0.65f, 0.8f).random(),
    val size: Int = listOf(10, 12, 15, 18).random(),
    val duration: Int = (10000..14000).random()
)
@Composable
fun LogoSectionHybrid() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .shadow(20.dp, CircleShape),
            contentAlignment = Alignment.Center //Quitar para quitar circulo del logo
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // tu recurso
                contentDescription = "Logo",
                //  modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(90.dp).clip(CircleShape) //Quitar para quitar circulo del logo
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Yáanal Ha'",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = Offset(0f, 2f),
                    blurRadius = 10f
                )
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Sistema de Inundación",
            fontSize = 20.sp,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(0f, 2f),
                    blurRadius = 8f
                )
            )
        )
    }
}