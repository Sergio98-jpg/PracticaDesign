package com.example.practicadesign.ui.reportes

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

/**
 * Representa el estado de la UI para la pantalla de Reportes ([ReportScreen]).
 *
 * Esta data class es la única fuente de verdad para la UI. Contiene toda la información
 * necesaria para que la pantalla se dibuje a sí misma en un momento dado.
 *
 * @property currentStep Paso actual del formulario (1-5)
 * @property selectedType Tipo de reporte seleccionado (inundacion, calle-bloqueada, etc.)
 * @property selectedLocation Tipo de ubicación seleccionada ("current" o "map")
 * @property locationCoordinates Coordenadas de la ubicación seleccionada (null si no se ha seleccionado)
 * @property title Título del reporte
 * @property description Descripción del reporte
 * @property urgency Nivel de urgencia (high, medium, low)
 * @property photos Lista de URIs de las fotos adjuntas (máximo 3)
 * @property isLoading Indica si se está enviando el reporte
 * @property errorMessage Mensaje de error si ocurre algún problema
 * @property successVisible Indica si se muestra el overlay de éxito
 */
data class ReportUiState(
    val currentStep: Int = 1,
    val selectedType: String? = null,
    val selectedLocation: String? = null,
    val locationCoordinates: LatLng? = null,
    val title: String = "",
    val description: String = "",
    val urgency: String = "medium",
    val photos: List<Uri?> = List(3) { null },
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successVisible: Boolean = false,
    // Estados de validación para cada campo
    val fieldErrors: Map<String, String> = emptyMap()
) {
    companion object {
        const val TOTAL_STEPS = 5
        const val MAX_TITLE_LENGTH = 60
        const val MAX_DESCRIPTION_LENGTH = 500
        const val MAX_PHOTOS = 3
    }

    /**
     * Verifica si el formulario es válido para el paso actual.
     * 
     * @return true si el paso actual tiene todos los datos requeridos
     */
    fun isCurrentStepValid(): Boolean {
        return when (currentStep) {
            1 -> selectedType != null
            2 -> selectedLocation != null && locationCoordinates != null
            3 -> title.isNotBlank() && description.isNotBlank() && fieldErrors.isEmpty()
            4 -> true // Las fotos son opcionales
            5 -> true // El resumen no requiere validación adicional
            else -> false
        }
    }
    
    /**
     * Obtiene el mensaje de error para un campo específico.
     * 
     * @param fieldName Nombre del campo
     * @return Mensaje de error o null si no hay error
     */
    fun getFieldError(fieldName: String): String? {
        return fieldErrors[fieldName]
    }
    
    /**
     * Verifica si un campo específico tiene error.
     * 
     * @param fieldName Nombre del campo
     * @return true si el campo tiene error
     */
    fun hasFieldError(fieldName: String): Boolean {
        return fieldErrors.containsKey(fieldName)
    }

    /**
     * Verifica si el formulario completo es válido para enviar.
     * 
     * @return true si todos los campos requeridos están completos
     */
    fun isFormValid(): Boolean {
        return selectedType != null &&
                selectedLocation != null &&
                title.isNotBlank() &&
                description.isNotBlank()
    }
}

/**
 * Enum que representa los tipos de reporte disponibles.
 */
enum class ReportType(val key: String, val displayName: String) {
    INUNDACION("inundacion", "Inundación"),
    CALLE_BLOQUEADA("calle-bloqueada", "Calle Bloqueada"),
    REFUGIO_LLENO("refugio-lleno", "Refugio Lleno"),
    DANO_INFRAESTRUCTURA("dano-infraestructura", "Daño a Infraestructura"),
    PERSONA_RIESGO("persona-riesgo", "Persona en Riesgo"),
    OTRO("otro", "Otro");

    companion object {
        fun fromKey(key: String): ReportType? {
            return values().find { it.key == key }
        }
    }
}

/**
 * Enum que representa los niveles de urgencia.
 */
enum class UrgencyLevel(val key: String, val displayName: String, val emoji: String) {
    HIGH("high", "Alta", "🔴"),
    MEDIUM("medium", "Media", "🟡"),
    LOW("low", "Baja", "🟢");

    companion object {
        fun fromKey(key: String): UrgencyLevel {
            return values().find { it.key == key } ?: MEDIUM
        }
    }
}

/**
 * Enum que representa los tipos de selección de ubicación.
 */
enum class LocationType(val key: String, val displayName: String) {
    CURRENT("current", "Mi ubicación actual"),
    MAP("map", "Seleccionar en mapa");

    companion object {
        fun fromKey(key: String): LocationType? {
            return values().find { it.key == key }
        }
    }
}

