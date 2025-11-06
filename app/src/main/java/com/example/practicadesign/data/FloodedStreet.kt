package com.example.practicadesign.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/* -------------------------
   Calles (Estructura)
   ------------------------- */
@Serializable
data class FloodedStreet(
    val id: String,
    val path: List<SerializableLatLng> // ✅ Usa la clase que sí es serializable
)

