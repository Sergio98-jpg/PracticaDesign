package com.example.practicadesign.ui.profile

/**
 * Representa el estado de la UI para la pantalla de Perfil ([ProfileScreen]).
 *
 * Esta data class es la única fuente de verdad para la UI. Contiene toda la información
 * necesaria para que la pantalla se dibuje a sí misma en un momento dado.
 *
 * @property isLoading Indica si se está realizando una carga inicial de datos.
 * @property userInfo Información del usuario (nombre, ubicación, foto de perfil).
 * @property stats Estadísticas del usuario (reportes enviados, alertas recibidas).
 * @property errorMessage Mensaje de error si ocurre algún problema al cargar los datos.
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val userInfo: UserInfo = UserInfo(),
    val stats: UserStats = UserStats(),
    val errorMessage: String? = null
)

/**
 * Información del usuario para mostrar en el perfil.
 *
 * @property name Nombre completo del usuario
 * @property location Ubicación del usuario (ciudad, estado)
 * @property avatarUrl URL de la foto de perfil del usuario
 */
data class UserInfo(
    val name: String = "",
    val location: String = "",
    val avatarUrl: String = ""
)

/**
 * Estadísticas del usuario.
 *
 * @property reportsSent Número de reportes enviados por el usuario
 * @property alertsReceived Número de alertas recibidas por el usuario
 */
data class UserStats(
    val reportsSent: Int = 0,
    val alertsReceived: Int = 0
)

