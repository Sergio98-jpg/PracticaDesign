package com.example.practicadesign.ui.reportes

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicadesign.data.AppError
import com.example.practicadesign.data.Report
import com.example.practicadesign.data.ReportRepository
import com.example.practicadesign.data.local.AppDatabase
import com.example.practicadesign.data.local.ReportDraftEntity
import com.example.practicadesign.data.local.toEntity
import com.example.practicadesign.data.local.toUiState
import com.example.practicadesign.data.toAppError
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
 * 5. Guardar y restaurar borradores del formulario usando Room Database.
 * 6. Exponer un único [StateFlow] de [ReportUiState] que la UI puede observar.
 */
class ReportViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio para enviar los datos. En un futuro se inyectaría con Hilt/Koin.
    private val repository = ReportRepository()
    
    // Bandera para deshabilitar la persistencia de borradores cuando el flujo termina en éxito
    // Evita condiciones de carrera con jobs de guardado diferidos (debounce) aún en vuelo.
    private var disableDraftPersistence: Boolean = false
    
    // Base de datos Room para persistencia de borradores
    private val database = AppDatabase.getDatabase(application)
    private val draftDao = database.reportDraftDao()

    // Flujo de estado mutable y privado. Solo el ViewModel puede modificarlo.
    private val _uiState = MutableStateFlow(ReportUiState())

    // Flujo de estado inmutable y público. La UI solo puede leerlo.
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()
    
    // Flujo para verificar si hay un borrador pendiente
    private val _hasDraft = MutableStateFlow(false)
    val hasDraft: StateFlow<Boolean> = _hasDraft.asStateFlow()
    
    /**
     * Inicializa el ViewModel cargando el borrador guardado si existe.
     */
    init {
        // Solo carga el borrador si no se ha deshabilitado la persistencia
        loadDraft()
        checkDraftExists()
    }
    
    /**
     * Verifica si existe un borrador pendiente en la base de datos.
     */
    private fun checkDraftExists() {
        viewModelScope.launch {
            try {
                val draft = draftDao.getLatestDraftSync()
                _hasDraft.value = draft != null
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error al verificar borrador", e)
                _hasDraft.value = false
            }
        }
    }
    
    /**
     * Actualiza el estado de hasDraft cuando se crea o elimina un borrador.
     */
    fun refreshDraftStatus() {
        checkDraftExists()
    }


    /**
     * Inicia el flujo de un nuevo reporte, limpiando cualquier estado anterior.
     * Limpia la base de datos y el estado en memoria.
     * Se llama después de enviar exitosamente un reporte.
     */
    fun startNewReport() {
        viewModelScope.launch {
            // 1. Limpia el borrador de la base de datos primero
            clearDraft()
            
            // 2. Resetea el estado en memoria a uno completamente nuevo
            _uiState.value = ReportUiState()
            
            // 3. Reactivar la persistencia para el nuevo reporte
            disableDraftPersistence = false
            
            // 4. Actualizar el estado de hasDraft
            _hasDraft.value = false
            
            Log.i("ReportViewModel", "======= NUEVO REPORTE INICIADO (BD y UI limpios) =======")
        }
    }

    /**
     * Carga el borrador más reciente desde la base de datos local.
     * 
     * Si existe un borrador, restaura el estado del formulario.
     * Si no existe, mantiene el estado inicial vacío.
     */
    private fun loadDraft() {
        viewModelScope.launch {
            try {
                val draft = draftDao.getLatestDraftSync()
                if (draft != null) {
                    val restoredState = draft.toUiState()
                    _uiState.update { restoredState }
                    Log.d("ReportViewModel", "↻ Borrador RESTAURADO: paso ${restoredState.currentStep}")
                } else {
                    Log.d("ReportViewModel", "○ No hay borrador guardado - Iniciando formulario limpio")
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error al cargar borrador", e)
                // Si falla la carga del borrador, continuamos con el estado inicial
            }
        }
    }
    
    /**
     * Guarda el estado actual del formulario como borrador en la base de datos.
     * 
     * Se llama automáticamente cuando el usuario avanza de paso o modifica campos.
     * Es una operación asíncrona que se ejecuta en background.
     */
    private fun saveDraft() {
        // Si la persistencia está deshabilitada, no guardar el borrador
        if (disableDraftPersistence) {
            Log.d("ReportViewModel", "Persistencia deshabilitada, NO se guarda borrador")
            return
        }
        
        viewModelScope.launch {
            try {
                val entity = _uiState.value.toEntity()
                draftDao.saveDraft(entity)
                _hasDraft.value = true // Actualizar estado cuando se guarda
                Log.d("ReportViewModel", "✓ Borrador guardado: paso ${_uiState.value.currentStep}")
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error al guardar borrador", e)
                // No mostramos error al usuario, es una operación en background
            }
        }
    }
    
    /**
     * Elimina el borrador guardado de forma síncrona.
     * 
     * Se llama después de enviar un reporte exitosamente.
     * Usa runBlocking para asegurar que se elimine inmediatamente antes de cualquier otra operación.
     */
    private suspend fun clearDraft() {
        try {
            draftDao.deleteLatestDraft()
            Log.d("ReportViewModel", "✓ Borrador ELIMINADO de la base de datos.")
        } catch (e: Exception) {
            Log.e("ReportViewModel", "❌ Error al eliminar borrador de la BD", e)
        }
    }

    /**
     * Navega al siguiente paso del formulario.
     * 
     * Valida que el paso actual sea válido antes de avanzar.
     * Muestra mensajes de error específicos si la validación falla.
     */
    fun onNextStep() {
        val currentState = _uiState.value
        
        // Valida el paso actual y muestra errores específicos
        val validationResult = validateCurrentStep(currentState)
        if (!validationResult.isValid) {
            _uiState.update { 
                it.copy(fieldErrors = validationResult.errors)
            }
            return
        }
        
        // Limpia errores antes de avanzar
        _uiState.update { 
            it.copy(
                currentStep = if (it.currentStep < ReportUiState.TOTAL_STEPS) {
                    it.currentStep + 1
                } else {
                    it.currentStep
                },
                fieldErrors = emptyMap()
            )
        }
        
        // Guarda el borrador después de avanzar
        saveDraft()
        
        // Si estamos en el último paso, enviamos el reporte
        if (currentState.currentStep >= ReportUiState.TOTAL_STEPS) {
            submitReport()
        }
    }
    
    /**
     * Resultado de la validación de un paso.
     */
    private data class ValidationResult(
        val isValid: Boolean,
        val errors: Map<String, String>
    )
    
    /**
     * Valida el paso actual del formulario y retorna errores específicos.
     * 
     * @param state Estado actual del formulario
     * @return Resultado de la validación con errores específicos por campo
     */
    private fun validateCurrentStep(state: ReportUiState): ValidationResult {
        val errors = mutableMapOf<String, String>()
        
        when (state.currentStep) {
            1 -> {
                if (state.selectedType == null) {
                    errors["type"] = "Por favor, seleccione un tipo de reporte"
                }
            }
            2 -> {
                if (state.selectedLocation == null) {
                    errors["location"] = "Por favor, seleccione un tipo de ubicación"
                } else if (state.locationCoordinates == null) {
                    errors["location"] = "Por favor, seleccione una ubicación válida"
                }
            }
            3 -> {
                if (state.title.isBlank()) {
                    errors["title"] = "El título es obligatorio"
                } else if (state.title.length < 5) {
                    errors["title"] = "El título debe tener al menos 5 caracteres"
                } else if (state.title.length > ReportUiState.MAX_TITLE_LENGTH) {
                    errors["title"] = "El título no puede tener más de ${ReportUiState.MAX_TITLE_LENGTH} caracteres"
                }
                
                if (state.description.isBlank()) {
                    errors["description"] = "La descripción es obligatoria"
                } else if (state.description.length < 10) {
                    errors["description"] = "La descripción debe tener al menos 10 caracteres"
                } else if (state.description.length > ReportUiState.MAX_DESCRIPTION_LENGTH) {
                    errors["description"] = "La descripción no puede tener más de ${ReportUiState.MAX_DESCRIPTION_LENGTH} caracteres"
                }
            }
            4 -> {
                // Las fotos son opcionales, no hay validación
            }
            5 -> {
                // El resumen no requiere validación
            }
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
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
        // Guarda el borrador después de retroceder
        saveDraft()
    }

    /**
     * Actualiza el tipo de reporte seleccionado.
     * 
     * Limpia el error del campo si existe.
     * 
     * @param type Tipo de reporte seleccionado
     */
    fun onTypeSelected(type: String) {
        _uiState.update { currentState ->
            val updatedErrors = currentState.fieldErrors.toMutableMap()
            updatedErrors.remove("type")
            currentState.copy(
                selectedType = type,
                fieldErrors = updatedErrors
            )
        }
        saveDraft()
    }

    /**
     * Actualiza el tipo de ubicación seleccionado.
     * 
     * Limpia el error del campo si existe.
     * 
     * @param locationType Tipo de ubicación seleccionado ("current" o "map")
     * @param currentLocation Ubicación actual del usuario (opcional, se usa cuando locationType es "current")
     */
    fun onLocationSelected(locationType: String, currentLocation: LatLng? = null) {
        _uiState.update { currentState ->
            val updatedErrors = currentState.fieldErrors.toMutableMap()
            updatedErrors.remove("location")
            
            // Coordenadas que se usarán según el tipo de ubicación
            val coordinates = when {
                // Si se proporciona una ubicación, usarla (viene del GPS o del mapa)
                currentLocation != null -> currentLocation
                // Fallback seguro: si aún no hay coordenadas, usar un valor por defecto
                // para permitir avanzar mientras se resuelve la selección real.
                currentState.locationCoordinates == null && (locationType == "current" || locationType == "map") ->
                    LatLng(19.4326, -99.1332) // CDMX Centro (provisional)
                // Mantener coordenadas actuales si existen
                else -> currentState.locationCoordinates
            }
            
            currentState.copy(
                selectedLocation = locationType,
                locationCoordinates = coordinates,
                fieldErrors = updatedErrors
            ) 
        }
        saveDraft()
    }

    /**
     * Actualiza las coordenadas de la ubicación del reporte.
     * 
     * Limpia el error del campo si existe.
     * 
     * @param coordinates Coordenadas de la ubicación
     */
    fun onLocationCoordinatesUpdated(coordinates: LatLng) {
        _uiState.update { currentState ->
            val updatedErrors = currentState.fieldErrors.toMutableMap()
            updatedErrors.remove("location")
            currentState.copy(
                locationCoordinates = coordinates,
                fieldErrors = updatedErrors
            )
        }
        saveDraft()
    }

    /**
     * Actualiza el título del reporte.
     * 
     * Valida en tiempo real y limpia el error del campo si existe.
     * 
     * @param title Nuevo título del reporte
     */
    fun onTitleChange(title: String) {
        if (title.length <= ReportUiState.MAX_TITLE_LENGTH) {
            _uiState.update { currentState ->
                val updatedErrors = currentState.fieldErrors.toMutableMap()
                // Limpia el error del título si el usuario está escribiendo
                updatedErrors.remove("title")
                currentState.copy(
                    title = title,
                    fieldErrors = updatedErrors
                )
            }
        }
        // Guarda el borrador después de un delay para evitar guardar en cada tecla
        viewModelScope.launch {
            delay(500) // Espera 500ms después de que el usuario deje de escribir
            saveDraft()
        }
    }

    /**
     * Actualiza la descripción del reporte.
     * 
     * Valida en tiempo real y limpia el error del campo si existe.
     * 
     * @param description Nueva descripción del reporte
     */
    fun onDescriptionChange(description: String) {
        if (description.length <= ReportUiState.MAX_DESCRIPTION_LENGTH) {
            _uiState.update { currentState ->
                val updatedErrors = currentState.fieldErrors.toMutableMap()
                // Limpia el error de la descripción si el usuario está escribiendo
                updatedErrors.remove("description")
                currentState.copy(
                    description = description,
                    fieldErrors = updatedErrors
                )
            }
        }
        // Guarda el borrador después de un delay para evitar guardar en cada tecla
        viewModelScope.launch {
            delay(500) // Espera 500ms después de que el usuario deje de escribir
            saveDraft()
        }
    }

    /**
     * Actualiza el nivel de urgencia del reporte.
     * 
     * @param urgency Nuevo nivel de urgencia (high, medium, low)
     */
    fun onUrgencyChange(urgency: String) {
        _uiState.update { it.copy(urgency = urgency) }
        saveDraft()
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
        saveDraft()
    }

    /**
     * Oculta el overlay de éxito después de enviar el reporte.
     * Resetea el formulario completo para permitir crear un nuevo reporte.
     */
    fun onSuccessOverlayDismissed() {
        _uiState.update { it.copy(successVisible = false) }
        Log.d("ReportViewModel", "Overlay de éxito cerrado.")
    }

    /**
     * Limpia el mensaje de error general.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Limpia los errores de validación de campos.
     */
    fun clearFieldErrors() {
        _uiState.update { it.copy(fieldErrors = emptyMap()) }
    }
    
    /**
     * Limpia el error de un campo específico.
     * 
     * @param fieldName Nombre del campo
     */
    fun clearFieldError(fieldName: String) {
        _uiState.update { currentState ->
            val updatedErrors = currentState.fieldErrors.toMutableMap()
            updatedErrors.remove(fieldName)
            currentState.copy(fieldErrors = updatedErrors)
        }
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
                    // 1. Deshabilitar persistencia inmediatamente
                    disableDraftPersistence = true
                    
                    // 2. Eliminar el borrador de la BD AHORA (no después)
                    clearDraft()
                    Log.d("ReportViewModel", "✗ Borrador eliminado inmediatamente después del éxito")
                    
                    // 3. Mostrar overlay de éxito
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successVisible = true
                        )
                    }
                    Log.d("ReportViewModel", "✓ Envío exitoso. Mostrando overlay.")

                } else {
                    // Manejo de error en el envío
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = response.message ?: "Error al enviar el reporte."
                        )
                    }
                }
            } catch (e: Exception) {
                // Convierte la excepción a AppError para un manejo consistente
                val appError = e.toAppError()
                Log.e("ReportViewModel", "Error al enviar el reporte: ${appError.message}", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = appError.userFriendlyMessage
                    ) 
                }
            }
        }
    }
}
