package com.example.practicadesign.ui.navegacion


/**
 * Definición de las rutas de navegación de la aplicación.
 * 
 * Usamos un 'sealed class' porque es más robusto y permite pasar argumentos de forma estructurada.
 */
sealed class Screen(val route: String) {
    // Para pantallas que no reciben argumentos
    object Login : Screen("login_screen")
    object Mapa : Screen("mapa_screen") {
        // Ruta con parámetro opcional para navegar a un refugio específico
        fun withShelterId(shelterId: String) = "mapa_screen?shelterId=$shelterId"
    }
    object Shelters : Screen("shelters_screen")
    object Report : Screen("report_screen")
    object ReportsHome : Screen("reports_home_screen")
    object ReportsHistory : Screen("reports_history_screen")
    object Profile : Screen("profile_screen")
    // Aquí añadirías Alertas, Noticias, etc.
    
    companion object {
        // Constantes para los argumentos de navegación
        const val SHELTER_ID_ARG = "shelterId"
    }
}