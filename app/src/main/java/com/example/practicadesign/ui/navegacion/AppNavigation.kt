// en /ui/navigation/AppNavigation.kt
package com.example.practicadesign.ui.navegacion

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.practicadesign.ui.auth.AuthViewModel
import com.example.practicadesign.ui.login.LoginScreen
import com.example.practicadesign.ui.mapa.MapScreen
import com.example.practicadesign.ui.mapa.componentes.BottomNav
import com.example.practicadesign.ui.refugios.SheltersScreen
import com.example.practicadesign.ui.reportes.ReportScreen

/**
 * Orquestador principal de la navegación de la aplicación.
 *
 * Este Composable se encarga de:
 * 1. Configurar el `Scaffold` principal, que incluye la barra de navegación inferior (`BottomNav`).
 * 2. Gestionar la visibilidad de la `BottomNav` según la ruta actual.
 * 3. Definir el grafo de navegación (`NavHost`) con todas las pantallas y sus transiciones.
 * 4. Proveer y observar el `AuthViewModel` para manejar el estado de autenticación y roles.
 */
@Composable
fun AppNavigation() {
    // --- DEPENDENCIAS Y ESTADO ---
    // El NavController es el cerebro de la navegación. `remember` lo mantiene a través de recomposiciones.
    val navController = rememberNavController()
    // ViewModel para compartir el estado de autenticación a través de las pantallas.
    val authViewModel: AuthViewModel = viewModel()

    // Observa el rol del usuario desde el ViewModel.
    // Durante el desarrollo, puedes cambiarlo por un valor fijo como: `val userRole: String? = "admin"`
    // val userRole by authViewModel.userRole.collectAsState()

    // MODO DE DESARROLLO (manual): Descomenta la siguiente línea para forzar un rol.
    val userRole: String? = "admin" // Puedes cambiar "admin" por "user" o null para probar.

    // --- LÓGICA DE UI ---
    // Obtiene la ruta actual para decidir si mostrar o no la barra de navegación.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomBarVisible = currentRoute != Screen.Login.route

    // --- ESTRUCTURA DE LA PANTALLA ---
    Scaffold(
        bottomBar = {
            // La BottomNav solo se muestra si la ruta actual no es la de Login.
            if (isBottomBarVisible) {
                BottomNav(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    navController = navController,
                    userRole = userRole // Pasamos el rol dinámico
                )
            }
        }
    ) { innerPadding ->
        // El NavHost gestiona qué pantalla se muestra.
        // El `innerPadding` del Scaffold evita que el contenido se dibuje debajo de la BottomNav.
        AppNavHost(
            navController = navController,
            authViewModel = authViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Define el grafo de navegación de la aplicación y las transiciones entre pantallas.
 *
 * Extraer el NavHost a su propio Composable mejora la legibilidad de `AppNavigation`.
 *
 * @param navController El controlador que gestiona la navegación.
 * @param authViewModel El ViewModel para la lógica de autenticación.
 * @param modifier El modificador que se aplicará al NavHost, incluyendo el padding del Scaffold.
 */
@Composable
private fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    // Especificaciones de animación para reutilizar
    val defaultFadeSpec = tween<Float>(300)

    NavHost(
        navController = navController,
        startDestination = Screen.Mapa.route, // La app siempre inicia en el mapa
        modifier = modifier
    ) {
        // --- PANTALLAS PRINCIPALES (Transición Fade) ---
        composable(
            route = Screen.Mapa.route,
            enterTransition = { fadeIn(animationSpec = defaultFadeSpec) },
            exitTransition = { fadeOut(animationSpec = defaultFadeSpec) }
        ) {
            MapScreen(navController = navController)
        }

        composable(
            route = Screen.Shelters.route,
            // La animación de entrada debe usar 'defaultFadeSpec', igual que las demás.
            enterTransition = { fadeIn(animationSpec = defaultFadeSpec) },
            exitTransition = { fadeOut(animationSpec = defaultFadeSpec) }
        ) {
            SheltersScreen()
        }

        composable(
            route = Screen.Report.route,
            enterTransition = { fadeIn(animationSpec = defaultFadeSpec) },
            exitTransition = { fadeOut(animationSpec = defaultFadeSpec) }
        ) {
            ReportScreen(onClose = { navController.popBackStack() })
        }

        // --- PANTALLAS SECUNDARIAS (Transición Slide) ---
        composable(
            route = Screen.Login.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            LoginScreen(
                onLoginSuccess = { newRole ->
                    authViewModel.onLoginSuccess(newRole)
                    // Vuelve a la pantalla anterior (Mapa) después de un login exitoso.
                    navController.popBackStack()
                }
            )
        }
    }
}
