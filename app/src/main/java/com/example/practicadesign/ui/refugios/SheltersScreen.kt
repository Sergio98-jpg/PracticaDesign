package com.example.practicadesign.ui.refugios

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practicadesign.ui.navegacion.Screen
import com.example.practicadesign.ui.refugios.componentes.ErrorStateComponent
import com.example.practicadesign.ui.refugios.componentes.QuickFiltersRow
import com.example.practicadesign.ui.refugios.componentes.ShelterDetailCard
import com.example.practicadesign.ui.refugios.componentes.ShelterItem
import com.example.practicadesign.ui.refugios.componentes.ShelterItemSkeleton

/**
 * Pantalla principal que muestra una lista de refugios disponibles.
 *
 * Esta pantalla se encarga de:
 * 1. Observar el estado de la UI desde [SheltersViewModel].
 * 2. Mostrar un indicador de carga mientras se obtienen los datos.
 * 3. Presentar una barra de filtros rápidos.
 * 4. Renderizar una lista de refugios (`LazyColumn`) que se puede expandir para ver detalles.
 *
 * Es una vista "tonta" que delega toda la lógica de negocio y estado al ViewModel.
 *
 * @param sheltersViewModel El ViewModel que proporciona el estado y maneja la lógica.
 */

@Preview(showBackground = true, name = "Filtros (Disponibles seleccionados)")
@Composable
private fun SheltersScreenPreview() {
    SheltersScreen(
    )
}

@Composable
fun SheltersScreen(
    navController: NavController? = null, // NavController opcional para navegación
    sheltersViewModel: SheltersViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    // --- ESTADO ---
    // Observa el UiState del ViewModel. Cualquier cambio aquí causará una recomposición.
    val uiState by sheltersViewModel.uiState.collectAsState()

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Color de fondo base para la pantalla
    ) {
        // --- Encabezado ---
        Text(
            text = "Refugios",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        // --- Filtros Rápidos ---
        QuickFiltersRow(
            selectedFilter = uiState.selectedFilter,
            // Notifica al ViewModel cuando el usuario selecciona un nuevo filtro.
            onFilterSelected = { filter -> sheltersViewModel.onFilterChange(filter) }
        )

        // --- Contenido Principal (Carga o Lista) ---
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    // Muestra un esqueleto de carga mientras se obtienen los datos.
                    // Esto mejora la UX al mostrar un placeholder en lugar de una pantalla vacía.
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false // El usuario no debería poder hacer scroll en el esqueleto
                    ) {
                        items(10) { // Muestra 10 items fantasma
                            ShelterItemSkeleton()
                        }
                    }
                }
                uiState.errorMessage != null -> {
                    // Muestra un componente de error con opción de reintentar.
                    ErrorStateComponent(
                        message = uiState.errorMessage,
                        onRetry = { sheltersViewModel.retryLoadShelters() }
                    )
                }
                else -> {
                    // Muestra la lista de refugios una vez que los datos están listos.
                    SheltersList(
                        uiState = uiState,
                        navController = navController,
                        onShelterClick = { shelterId -> sheltersViewModel.onShelterToggled(shelterId) }
                    )
                }
            }
        }
    }
}

/**
 * Composable que renderiza la lista de refugios.
 *
 * Extraer la `LazyColumn` a su propio Composable mejora la legibilidad y el enfoque de `SheltersScreen`.
 *
 * @param uiState El estado actual de la UI que contiene la lista de refugios y cuál está expandido.
 * @param onShelterClick Callback que se invoca cuando un usuario toca un ítem de refugio.
 */
@Composable
private fun SheltersList(
    uiState: SheltersUiState,
    navController: NavController?,
    onShelterClick: (shelterId: String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Aumenta el espacio para la tarjeta de detalle
    ) {
        items(uiState.filteredShelters, key = { it.id }) { shelter ->
            val isExpanded = uiState.expandedShelterId == shelter.id

            // El ShelterItem es el ítem principal siempre visible.
            ShelterItem(
                shelter = shelter,
                expanded = isExpanded,
                onClick = { onShelterClick(shelter.id) }
            )

            // El ShelterDetailCard solo es visible cuando el ítem está expandido.
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
            ) {
                // El detalle se muestra directamente debajo del ítem.
                ShelterDetailCard(
                    shelter = shelter,
                    navController = navController,
                    onClose = { onShelterClick(shelter.id) } // Tocar el botón de cerrar también invoca el toggle
                )
            }
        }
    }
}
