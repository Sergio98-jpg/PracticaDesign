package com.example.practicadesign.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

/**
 * Representa una coordenada geográfica serializable.
 * 
 * Esta clase es necesaria porque `LatLng` de Google Maps no es serializable
 * directamente. Se utiliza para serializar/deserializar coordenadas en JSON.
 * 
 * @property latitude Latitud de la coordenada
 * @property longitude Longitud de la coordenada
 */
@Serializable
data class SerializableLatLng(
    val latitude: Double,
    val longitude: Double
)

/**
 * Convierte un SerializableLatLng a un LatLng de Google Maps.
 * 
 * @return Objeto LatLng de Google Maps
 */
fun SerializableLatLng.toGoogleLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

/**
 * Convierte un LatLng de Google Maps a un SerializableLatLng.
 * 
 * @return Objeto SerializableLatLng
 */
fun LatLng.toSerializableLatLng(): SerializableLatLng {
    return SerializableLatLng(this.latitude, this.longitude)
}

