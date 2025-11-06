package com.example.practicadesign.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado principal de la aplicación,
 * incluyendo el splash screen y la inicialización de la app.
 * Sigue el patrón MVVM para separar la lógica de negocio de la UI.
 */
class MainViewModel : ViewModel() {
    
    // Tiempo de duración del splash screen en milisegundos
    private val SPLASH_SCREEN_DURATION = 4000L
    
    // Estado que indica si el splash screen debe mostrarse
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible = _isSplashVisible.asStateFlow()
    
    init {
        // Inicia la carga de datos y el temporizador del splash
        startSplashTimer()
    }
    
    /**
     * Inicia el temporizador del splash screen.
     * Después del tiempo especificado, oculta el splash.
     */
    private fun startSplashTimer() {
        viewModelScope.launch {
            delay(SPLASH_SCREEN_DURATION)
            _isSplashVisible.value = false
        }
    }
    
    /**
     * Permite ocultar manualmente el splash screen si es necesario.
     * Útil para casos donde la carga de datos se completa antes del tiempo establecido.
     */
    fun hideSplash() {
        _isSplashVisible.value = false
    }
}

