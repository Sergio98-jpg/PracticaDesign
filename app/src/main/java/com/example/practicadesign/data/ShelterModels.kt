package com.example.practicadesign.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Respuesta de la API del backend para refugios.
 * 
 * @property success Indica si la petición fue exitosa
 * @property message Mensaje de respuesta del servidor
 * @property data Lista de DTOs de refugios
 */
@Serializable
data class SheltersApiResponse(
    val success: Boolean,
    val message: String,
    val data: List<ShelterDto>
)

/**
 * DTO (Data Transfer Object) de refugio recibido desde el backend.
 * 
 * Representa la estructura de datos tal como viene del servidor.
 */
@Serializable
data class ShelterDto(
    @SerialName("id_refugio")
    val idRefugio: Int,
    val nombre: String,
    val direccion: String,
    @SerialName("capacidad_total")
    val capacidadTotal: Int,
    @SerialName("capacidad_actual")
    val capacidadActual: Int,
    @SerialName("estado_refugio_id")
    val estadoRefugioId: Int,
    @SerialName("telefono_contacto")
    val telefonoContacto: String? = null,  // Puede ser null en el backend
    val responsable: String? = null,       // Puede ser null en el backend
    val latitud: String,
    val longitud: String,
    @SerialName("created_at")
    val createdAt: String? = null,         // Puede ser null en el backend
    @SerialName("updated_at")
    val updatedAt: String? = null,         // Puede ser null en el backend
    val municipio: MunicipioDto? = null,   // Puede ser null si no hay relación
    val estado: EstadoRefugioDto,
    val servicios: List<ServicioDto> = emptyList()
)

/**
 * DTO de municipio asociado a un refugio.
 */
@Serializable
data class MunicipioDto(
    @SerialName("id_municipio")
    val idMunicipio: Int,
    val nombre: String,
    @SerialName("codigo_inegi")
    val codigoInegi: String
)

/**
 * DTO del estado de un refugio (Abierto/Cerrado).
 */
@Serializable
data class EstadoRefugioDto(
    @SerialName("id_estado_refugio")
    val idEstadoRefugio: Int,
    val codigo: String,
    val descripcion: String
)

/**
 * DTO de servicio disponible en un refugio.
 */
@Serializable
data class ServicioDto(
    val id: Int? = null,
    val nombre: String? = null
)

/**
 * Modelo de dominio de refugio utilizado en la aplicación.
 * 
 * Representa un refugio con toda la información necesaria para la UI.
 * 
 * @property id Identificador único del refugio
 * @property name Nombre del refugio
 * @property isOpen Indica si el refugio está abierto o cerrado
 * @property address Dirección del refugio
 * @property capacity Capacidad total del refugio
 * @property currentOccupancy Ocupación actual del refugio
 * @property latitude Latitud de la ubicación del refugio
 * @property longitude Longitud de la ubicación del refugio
 * @property phoneContact Teléfono de contacto del refugio
 * @property responsible Persona responsable del refugio
 */
data class Shelter(
    val id: String,
    val name: String,
    val isOpen: Boolean,
    val address: String,
    val capacity: Int,
    val currentOccupancy: Int,
    val latitude: Double,
    val longitude: Double,
    val phoneContact: String = "",
    val responsible: String = ""
) {
    val position: LatLng
        get() = LatLng(latitude, longitude)

    // Propiedad útil para UI
    val availableSpaces: Int
        get() = capacity - currentOccupancy

    val occupancyPercentage: Float
        get() = if (capacity > 0) (currentOccupancy.toFloat() / capacity) * 100 else 0f
}

/**
 * Convierte un DTO de refugio a un objeto de dominio.
 * 
 * Mapea los datos del formato del backend al formato utilizado en la aplicación,
 * incluyendo la conversión de tipos y validaciones.
 * 
 * Estados del backend:
 * - OPERATIVO: Refugio operativo y disponible (abierto)
 * - LLENO: Refugio al máximo de capacidad (abierto pero lleno)
 * - MANTENIMIENTO: Refugio en mantenimiento (cerrado)
 * - CERRADO: Refugio temporalmente cerrado (cerrado)
 * - EMERGENCIA: Solo para emergencias críticas (considerado abierto con restricciones)
 * 
 * @return Objeto Shelter con los datos del refugio
 */
fun ShelterDto.toDomain(): Shelter {
    // Un refugio se considera "abierto" si está en estado OPERATIVO, LLENO o EMERGENCIA
    // Se considera "cerrado" si está en MANTENIMIENTO o CERRADO
    val isOpen = when (estado.codigo) {
        "OPERATIVO", "LLENO", "EMERGENCIA" -> true
        "MANTENIMIENTO", "CERRADO" -> false
        else -> false // Por defecto cerrado si hay un estado desconocido
    }
    
    return Shelter(
        id = idRefugio.toString(),
        name = nombre,
        isOpen = isOpen,
        address = direccion,
        capacity = capacidadTotal,
        currentOccupancy = capacidadActual,
        latitude = latitud.toDoubleOrNull() ?: 0.0,
        longitude = longitud.toDoubleOrNull() ?: 0.0,
        phoneContact = telefonoContacto ?: "",  // Valor por defecto si es null
        responsible = responsable ?: ""         // Valor por defecto si es null
    )
}

