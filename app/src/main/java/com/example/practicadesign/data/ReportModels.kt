package com.example.practicadesign.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Respuesta de la API del backend al enviar un reporte.
 * 
 * @property success Indica si la petición fue exitosa
 * @property message Mensaje de respuesta del servidor
 * @property data Datos del reporte creado (opcional)
 */
@Serializable
data class ReportApiResponse(
    val success: Boolean,
    val message: String,
    val data: ReportResponseDto? = null
)

/**
 * DTO de respuesta del backend cuando se crea un reporte.
 */
@Serializable
data class ReportResponseDto(
    @SerialName("id_reporte")
    val idReporte: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

/**
 * DTO (Data Transfer Object) para enviar un reporte al backend.
 * 
 * Representa la estructura de datos tal como se envía al servidor.
 * 
 * Nota: Los nombres de las propiedades deben coincidir exactamente con los nombres
 * que espera el backend. Se usa @SerialName para mapear nombres diferentes si es necesario.
 */
@Serializable
data class ReportDto(
    @SerialName("tipo_reporte")
    val tipoReporte: String,
    @SerialName("titulo")
    val titulo: String,
    @SerialName("descripcion")
    val descripcion: String,
    @SerialName("nivel_urgencia")
    val nivelUrgencia: String,
    @SerialName("latitud")
    val latitud: Double,
    @SerialName("longitud")
    val longitud: Double,
    @SerialName("tipo_ubicacion")
    val tipoUbicacion: String, // "current" o "map"
    @SerialName("fotos")
    val fotos: List<String> = emptyList() // URLs de las fotos subidas al servidor
)

/**
 * Modelo de dominio de reporte utilizado en la aplicación.
 * 
 * Representa un reporte con toda la información necesaria para la UI y el envío al backend.
 * 
 * @property type Tipo de reporte
 * @property title Título del reporte
 * @property description Descripción del reporte
 * @property urgency Nivel de urgencia
 * @property locationCoordinates Coordenadas de la ubicación
 * @property locationType Tipo de ubicación (current o map)
 * @property photoUris Lista de URIs de las fotos (a nivel local)
 */
data class Report(
    val type: String,
    val title: String,
    val description: String,
    val urgency: String,
    val locationCoordinates: LatLng,
    val locationType: String,
    val photoUris: List<String> = emptyList() // URLs después de subir al servidor
)

/**
 * Convierte un objeto de dominio Report a un DTO para enviar al backend.
 * 
 * Mapea los datos del formato de la aplicación al formato que espera el backend.
 * 
 * @return Objeto ReportDto con los datos listos para enviar
 */
fun Report.toDto(): ReportDto {
    return ReportDto(
        tipoReporte = type,
        titulo = title,
        descripcion = description,
        nivelUrgencia = urgency,
        latitud = locationCoordinates.latitude,
        longitud = locationCoordinates.longitude,
        tipoUbicacion = locationType,
        fotos = photoUris
    )
}

