package com.example.practicadesign.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para gestionar el estado de autenticación de la aplicación.
 * 
 * Mantiene el estado de autenticación del usuario, incluyendo:
 * - Si el usuario está autenticado
 * - El rol del usuario (admin, user, null)
 * 
 * Este ViewModel es compartido a través de toda la aplicación para permitir
 * navegación protegida y control de acceso basado en roles.
 */
class AuthViewModel : ViewModel() {
    /**
     * Estado de autenticación del usuario.
     * 
     * @property isLoggedIn Indica si el usuario está autenticado
     * @property userRole Rol del usuario (admin, user) o null si no está autenticado
     */
    data class AuthState(
        val isLoggedIn: Boolean = false,
        val userRole: String? = null
    )
    
    // Estado mutable privado
    private val _authState = MutableStateFlow(AuthState())
    
    // Estado inmutable público
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    /**
     * Rol del usuario (compatibilidad con código existente).
     * 
     * @deprecated Usar authState.value.userRole en su lugar
     */
    @Deprecated("Usar authState.value.userRole en su lugar")
    val userRole: StateFlow<String?> = _authState.map { it.userRole }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    /**
     * Indica si el usuario está autenticado.
     */
    val isLoggedIn: StateFlow<Boolean> = _authState.map { it.isLoggedIn }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    /**
     * Maneja un login exitoso.
     * 
     * Actualiza el estado de autenticación con el rol del usuario.
     * 
     * @param role Rol del usuario (admin, user)
     */
    fun onLoginSuccess(role: String) {
        _authState.value = AuthState(
            isLoggedIn = true,
            userRole = role
        )
    }
    
    /**
     * Maneja el cierre de sesión.
     * 
     * Resetea el estado de autenticación a no autenticado.
     */
    fun onLogout() {
        _authState.value = AuthState(
            isLoggedIn = false,
            userRole = null
        )
    }
    
    /**
     * Verifica si el usuario tiene un rol específico.
     * 
     * @param role Rol a verificar (admin, user)
     * @return true si el usuario tiene el rol especificado
     */
    fun hasRole(role: String): Boolean {
        return _authState.value.userRole == role
    }
    
    /**
     * Verifica si el usuario tiene permisos de administrador.
     * 
     * @return true si el usuario es admin
     */
    fun isAdmin(): Boolean {
        return hasRole("admin")
    }
    
    /**
     * Verifica si el usuario tiene permisos de usuario regular.
     * 
     * @return true si el usuario es user o admin
     */
    fun isUser(): Boolean {
        val role = _authState.value.userRole
        return role == "user" || role == "admin"
    }
}
