package com.example.practicadesign.ui.navegacion


// Usamos un 'sealed class' porque es más robusto y te permite pasar argumentos de forma estructurada.
sealed class Screen(val route: String) {
    // Para pantallas que no reciben argumentos
    object Login : Screen("login_screen")
    object Mapa : Screen("mapa_screen")
    object Shelters : Screen("shelters_screen")
    object Report : Screen("report_screen")
    // Aquí añadirías Alertas, Noticias, etc.

}