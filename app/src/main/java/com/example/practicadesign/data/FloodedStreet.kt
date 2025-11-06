package com.example.practicadesign.data

import kotlinx.serialization.Serializable

/**
 * Representa una calle inundada en el mapa.
 * 
 * Una calle inundada está definida por una ruta (polilínea) formada
 * por una lista de coordenadas.
 * 
 * @property id Identificador único de la calle inundada
 * @property path Lista de coordenadas que forman la ruta de la calle inundada
 */
@Serializable
data class FloodedStreet(
    val id: String,
    val path: List<SerializableLatLng>
)

