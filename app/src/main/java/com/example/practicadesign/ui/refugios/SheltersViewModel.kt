package com.example.practicadesign.ui.refugios

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicadesign.data.MapRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Refugios ([SheltersScreen]).
 *
 * Se encarga de la lógica de negocio:
 * 1. Obtener la lista de refugios desde el [MapRepository] (con cache local).
 * 2. Manejar el estado de carga y los posibles errores.
 * 3. Procesar los eventos del usuario, como cambiar filtros o expandir/colapsar un ítem.
 * 4. Exponer un único [StateFlow] de [SheltersUiState] que la UI puede observar.
 */
open class SheltersViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio para obtener los datos. En un futuro se inyectaría con Hilt/Koin.
    // Pasa el contexto de la aplicación para habilitar el cache local
    private val repository = MapRepository(getApplication())

    // Flujo de estado mutable y privado. Solo el ViewModel puede modificarlo.
    protected val _uiState = MutableStateFlow(SheltersUiState())

    // Flujo de estado inmutable y público. La UI solo puede leerlo.
    val uiState: StateFlow<SheltersUiState> = _uiState.asStateFlow()

    init {
        // Al inicializarse el ViewModel, se lanzan las operaciones de carga de datos.
        loadShelters()
    }

    /**
     * Obtiene la lista de refugios del repositorio y actualiza el UiState.
     * 
     * Maneja errores de forma robusta para evitar crashes si falla la conexión al backend.
     * Si la carga falla, se actualiza el estado con un mensaje de error.
     */
    private fun loadShelters() {
        Log.d("SheltersViewModel", "Iniciando la carga de refugios...")
        viewModelScope.launch {
            try {
                // Tiempo mínimo de carga para evitar parpadeos en la UI (mejora UX)
                val startTime = System.currentTimeMillis()
                val MIN_LOADING_TIME_MS = 500L
                
                repository.getShelters()
                    .catch { exception ->
                        // Cuando el Flow lanza una excepción, este bloque se activa.
                        Log.e("SheltersViewModel", "Error al cargar refugios", exception)
                        
                        // Asegura un tiempo mínimo de carga para mejorar la experiencia de usuario
                        val elapsedTime = System.currentTimeMillis() - startTime
                        val remainingTime = MIN_LOADING_TIME_MS - elapsedTime
                        if (remainingTime > 0) {
                            delay(remainingTime)
                        }
                        
                        // Actualizamos el estado para indicar que la carga falló,
                        // pero la app no crashea.
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "No se pudieron cargar los refugios. Verifique su conexión."
                            ) 
                        }
                    }
                    .collect { shelters ->
                        // Asegura un tiempo mínimo de carga para mejorar la experiencia de usuario
                        val elapsedTime = System.currentTimeMillis() - startTime
                        val remainingTime = MIN_LOADING_TIME_MS - elapsedTime
                        if (remainingTime > 0) {
                            delay(remainingTime)
                        }
                        
                        // Cuando se reciben los datos, se actualiza el estado.
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                shelters = shelters,
                                errorMessage = null // Limpia cualquier error anterior
                            )
                        }
                    }
            } catch (e: Exception) {
                // Manejo de errores adicional por si algo inesperado ocurre
                Log.e("SheltersViewModel", "Error inesperado al cargar refugios", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar los refugios. Intente más tarde."
                    ) 
                }
            }
        }
    }

    /**
     * Maneja el evento de cambio de filtro desde la UI.
     * Crea un nuevo estado con el filtro seleccionado.
     *
     * @param newFilter El nuevo filtro seleccionado por el usuario.
     */
    fun onFilterChange(newFilter: ShelterFilter) {
        _uiState.update { it.copy(selectedFilter = newFilter) }
    }

    /**
     * Maneja el evento de tocar un ítem de refugio en la lista.
     * Si el refugio ya está expandido, lo colapsa (expandedShelterId = null).
     * Si está colapsado, lo expande.
     *
     * @param shelterId El ID del refugio que fue tocado.
     */
    fun onShelterToggled(shelterId: String) {
        _uiState.update { currentState ->
            val newExpandedId = if (currentState.expandedShelterId == shelterId) {
                null // Si ya estaba expandido, lo cerramos
            } else {
                shelterId // Si no, lo expandimos
            }
            currentState.copy(expandedShelterId = newExpandedId)
        }
    }

    /**
     * Maneja el evento de reintento desde la UI.
     * Resetea el estado de error y vuelve a lanzar la carga de datos.
     */
    fun retryLoadShelters() {
        // Primero, reseteamos el estado para que vuelva a mostrar el loader
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null // Limpiamos el mensaje de error anterior
            )
        }
        // Luego, llamamos a la función de carga original
        loadShelters()
    }
}
