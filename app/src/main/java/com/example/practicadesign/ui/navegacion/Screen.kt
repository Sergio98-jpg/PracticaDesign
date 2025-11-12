package com.example.practicadesign.ui.navegacion


/**
 * Definición de las rutas de navegación de la aplicación.
 * 
 * Usamos un 'sealed class' porque es más robusto y permite pasar argumentos de forma estructurada.
 */
sealed class Screen(val route: String) {
    // Para pantallas que no reciben argumentos
    object Login : Screen("login_screen")
    object Mapa : Screen("mapa_screen")
    object Shelters : Screen("shelters_screen")
    object Report : Screen("report_screen")
    object Profile : Screen("profile_screen")
    // Aquí añadirías Alertas, Noticias, etc.
}