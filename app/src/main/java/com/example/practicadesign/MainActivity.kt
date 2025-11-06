package com.example.practicadesign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicadesign.ui.main.MainViewModel
import com.example.practicadesign.ui.navegacion.AppNavigation
import com.example.practicadesign.ui.splash.SplashYaanalHaHybrid
import com.example.practicadesign.ui.theme.PracticaDesignTheme


/**
 * MainActivity es la actividad principal de la aplicación.
 * Se encarga de la configuración inicial y la configuración del sistema de ventanas.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Instala el splash screen nativo de Android.
        // Se mostrará brevemente antes de que se renderice el contenido de Compose.
        installSplashScreen()
        
        // Configura el sistema de ventanas para que el contenido se ajuste correctamente
        WindowCompat.setDecorFitsSystemWindows(window, true)
        
        // Habilita el modo edge-to-edge para que la app use toda la pantalla
        enableEdgeToEdge()
        
        setContent {
            // Función Composable principal que contiene toda la UI de la aplicación
            PracticaDesignApp()
        }
    }
}
/**
 * Función Composable principal de la aplicación.
 * Gestiona el estado del splash screen y la navegación principal usando MVVM.
 * 
 * Utiliza MainViewModel para gestionar el estado del splash screen,
 * siguiendo el patrón de arquitectura MVVM establecido en la aplicación.
 */
@Preview(showBackground = true)
@Composable
fun PracticaDesignApp(
    viewModel: MainViewModel = viewModel()
) {
    // Observa el estado del splash screen desde el ViewModel
    val isSplashVisible by viewModel.isSplashVisible.collectAsState()

    PracticaDesignTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Muestra el splash screen con animación de entrada/salida
            AnimatedVisibility(
                visible = isSplashVisible,
                enter = fadeIn(animationSpec = tween(800)),
                exit = fadeOut(animationSpec = tween(800))
            ) {
                SplashYaanalHaHybrid()
            }

            // Muestra la navegación principal cuando el splash se oculta
            AnimatedVisibility(
                visible = !isSplashVisible,
                enter = fadeIn(animationSpec = tween(800))
            ) {
                AppNavigation()
            }
        }
    }
}
