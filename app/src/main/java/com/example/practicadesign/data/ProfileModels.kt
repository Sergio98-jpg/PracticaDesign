package com.example.practicadesign.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Respuesta de la API del backend para el perfil del usuario.
 * 
 * @property success Indica si la petición fue exitosa
 * @property message Mensaje de respuesta del servidor
 * @property data Datos del perfil del usuario
 */
@Serializable
data class ProfileApiResponse(
    val success: Boolean,
    val message: String,
    val data: ProfileDto
)

/**
 * DTO (Data Transfer Object) del perfil de usuario recibido desde el backend.
 * 
 * Representa la estructura de datos tal como viene del servidor.
 * 
 * Nota: Los nombres de las propiedades deben coincidir exactamente con los nombres
 * que envía el backend. Se usa @SerialName para mapear nombres diferentes si es necesario.
 */
@Serializable
data class ProfileDto(
    @SerialName("id_usuario")
    val idUsuario: Int? = null,
    val nombre: String? = null,
    @SerialName("apellido_paterno")
    val apellidoPaterno: String? = null,
    @SerialName("apellido_materno")
    val apellidoMaterno: String? = null,
    val email: String? = null,
    @SerialName("foto_perfil")
    val fotoPerfil: String? = null,
    val municipio: MunicipioProfileDto? = null,
    val estadisticas: EstadisticasDto? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * DTO de municipio asociado al perfil del usuario.
 */
@Serializable
data class MunicipioProfileDto(
    @SerialName("id_municipio")
    val idMunicipio: Int? = null,
    val nombre: String? = null,
    @SerialName("codigo_inegi")
    val codigoInegi: String? = null,
    val estado: EstadoProfileDto? = null
)

/**
 * DTO de estado asociado al municipio del usuario.
 */
@Serializable
data class EstadoProfileDto(
    @SerialName("id_estado")
    val idEstado: Int? = null,
    val nombre: String? = null,
    @SerialName("codigo_inegi")
    val codigoInegi: String? = null
)

/**
 * DTO de estadísticas del usuario.
 */
@Serializable
data class EstadisticasDto(
    @SerialName("reportes_enviados")
    val reportesEnviados: Int? = null,
    @SerialName("alertas_recibidas")
    val alertasRecibidas: Int? = null
)

/**
 * Modelo de dominio del perfil de usuario utilizado en la aplicación.
 * 
 * Representa el perfil del usuario con toda la información necesaria para la UI.
 * 
 * @property id Identificador único del usuario
 * @property name Nombre completo del usuario
 * @property location Ubicación del usuario (ciudad, estado)
 * @property avatarUrl URL de la foto de perfil del usuario
 * @property email Correo electrónico del usuario
 * @property reportsSent Número de reportes enviados
 * @property alertsReceived Número de alertas recibidas
 */
data class Profile(
    val id: String,
    val name: String,
    val location: String,
    val avatarUrl: String,
    val email: String = "",
    val reportsSent: Int = 0,
    val alertsReceived: Int = 0
)

/**
 * Convierte un DTO de perfil a un objeto de dominio.
 * 
 * Mapea los datos del formato del backend al formato utilizado en la aplicación,
 * incluyendo la conversión de tipos y validaciones.
 * 
 * @return Objeto Profile con los datos del usuario
 */
fun ProfileDto.toDomain(): Profile {
    // Construir el nombre completo
    val nombreCompleto = buildString {
        if (!nombre.isNullOrBlank()) append(nombre.trim())
        if (!apellidoPaterno.isNullOrBlank()) {
            if (isNotEmpty()) append(" ")
            append(apellidoPaterno.trim())
        }
        if (!apellidoMaterno.isNullOrBlank()) {
            if (isNotEmpty()) append(" ")
            append(apellidoMaterno.trim())
        }
    }.takeIf { it.isNotBlank() } ?: "Usuario"

    // Construir la ubicación (municipio, estado)
    val ubicacion = buildString {
        val municipioNombre = municipio?.nombre?.trim()
        val estadoNombre = municipio?.estado?.nombre?.trim()
        
        if (!municipioNombre.isNullOrBlank()) {
            append(municipioNombre)
        }
        if (!estadoNombre.isNullOrBlank()) {
            if (isNotEmpty()) append(", ")
            append(estadoNombre)
        }
    }.takeIf { it.isNotBlank() } ?: "Ubicación no disponible"

    return Profile(
        id = idUsuario?.toString() ?: "",
        name = nombreCompleto,
        location = ubicacion,
        avatarUrl = fotoPerfil?.takeIf { it.isNotBlank() } ?: "",
        email = email?.takeIf { it.isNotBlank() } ?: "",
        reportsSent = estadisticas?.reportesEnviados ?: 0,
        alertsReceived = estadisticas?.alertasRecibidas ?: 0
    )
}

