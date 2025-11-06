package com.example.practicadesign.ui.refugios

import androidx.activity.result.launch
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.example.practicadesign.data.MapRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.practicadesign.ui.refugios.componentes.*


/* -------------------------------------------------
   VISTA PREVIA (PREVIEW)
------------------------------------------------- */

/*@Preview(showBackground = true)
@Composable
fun PreviewSheltersScreen() {
    // 1. Crea una instancia de un ViewModel falso.
    val fakeViewModel = object : SheltersViewModel() {
        init {
            // 2. Sobreescribe el estado inicial con datos de prueba.
            //    Establece isLoading = false para que la preview muestre la lista.
            _uiState.value = SheltersUiState(
                isLoading = false,
                shelters = listOf(
                    Shelter("1", LatLng(19.428, -99.155), "Refugio Benito Juárez", true, "Av. Insurgentes Sur 300", 150, 95),
                    Shelter("2", LatLng(19.419, -99.162), "Centro Comunitario Roma", true, "Roma Norte, CDMX", 200, 42),
                    Shelter("3", LatLng(19.44, -99.13), "Escuela Primaria (Llena)", true, "Av. Reforma 10", 120, 120),
                    Shelter("4", LatLng(19.41, -99.165), "Auditorio Municipal (Cerrado)", false, "Centro Histórico", 300, 0)
                ),
                selectedFilter = ShelterFilter.ALL
            )
        }
        // No necesitamos sobreescribir onFilterChange porque la preview no interactúa.
    }

    // 3. Pasa el ViewModel falso a tu pantalla.
    SheltersScreen(sheltersViewModel = fakeViewModel)
}*/


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SheltersScreen(
    sheltersViewModel: SheltersViewModel = viewModel()
) {
    val uiState by sheltersViewModel.uiState.collectAsState()
    var selectedShelterId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC))) {

        // --- Header ---
        Text(
            text = "Refugios",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        // --- Quick Filters ---
        QuickFiltersRow(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = { sheltersViewModel.onFilterChange(it) }
        )

        // ✅ Manejamos el estado de carga
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // --- Lista de refugios ---
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ✅ Usamos la lista filtrada del UiState
                items(uiState.filteredShelters, key = { it.id }) { shelter ->
                    val expanded = selectedShelterId == shelter.id

                    Column {

                        // --- Item compacto ---
                        ShelterItem(
                            shelter = shelter,
                            expanded = expanded,
                            onClick = {
                                selectedShelterId = if (expanded) null else shelter.id
                            }
                        )

                        // --- Detalle expandido ---
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp)) // 👈 Aplica el clip aquí
                                    .background(Color.White)         // 👈 Asegura fondo uniforme
                            ) {
                                ShelterDetailCard(shelter = shelter) {
                                    selectedShelterId = null
                                }
                            }
                                Spacer(Modifier.height(8.dp))
                        }

                    }
                }
            }
        }
    }
}








/*
data class SheltersUiState(
    val isLoading: Boolean = true,
    val shelters: List<Shelter> = emptyList(),
    val selectedFilter: ShelterFilter = ShelterFilter.ALL
) {
    val filteredShelters: List<Shelter>
        get() = when (selectedFilter) {
            ShelterFilter.ALL -> shelters
            ShelterFilter.OPEN -> shelters.filter { it.isOpen }
            ShelterFilter.NEAREST -> shelters // La lógica de ordenamiento real es compleja para preview
            ShelterFilter.AVAILABLE -> shelters.filter { it.currentOccupancy < it.capacity }
        }
}

// ✅ Hereda de androidx.lifecycle.ViewModel
open class SheltersViewModel : ViewModel() {

    // ✅ Usa MutableStateFlow para que la UI pueda observar los cambios
    val _uiState = MutableStateFlow(SheltersUiState())
    val uiState = _uiState.asStateFlow()

    private val mapRepository = MapRepository()

    init {
        loadShelters()
    }

    private fun loadShelters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val mapData = mapRepository.getMapData()
                _uiState.update {
                    it.copy(isLoading = false, shelters = mapData.shelters)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onFilterChange(filter: ShelterFilter) {
        _uiState.update { currentState ->
            currentState.copy(selectedFilter = filter)
        }
    }
}*/
