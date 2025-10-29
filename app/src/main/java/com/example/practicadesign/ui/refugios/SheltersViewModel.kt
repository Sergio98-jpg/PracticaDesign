package com.example.practicadesign.ui.refugios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicadesign.data.MapRepository // Reutilizamos el repo del mapa!
import com.example.practicadesign.ui.mapa.componentes.Shelter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SheltersUiState(
    val isLoading: Boolean = true,
    val shelters: List<Shelter> = emptyList()
)

class SheltersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SheltersUiState())
    val uiState = _uiState.asStateFlow()

    // Reutilizamos MapRepository para no duplicar la lógica de carga de datos.
    private val mapRepository = MapRepository()

    init {
        loadShelters()
    }

    private fun loadShelters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Obtenemos todos los datos, pero solo usamos los refugios
                val mapData = mapRepository.getMapData()
                _uiState.update { it.copy(isLoading = false, shelters = mapData.shelters) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}