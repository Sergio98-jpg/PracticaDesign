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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
@Composable
fun SheltersScreen(
    sheltersViewModel: SheltersViewModel = viewModel()
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
            if (uiState.isLoading) {
                // Muestra un indicador de carga centrado si los datos se están obteniendo.
                //CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false // El usuario no debería poder hacer scroll en el esqueleto
                ) {
                    items(10) { // Muestra 10 items fantasma
                        ShelterItemSkeleton()
                    }
                }
            } else if (uiState.errorMessage != null) { // <-- NUEVA CONDICIÓN
                // Muestra un componente de error
                val errorMessage = uiState.errorMessage
                ErrorStateComponent(
                    message = errorMessage,
                    onRetry = { sheltersViewModel.retryLoadShelters() } // <-- ¡Avanzado pero ideal!
                )
            } else {
                // Muestra la lista de refugios una vez que los datos están listos.
                SheltersList(
                    uiState = uiState,
                    onShelterClick = { shelterId -> sheltersViewModel.onShelterToggled(shelterId) }
                )
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
                    onClose = { onShelterClick(shelter.id) } // Tocar el botón de cerrar también invoca el toggle
                )
            }
        }
    }
}
