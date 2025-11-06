// En data/RiskZoneModels.kt

package com.example.practicadesign.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa la respuesta completa de la API para el endpoint de zonas de riesgo.
 * Esta clase no cambia.
 */
@Serializable
data class RiskZoneApiResponse(
    val success: Boolean,
    val message: String,
    val data: List<RiskZoneDto> // La lista de nuestros nuevos DTOs
)

/**
 * Data Transfer Object (DTO) que coincide EXACTAMENTE con el objeto de zona de riesgo del JSON.
 * Usamos @SerialName para mapear los nombres de la API a nuestras variables.
 */
@Serializable
data class RiskZoneDto(
    @SerialName("id_zona") val idZona: Int,
    @SerialName("identificador") val identificador: String,
    @SerialName("id_nivel") val idNivel: Int,
    @SerialName("poligono") val poligono: PoligonoDto,
    @SerialName("nivel") val nivel: NivelDto,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * DTO para el objeto anidado "poligono".
 */
@Serializable
data class PoligonoDto(
    @SerialName("area") val area: List<CoordenadaDto>
)

/**
 * DTO para el objeto anidado "nivel".
 */
@Serializable
data class NivelDto(
    @SerialName("id_nivel") val idNivel: Int,
    @SerialName("codigo") val codigo: String,
    @SerialName("descripcion") val descripcion: String? // Es nulable, como en el JSON
)

/**
 * DTO para una coordenada geográfica.
 * El JSON usa "lat" y "lng", así que lo mapeamos.
 */
@Serializable
data class CoordenadaDto(
    @SerialName("lat") val latitud: Double,
    @SerialName("lng") val longitud: Double
)


// --- FUNCIÓN DE MAPEO A DOMINIO (LA MÁS IMPORTANTE) ---

/**
 * Función de extensión para convertir el DTO (`RiskZoneDto`) a un objeto de dominio (`RiskZone`).
 *
 * Esta función es la que nos protege de los cambios del backend. Si el JSON cambia,
 * solo tenemos que cambiar esta función y los DTOs, pero nuestro objeto `RiskZone`
 * de la aplicación permanece intacto.
 */
fun RiskZoneDto.toDomain(): RiskZone {
    return RiskZone(
        id = this.idZona.toString(),
        name = this.identificador, // Usamos 'identificador' como el nombre
        riskLevel = when(this.nivel.codigo) { // Traducimos el código a nuestros valores ("ALTO", "MEDIO", etc.)
            "Peligro" -> "ALTO"
            "Precaución" -> "MEDIO"
            "Área Segura" -> "BAJO"
            else -> "DESCONOCIDO" // Un valor por defecto por si llega algo inesperado
        },
        area = this.poligono.area.map { SerializableLatLng(it.latitud, it.longitud) }
    )
}
