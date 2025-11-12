package com.example.practicadesign.data

import kotlinx.serialization.Serializable

/**
 * Representa una zona de riesgo de inundación en el mapa.
 * 
 * Una zona de riesgo está definida por un polígono (lista de coordenadas),
 * un nombre identificador y un nivel de riesgo (ALTO, MEDIO, BAJO).
 * 
 * @property id Identificador único de la zona de riesgo
 * @property name Nombre identificador de la zona de riesgo
 * @property riskLevel Nivel de riesgo de la zona (ALTO, MEDIO, BAJO)
 * @property area Lista de coordenadas que forman el polígono de la zona
 */
@Serializable
data class RiskZone(
    val id: String,
    val name: String,
    val riskLevel: String, // Podría ser un enum en el futuro para más seguridad de tipos
    val area: List<SerializableLatLng>
)