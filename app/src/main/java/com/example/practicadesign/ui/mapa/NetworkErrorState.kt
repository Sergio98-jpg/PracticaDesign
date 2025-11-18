package com.example.practicadesign.ui.mapa

/**
 * Estado de error de red para el mapa.
 * 
 * Distingue entre dos escenarios:
 * 1. Sin conexión y sin cache: No hay datos disponibles
 * 2. Sin conexión pero con cache: Los datos mostrados provienen del cache y podrían no ser actuales
 */
sealed class NetworkErrorState {
    /**
     * No hay error de red.
     */
    data object None : NetworkErrorState()
    
    /**
     * Error de red sin cache disponible.
     * No hay datos disponibles para mostrar.
     */
    data object NoConnection : NetworkErrorState()
    
    /**
     * Error de red pero hay cache disponible.
     * Los datos mostrados provienen del cache y podrían no ser actuales.
     */
    data object UsingCache : NetworkErrorState()
    
    /**
     * Obtiene el mensaje de error apropiado para mostrar al usuario.
     */
    fun getMessage(): String? = when (this) {
        is None -> null
        is NoConnection -> "No hay conexión. No se pudieron cargar los datos del mapa."
        is UsingCache -> "Sin conexión. Mostrando datos guardados que podrían no estar actualizados."
    }
}

