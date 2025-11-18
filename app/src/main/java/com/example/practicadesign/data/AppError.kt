package com.example.practicadesign.data

/**
 * Sistema de manejo de errores robusto para la aplicación.
 * 
 * Define todos los tipos de errores posibles de forma type-safe usando sealed classes.
 * Esto permite un manejo de errores consistente y específico en toda la aplicación.
 */
sealed class AppError(
    open val message: String,
    open val userFriendlyMessage: String,
    open val cause: Throwable? = null
) {
    /**
     * Errores relacionados con la red y conectividad.
     */
    sealed class NetworkError(
        message: String,
        userFriendlyMessage: String,
        cause: Throwable? = null
    ) : AppError(message, userFriendlyMessage, cause) {
        
        /**
         * No hay conexión a internet disponible.
         */
        data object NoConnection : NetworkError(
            message = "No hay conexión a internet",
            userFriendlyMessage = "No hay conexión a internet. Verifique su red e intente nuevamente."
        )
        
        /**
         * Timeout en la petición (la conexión tardó demasiado).
         */
        data object Timeout : NetworkError(
            message = "Timeout en la petición",
            userFriendlyMessage = "La conexión tardó demasiado. Verifique su red e intente nuevamente."
        )
        
        /**
         * Error desconocido de red.
         */
        data class Unknown(
            override val cause: Throwable? = null
        ) : NetworkError(
            message = "Error de red desconocido",
            userFriendlyMessage = "Error de conexión. Verifique su red e intente nuevamente.",
            cause = cause
        )
    }
    
    /**
     * Errores relacionados con la autenticación.
     */
    sealed class AuthError(
        message: String,
        userFriendlyMessage: String,
        cause: Throwable? = null
    ) : AppError(message, userFriendlyMessage, cause) {
        
        /**
         * Credenciales inválidas (email o contraseña incorrectos).
         */
        data object InvalidCredentials : AuthError(
            message = "Credenciales inválidas",
            userFriendlyMessage = "Email o contraseña incorrectos. Verifique sus datos e intente nuevamente."
        )
        
        /**
         * Sesión expirada o token inválido.
         */
        data object SessionExpired : AuthError(
            message = "Sesión expirada",
            userFriendlyMessage = "Su sesión ha expirado. Por favor, inicie sesión nuevamente."
        )
        
        /**
         * Usuario no autenticado (no hay sesión activa).
         */
        data object NotAuthenticated : AuthError(
            message = "Usuario no autenticado",
            userFriendlyMessage = "Debe iniciar sesión para acceder a esta funcionalidad."
        )
    }
    
    /**
     * Errores relacionados con validación de datos.
     */
    sealed class ValidationError(
        message: String,
        userFriendlyMessage: String,
        cause: Throwable? = null
    ) : AppError(message, userFriendlyMessage, cause) {
        
        /**
         * Campo requerido está vacío.
         */
        data class RequiredField(
            val fieldName: String
        ) : ValidationError(
            message = "Campo requerido: $fieldName",
            userFriendlyMessage = "El campo '$fieldName' es obligatorio. Por favor, complételo."
        )
        
        /**
         * Campo excede la longitud máxima permitida.
         */
        data class MaxLengthExceeded(
            val fieldName: String,
            val maxLength: Int,
            val currentLength: Int
        ) : ValidationError(
            message = "Campo $fieldName excede longitud máxima: $currentLength/$maxLength",
            userFriendlyMessage = "El campo '$fieldName' no puede tener más de $maxLength caracteres. Actualmente tiene $currentLength."
        )
        
        /**
         * Campo no cumple con el formato requerido (ej: email, teléfono).
         */
        data class InvalidFormat(
            val fieldName: String,
            val expectedFormat: String
        ) : ValidationError(
            message = "Formato inválido para $fieldName. Se esperaba: $expectedFormat",
            userFriendlyMessage = "El formato del campo '$fieldName' no es válido. $expectedFormat"
        )
        
        /**
         * Ubicación no seleccionada o inválida.
         */
        data object InvalidLocation : ValidationError(
            message = "Ubicación no seleccionada o inválida",
            userFriendlyMessage = "Por favor, seleccione una ubicación válida para el reporte."
        )
    }
    
    /**
     * Errores relacionados con el servidor (errores 5xx).
     */
    sealed class ServerError(
        message: String,
        userFriendlyMessage: String,
        cause: Throwable? = null
    ) : AppError(message, userFriendlyMessage, cause) {
        
        /**
         * Error interno del servidor (500).
         */
        data object InternalServerError : ServerError(
            message = "Error interno del servidor",
            userFriendlyMessage = "El servidor está experimentando problemas. Intente más tarde."
        )
        
        /**
         * Servicio no disponible (503).
         */
        data object ServiceUnavailable : ServerError(
            message = "Servicio no disponible",
            userFriendlyMessage = "El servicio no está disponible en este momento. Intente más tarde."
        )
        
        /**
         * Error desconocido del servidor.
         */
        data class Unknown(
            val statusCode: Int,
            override val cause: Throwable? = null
        ) : ServerError(
            message = "Error del servidor: $statusCode",
            userFriendlyMessage = "Error del servidor. Intente más tarde.",
            cause = cause
        )
    }
    
    /**
     * Errores relacionados con peticiones incorrectas (errores 4xx).
     */
    sealed class ClientError(
        message: String,
        userFriendlyMessage: String,
        cause: Throwable? = null
    ) : AppError(message, userFriendlyMessage, cause) {
        
        /**
         * Recurso no encontrado (404).
         */
        data object NotFound : ClientError(
            message = "Recurso no encontrado",
            userFriendlyMessage = "No se pudo encontrar el recurso solicitado."
        )
        
        /**
         * Petición incorrecta (400).
         */
        data object BadRequest : ClientError(
            message = "Petición incorrecta",
            userFriendlyMessage = "La petición es incorrecta. Verifique los datos e intente nuevamente."
        )
        
        /**
         * No autorizado (401).
         */
        data object Unauthorized : ClientError(
            message = "No autorizado",
            userFriendlyMessage = "No tiene permisos para realizar esta acción."
        )
    }
    
    /**
     * Errores relacionados con almacenamiento local (Room, DataStore, etc.).
     */
    sealed class StorageError(
        message: String,
        userFriendlyMessage: String,
        cause: Throwable? = null
    ) : AppError(message, userFriendlyMessage, cause) {
        
        /**
         * Error al guardar datos localmente.
         */
        data class SaveFailed(
            override val cause: Throwable? = null
        ) : StorageError(
            message = "Error al guardar datos localmente",
            userFriendlyMessage = "No se pudieron guardar los datos. Intente nuevamente.",
            cause = cause
        )
        
        /**
         * Error al leer datos localmente.
         */
        data class ReadFailed(
            override val cause: Throwable? = null
        ) : StorageError(
            message = "Error al leer datos localmente",
            userFriendlyMessage = "No se pudieron leer los datos guardados.",
            cause = cause
        )
    }
    
    /**
     * Error desconocido o inesperado.
     */
    data class Unknown(
        override val message: String = "Error desconocido",
        override val userFriendlyMessage: String = "Ocurrió un error inesperado. Intente nuevamente.",
        override val cause: Throwable? = null
    ) : AppError(message, userFriendlyMessage, cause)
}

/**
 * Extensión para convertir excepciones comunes a AppError.
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        is java.net.UnknownHostException,
        is java.net.ConnectException -> AppError.NetworkError.NoConnection
        is java.net.SocketTimeoutException -> AppError.NetworkError.Timeout
        is java.io.IOException -> AppError.NetworkError.Unknown(this)
        else -> AppError.Unknown(
            message = this.message ?: "Error desconocido",
            cause = this
        )
    }
}

