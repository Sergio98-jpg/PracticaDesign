package com.example.practicadesign.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/* ===================================
   RESPUESTA DE LA API (Backend)
   =================================== */

@Serializable
data class SheltersApiResponse(
    val success: Boolean,
    val message: String,
    val data: List<ShelterDto>
)

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
    val telefonoContacto: String,
    val responsable: String,
    val latitud: String,
    val longitud: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    val municipio: MunicipioDto,
    val estado: EstadoRefugioDto,
    val servicios: List<ServicioDto> = emptyList()
)

@Serializable
data class MunicipioDto(
    @SerialName("id_municipio")
    val idMunicipio: Int,
    val nombre: String,
    @SerialName("codigo_inegi")
    val codigoInegi: String
)

@Serializable
data class EstadoRefugioDto(
    @SerialName("id_estado_refugio")
    val idEstadoRefugio: Int,
    val codigo: String,
    val descripcion: String
)

@Serializable
data class ServicioDto(
    val id: Int? = null,
    val nombre: String? = null
)

/* ===================================
   MODELO DE DOMINIO (App)
   =================================== */

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

/* ===================================
   MAPPER (Conversión)
   =================================== */

fun ShelterDto.toDomain(): Shelter {
    return Shelter(
        id = idRefugio.toString(),
        name = nombre,
        isOpen = estado.codigo == "ABIERTO",
        address = direccion,
        capacity = capacidadTotal,
        currentOccupancy = capacidadActual,
        latitude = latitud.toDoubleOrNull() ?: 0.0,
        longitude = longitud.toDoubleOrNull() ?: 0.0,
        //phoneContact = telefonoContacto,
        //responsible = responsable
    )
}

