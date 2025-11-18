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
    @SerialName("poligono") val poligono: GeoJsonPolygonDto,  // ← El backend envía un objeto GeoJSON
    @SerialName("nivel") val nivel: NivelDto,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

/**
 * DTO para el objeto GeoJSON Polygon que envía el backend.
 * Formato: { "type": "Polygon", "coordinates": [[...]] }
 */
@Serializable
data class GeoJsonPolygonDto(
    @SerialName("type") val type: String,
    @SerialName("coordinates") val coordinates: List<List<List<Double>>>  // GeoJSON usa array anidado: [[[lng, lat], [lng, lat], ...]]
)

/**
 * DTO para el objeto anidado "nivel".
 */
@Serializable
data class NivelDto(
    @SerialName("id_nivel") val idNivel: Int,
    @SerialName("codigo") val codigo: String,
    @SerialName("descripcion") val descripcion: String? = null, // Es nulable, como en el JSON
    @SerialName("created_at") val createdAt: String? = null,    // Puede ser null
    @SerialName("updated_at") val updatedAt: String? = null     // Puede ser null
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
    // El backend envía el polígono en formato GeoJSON: { "type": "Polygon", "coordinates": [[[lat, lng], ...]] }
    // Nota: El backend NO sigue el estándar GeoJSON que usa [lng, lat], sino que envía [lat, lng]
    // El primer nivel es el array de anillos (rings), el segundo es el array de coordenadas
    // Tomamos el primer anillo (exterior) y convertimos las coordenadas
    val coordinates = this.poligono.coordinates.firstOrNull() ?: emptyList()
    
    val mappedArea = coordinates.map { coord ->
        // El backend envía [latitude, longitude] (no el estándar GeoJSON [longitude, latitude])
        // coord[0] = latitude, coord[1] = longitude
        val lat = coord.getOrNull(0) ?: 0.0
        val lng = coord.getOrNull(1) ?: 0.0
        SerializableLatLng(latitude = lat, longitude = lng)
    }
    
    return RiskZone(
        id = this.idZona.toString(),
        name = this.identificador, // Usamos 'identificador' como el nombre
        riskLevel = when(this.nivel.codigo.uppercase()) { // Normalizamos a mayúsculas para comparación
            "ALTO" -> "ALTO"
            "MODERADO" -> "MEDIO"  // El backend usa "MODERADO", nosotros usamos "MEDIO"
            "BAJO" -> "BAJO"
            else -> {
                android.util.Log.w("RiskZoneDto", "Código de nivel desconocido: '${this.nivel.codigo}'. Usando 'ALTO' por defecto.")
                "ALTO"  // Por defecto, asumimos alto riesgo si no reconocemos el código
            }
        },
        area = mappedArea
    )
}
