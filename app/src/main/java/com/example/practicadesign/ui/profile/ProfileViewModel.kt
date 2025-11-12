package com.example.practicadesign.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicadesign.data.ProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Perfil ([ProfileScreen]).
 *
 * Se encarga de la lógica de negocio:
 * 1. Obtener el perfil del usuario desde el [ProfileRepository].
 * 2. Manejar el estado de carga y los posibles errores.
 * 3. Procesar los eventos del usuario, como editar perfil o cerrar sesión.
 * 4. Exponer un único [StateFlow] de [ProfileUiState] que la UI puede observar.
 */
class ProfileViewModel : ViewModel() {

    // Repositorio para obtener los datos. En un futuro se inyectaría con Hilt/Koin.
    private val repository = ProfileRepository()

    // Flujo de estado mutable y privado. Solo el ViewModel puede modificarlo.
    private val _uiState = MutableStateFlow(ProfileUiState())

    // Flujo de estado inmutable y público. La UI solo puede leerlo.
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Al inicializarse el ViewModel, se lanzan las operaciones de carga de datos.
        loadProfile()
    }

    /**
     * Obtiene el perfil del usuario del repositorio y actualiza el UiState.
     * 
     * Maneja errores de forma robusta para evitar crashes si falla la conexión al backend.
     * Si la carga falla, se usa datos simulados como fallback.
     */
    private fun loadProfile() {
        Log.d("ProfileViewModel", "Iniciando la carga del perfil...")
        viewModelScope.launch {
            try {
                // Tiempo mínimo de carga para evitar parpadeos en la UI (mejora UX)
                val startTime = System.currentTimeMillis()
                val MIN_LOADING_TIME_MS = 500L
                
                // Intenta cargar desde el backend, si falla usa datos simulados
                // Por ahora, siempre usa datos simulados hasta que el backend esté listo
                // Para activar el backend, cambia getMockProfile() por getProfile()
                val profile = repository.getMockProfile().first()
                
                // TODO: Cuando el backend esté listo, descomentar y usar:
                // val profile = try {
                //     repository.getProfile()
                //         .catch { exception ->
                //             Log.w("ProfileViewModel", "Error al cargar perfil desde backend, usando datos simulados", exception)
                //             // Si falla el backend, emite datos simulados como fallback
                //             emit(repository.getMockProfile().first())
                //         }
                //         .first()
                // } catch (e: Exception) {
                //     Log.w("ProfileViewModel", "Excepción al cargar perfil, usando datos simulados", e)
                //     // Si hay una excepción, usa datos simulados
                //     repository.getMockProfile().first()
                // }
                
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
                        userInfo = UserInfo(
                            name = profile.name,
                            location = profile.location,
                            avatarUrl = profile.avatarUrl
                        ),
                        stats = UserStats(
                            reportsSent = profile.reportsSent,
                            alertsReceived = profile.alertsReceived
                        ),
                        errorMessage = null // Limpia cualquier error anterior
                    )
                }
            } catch (e: Exception) {
                // Manejo de errores adicional por si algo inesperado ocurre
                Log.e("ProfileViewModel", "Error inesperado al cargar el perfil", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar el perfil. Intente más tarde."
                    ) 
                }
            }
        }
    }

    /**
     * Maneja el evento de reintento desde la UI.
     * Resetea el estado de error y vuelve a lanzar la carga de datos.
     */
    fun retryLoadProfile() {
        // Primero, reseteamos el estado para que vuelva a mostrar el loader
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null // Limpiamos el mensaje de error anterior
            )
        }
        // Luego, llamamos a la función de carga original
        loadProfile()
    }

    /**
     * Maneja el evento de edición del perfil.
     * 
     * TODO: Implementar navegación a pantalla de edición de perfil cuando esté disponible.
     */
    fun onEditProfileClick() {
        // TODO: Implementar navegación a pantalla de edición de perfil
        Log.d("ProfileViewModel", "Editar perfil clickeado")
    }
}

