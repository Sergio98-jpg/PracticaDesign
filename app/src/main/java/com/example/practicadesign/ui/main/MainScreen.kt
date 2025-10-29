package com.example.practicadesign.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practicadesign.ui.navegacion.Screen
import com.example.practicadesign.ui.mapa.MapScreen
import com.example.practicadesign.ui.refugios.SheltersScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(userRole: String) {
    // Este es un SEGUNDO NavController, exclusivo para las pantallas del BottomNavBar.
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // Aquí iría tu Composable de BottomNavBar.
            // Le pasas el navController para que los clics en los items naveguen.
            // Ejemplo: AppBottomBar(navController = bottomNavController, userRole = userRole)
        }
    ) { innerPadding ->
        // Este es el NavHost para las pantallas INTERNAS
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Mapa.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Mapa.route) { MapScreen(navController = bottomNavController) }
            composable(Screen.Shelters.route) { SheltersScreen() }
            // composable(Screen.Alerts.route) { ... }
        }
    }
}

