// en /ui/navigation/AppNavigation.kt
package com.example.practicadesign.ui.navegacion

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practicadesign.ui.auth.AuthViewModel
import com.example.practicadesign.ui.mapa.MapScreen // <-- Importa tu pantalla
import com.example.practicadesign.ui.login.LoginScreen
import com.example.practicadesign.ui.main.MainScreen
import com.example.practicadesign.ui.mapa.componentes.BottomNav

import androidx.compose.animation.fadeIn // ✅ Importar
import androidx.compose.animation.fadeOut // ✅ Importar
import androidx.compose.animation.slideInHorizontally // ✅ Importar
import androidx.compose.animation.slideOutHorizontally // ✅ Importar
import androidx.compose.animation.core.tween // ✅ Importar
import com.example.practicadesign.ui.refugios.SheltersScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // ViewModel para compartir el estado de login entre pantallas
    val authViewModel: AuthViewModel = viewModel()
    val userRole by authViewModel.userRole.collectAsState() // Observa el rol

    // Usamos un Scaffold como contenedor principal que muestra la barra de navegación
    Scaffold(
        bottomBar = {
            // ✅ Usa TU BottomNav aquí, pasándole el estado y el controlador
            BottomNav(
                modifier = Modifier
                   // .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(80.dp),
                navController = navController,
                userRole = userRole
            )
        }
    ) { innerPadding ->
        // NavHost ahora vive dentro del Scaffold, y el padding evita que el contenido
        // se dibuje debajo de la barra de navegación.
        NavHost(
            navController = navController,
            startDestination = Screen.Mapa.route,
            modifier = Modifier.padding(innerPadding) // ✅ APLICA EL PADDING
        ) {
            val fadeSpec = tween<Float>(300)
            // RUTA PARA LA PANTALLA PRINCIPAL (MAPA)
            /*composable(Screen.Mapa.route) {
                MapScreen(navController = navController),
            }

            // RUTA PARA LA PANTALLA DE REFUGIOS
            composable(Screen.Shelters.route) {
              //  SheltersScreen()
            }

            // RUTA PARA LA PANTALLA DE LOGIN
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { newRole ->
                        authViewModel.onLoginSuccess(newRole)
                        navController.popBackStack() // Cierra el login y vuelve al mapa
                    }
                )
            }*/
            // Aquí irían las otras pantallas como Alerts, Profile, etc.

            composable(
                Screen.Mapa.route,
                enterTransition = { fadeIn(animationSpec = fadeSpec) },
                exitTransition = { fadeOut(animationSpec = fadeSpec) }
            ) {
                MapScreen(navController = navController)
            }

            composable(
                Screen.Shelters.route,
                enterTransition = { fadeIn(animationSpec = fadeSpec) },
                exitTransition = { fadeOut(animationSpec = fadeSpec) }
            ) {
                SheltersScreen()
            }

            // --- Opción 2: Desplazamiento Horizontal para Login ---
            // A veces quieres una animación diferente para una pantalla específica.
            composable(
                route = Screen.Login.route,
                enterTransition = {
                    // La pantalla de Login entra deslizándose desde la derecha
                    slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth })
                },
                exitTransition = {
                    // La pantalla de Login sale deslizándose hacia la derecha
                    slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                }
            ) {
                LoginScreen(
                    onLoginSuccess = { newRole ->
                        authViewModel.onLoginSuccess(newRole)
                        navController.popBackStack() // Cierra el login y vuelve al mapa
                    }
                )
            }

            // ... configura las transiciones para tus otras rutas

        }
    }



        // Aquí definiremos las otras pantallas luego...
        /*
        composable(Screen.Alerts.route) {
            AlertsScreen(navController = navController)
        }
        composable(Screen.Shelters.route) {
            SheltersScreen(navController = navController)
        }
        */

}
