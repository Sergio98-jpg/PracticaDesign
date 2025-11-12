package com.example.practicadesign.ui.reportes

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicadesign.data.Report
import com.example.practicadesign.data.ReportRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Reportes ([ReportScreen]).
 *
 * Se encarga de la lógica de negocio:
 * 1. Gestionar el estado del formulario de reportes (pasos, validaciones).
 * 2. Manejar la navegación entre pasos del formulario.
 * 3. Enviar el reporte al backend a través del [ReportRepository].
 * 4. Manejar el estado de carga y los posibles errores.
 * 5. Exponer un único [StateFlow] de [ReportUiState] que la UI puede observar.
 */
class ReportViewModel : ViewModel() {

    // Repositorio para enviar los datos. En un futuro se inyectaría con Hilt/Koin.
    private val repository = ReportRepository()

    // Flujo de estado mutable y privado. Solo el ViewModel puede modificarlo.
    private val _uiState = MutableStateFlow(ReportUiState())

    // Flujo de estado inmutable y público. La UI solo puede leerlo.
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    /**
     * Navega al siguiente paso del formulario.
     * 
     * Valida que el paso actual sea válido antes de avanzar.
     */
    fun onNextStep() {
        val currentState = _uiState.value
        
        // Valida que el paso actual sea válido antes de avanzar
        if (!currentState.isCurrentStepValid()) {
            return
        }
        
        if (currentState.currentStep < ReportUiState.TOTAL_STEPS) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        } else {
            // Si estamos en el último paso, enviamos el reporte
            submitReport()
        }
    }

    /**
     * Navega al paso anterior del formulario.
     */
    fun onPreviousStep() {
        _uiState.update { currentState ->
            if (currentState.currentStep > 1) {
                currentState.copy(currentStep = currentState.currentStep - 1)
            } else {
                currentState
            }
        }
    }

    /**
     * Actualiza el tipo de reporte seleccionado.
     * 
     * @param type Tipo de reporte seleccionado
     */
    fun onTypeSelected(type: String) {
        _uiState.update { it.copy(selectedType = type) }
    }

    /**
     * Actualiza el tipo de ubicación seleccionado.
     * 
     * @param locationType Tipo de ubicación seleccionado ("current" o "map")
     * @param currentLocation Ubicación actual del usuario (opcional, se usa cuando locationType es "current")
     */
    fun onLocationSelected(locationType: String, currentLocation: LatLng? = null) {
        _uiState.update { 
            it.copy(
                selectedLocation = locationType,
                // Si se selecciona "current" y se proporciona una ubicación, la guardamos
                locationCoordinates = if (locationType == "current" && currentLocation != null) {
                    currentLocation
                } else if (locationType == "map") {
                    // Si se selecciona "map", las coordenadas se establecerán cuando el usuario seleccione en el mapa
                    it.locationCoordinates
                } else {
                    it.locationCoordinates
                }
            ) 
        }
    }

    /**
     * Actualiza las coordenadas de la ubicación del reporte.
     * 
     * @param coordinates Coordenadas de la ubicación
     */
    fun onLocationCoordinatesUpdated(coordinates: LatLng) {
        _uiState.update { it.copy(locationCoordinates = coordinates) }
    }

    /**
     * Actualiza el título del reporte.
     * 
     * @param title Nuevo título del reporte
     */
    fun onTitleChange(title: String) {
        if (title.length <= ReportUiState.MAX_TITLE_LENGTH) {
            _uiState.update { it.copy(title = title) }
        }
    }

    /**
     * Actualiza la descripción del reporte.
     * 
     * @param description Nueva descripción del reporte
     */
    fun onDescriptionChange(description: String) {
        if (description.length <= ReportUiState.MAX_DESCRIPTION_LENGTH) {
            _uiState.update { it.copy(description = description) }
        }
    }

    /**
     * Actualiza el nivel de urgencia del reporte.
     * 
     * @param urgency Nuevo nivel de urgencia (high, medium, low)
     */
    fun onUrgencyChange(urgency: String) {
        _uiState.update { it.copy(urgency = urgency) }
    }

    /**
     * Actualiza una foto en la lista de fotos.
     * 
     * @param index Índice de la foto a actualizar (0-2)
     * @param uri URI de la nueva foto
     */
    fun onPhotoAdded(index: Int, uri: Uri?) {
        if (index in 0 until ReportUiState.MAX_PHOTOS) {
            _uiState.update { currentState ->
                val updatedPhotos = currentState.photos.toMutableList().apply {
                    this[index] = uri
                }
                currentState.copy(photos = updatedPhotos)
            }
        }
    }

    /**
     * Oculta el overlay de éxito después de enviar el reporte.
     */
    fun onSuccessOverlayDismissed() {
        _uiState.update { it.copy(successVisible = false) }
    }

    /**
     * Limpia el mensaje de error.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Envía el reporte al backend.
     * 
     * Valida que el formulario sea válido, crea el objeto Report,
     * lo envía al repositorio y maneja la respuesta.
     * 
     * Maneja errores de forma robusta para evitar crashes si falla la conexión al backend.
     */
    private fun submitReport() {
        val currentState = _uiState.value
        
        // Valida que el formulario sea válido
        if (!currentState.isFormValid()) {
            _uiState.update { 
                it.copy(errorMessage = "Por favor, complete todos los campos requeridos.")
            }
            return
        }

        // Valida que haya coordenadas de ubicación
        val coordinates = currentState.locationCoordinates
        if (coordinates == null) {
            _uiState.update { 
                it.copy(errorMessage = "Por favor, seleccione una ubicación válida.")
            }
            return
        }

        // Valida que el tipo de reporte esté seleccionado
        val reportType = currentState.selectedType
        if (reportType == null) {
            _uiState.update { 
                it.copy(errorMessage = "Por favor, seleccione un tipo de reporte.")
            }
            return
        }

        // Valida que el tipo de ubicación esté seleccionado
        val locationType = currentState.selectedLocation
        if (locationType == null) {
            _uiState.update { 
                it.copy(errorMessage = "Por favor, seleccione un tipo de ubicación.")
            }
            return
        }

        Log.d("ReportViewModel", "Iniciando el envío del reporte...")
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Crea el objeto Report con los datos del formulario
                // Por ahora, las fotos se envían como URIs vacías (el backend las procesará después)
                val report = Report(
                    type = reportType,
                    title = currentState.title,
                    description = currentState.description,
                    urgency = currentState.urgency,
                    locationCoordinates = coordinates,
                    locationType = locationType,
                    photoUris = emptyList() // TODO: Subir fotos al servidor y obtener URLs
                )

                // Intenta enviar al backend, si falla usa datos simulados
                // Por ahora, siempre usa datos simulados hasta que el backend esté listo
                // Para activar el backend, cambia submitMockReport() por submitReport()
                val response = repository.submitMockReport(report).first()
                
                // TODO: Cuando el backend esté listo, descomentar y usar:
                // val response = try {
                //     repository.submitReport(report)
                //         .catch { exception ->
                //             Log.w("ReportViewModel", "Error al enviar reporte al backend, usando simulación", exception)
                //             // Si falla el backend, emite una respuesta simulada como fallback
                //             emit(repository.submitMockReport(report).first())
                //         }
                //         .first()
                // } catch (e: Exception) {
                //     Log.w("ReportViewModel", "Excepción al enviar reporte, usando simulación", e)
                //     // Si hay una excepción, usa datos simulados
                //     repository.submitMockReport(report).first()
                // }

                // Si la respuesta es exitosa, muestra el overlay de éxito
                if (response.success) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successVisible = true,
                            errorMessage = null
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Error al enviar el reporte."
                        ) 
                    }
                }
            } catch (e: Exception) {
                // Manejo de errores adicional por si algo inesperado ocurre
                Log.e("ReportViewModel", "Error inesperado al enviar el reporte", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al enviar el reporte. Verifique su conexión e intente nuevamente."
                    ) 
                }
            }
        }
    }
}
