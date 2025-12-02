package com.example.practicadesign.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicadesign.data.AppError
import com.example.practicadesign.data.auth.AuthRepository
import com.example.practicadesign.data.auth.RegisterRequest
import com.example.practicadesign.data.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado de autenticación de la aplicación.
 * 
 * Mantiene el estado de autenticación del usuario, incluyendo:
 * - Si el usuario está autenticado
 * - El rol del usuario (admin, user, null)
 * - Estado de carga y errores
 * 
 * Este ViewModel es compartido a través de toda la aplicación para permitir
 * navegación protegida y control de acceso basado en roles.
 * 
 * Integra con AuthRepository para realizar operaciones de autenticación con el backend.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authRepository = AuthRepository(application)
    
    /**
     * Estado de autenticación del usuario.
     * 
     * @property isLoggedIn Indica si el usuario está autenticado
     * @property userRole Rol del usuario (admin, user) o null si no está autenticado
     * @property isLoading Indica si hay una operación de autenticación en curso
     * @property errorMessage Mensaje de error si ocurre algún problema
     */
    data class AuthState(
        val isLoggedIn: Boolean = false,
        val userRole: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )
    
    // Estado mutable privado
    private val _authState = MutableStateFlow(AuthState())
    
    // Estado inmutable público
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    companion object {
        private const val TAG = "AuthViewModel"
    }
    
    init {
        // Verificar si hay una sesión guardada al inicializar
        checkSavedSession()
    }
    
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
     * Verifica si hay una sesión guardada (token válido).
     * 
     * Se llama al inicializar el ViewModel para restaurar la sesión del usuario.
     */
    private fun checkSavedSession() {
        viewModelScope.launch {
            try {
                if (authRepository.isAuthenticated()) {
                    // Intentar obtener información del usuario para verificar que el token es válido
                    val user = authRepository.getCurrentUser().first()
                    _authState.value = AuthState(
                        isLoggedIn = true,
                        userRole = user.roleName,
                        isLoading = false,
                        errorMessage = null
                    )
                    Log.d(TAG, "Sesión restaurada: ${user.roleName}")
                } else {
                    Log.d(TAG, "No hay sesión guardada")
                }
            } catch (e: Exception) {
                // Si falla, limpiar el estado (token inválido o expirado)
                Log.w(TAG, "Error al verificar sesión guardada", e)
                _authState.value = AuthState(
                    isLoggedIn = false,
                    userRole = null,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }
    
    /**
     * Realiza el login de un usuario.
     * 
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param onSuccess Callback que se ejecuta cuando el login es exitoso
     * @param onError Callback que se ejecuta cuando ocurre un error
     */
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val authData = authRepository.login(email, password)
                    .catch { e ->
                        val error = e.toAppError()
                        val errorMessage = error.userFriendlyMessage
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                        onError(errorMessage)
                    }
                    .first()
                
                // Login exitoso
                _authState.value = AuthState(
                    isLoggedIn = true,
                    userRole = authData.user.roleName,
                    isLoading = false,
                    errorMessage = null
                )
                
                Log.d(TAG, "Login exitoso: ${authData.user.email} (${authData.user.roleName})")
                onSuccess()
                
            } catch (e: Exception) {
                val error = e.toAppError()
                val errorMessage = error.userFriendlyMessage
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
                Log.e(TAG, "Error en login", e)
                onError(errorMessage)
            }
        }
    }
    
    /**
     * Registra un nuevo usuario.
     * 
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param passwordConfirmation Confirmación de la contraseña
     * @param telefono Teléfono del usuario (opcional)
     * @param onSuccess Callback que se ejecuta cuando el registro es exitoso
     * @param onError Callback que se ejecuta cuando ocurre un error
     */
    fun register(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        telefono: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val request = RegisterRequest(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    password = password,
                    passwordConfirmation = passwordConfirmation,
                    telefono = telefono?.takeIf { it.isNotBlank() },
                    ubicacion = null,
                    latitud = null,
                    longitud = null
                )
                
                val authData = authRepository.register(request)
                    .catch { e ->
                        val error = e.toAppError()
                        val errorMessage = error.userFriendlyMessage
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                        onError(errorMessage)
                    }
                    .first()
                
                // Registro exitoso
                _authState.value = AuthState(
                    isLoggedIn = true,
                    userRole = authData.user.roleName,
                    isLoading = false,
                    errorMessage = null
                )
                
                Log.d(TAG, "Registro exitoso: ${authData.user.email} (${authData.user.roleName})")
                onSuccess()
                
            } catch (e: Exception) {
                val error = e.toAppError()
                val errorMessage = error.userFriendlyMessage
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
                Log.e(TAG, "Error en registro", e)
                onError(errorMessage)
            }
        }
    }
    
    /**
     * Maneja un login exitoso (método de compatibilidad).
     * 
     * @deprecated Usar login() en su lugar
     */
    @Deprecated("Usar login() en su lugar")
    fun onLoginSuccess(role: String) {
        _authState.value = AuthState(
            isLoggedIn = true,
            userRole = role
        )
    }
    
    /**
     * Maneja el cierre de sesión.
     * 
     * Revoca el token en el servidor y resetea el estado de autenticación.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout().first()
                _authState.value = AuthState(
                    isLoggedIn = false,
                    userRole = null,
                    isLoading = false,
                    errorMessage = null
                )
                Log.d(TAG, "Logout exitoso")
            } catch (e: Exception) {
                // Aunque falle en el servidor, limpiamos el estado local
                _authState.value = AuthState(
                    isLoggedIn = false,
                    userRole = null,
                    isLoading = false,
                    errorMessage = null
                )
                Log.w(TAG, "Error en logout (estado local limpiado)", e)
            }
        }
    }
    
    /**
     * Limpia el mensaje de error.
     */
    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
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
