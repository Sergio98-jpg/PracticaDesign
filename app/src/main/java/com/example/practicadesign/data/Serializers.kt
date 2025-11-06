package com.example.practicadesign.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


// ... en tu paquete data ...
@Serializable
data class SerializableLatLng(val latitude: Double, val longitude: Double)

// Función para convertir nuestro modelo al de Google Maps
fun SerializableLatLng.toGoogleLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

// Función para convertir el de Google Maps a nuestro modelo
fun LatLng.toSerializableLatLng(): SerializableLatLng {
    return SerializableLatLng(this.latitude, this.longitude)
}

