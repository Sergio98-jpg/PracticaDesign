package com.example.practicadesign.data

import com.example.practicadesign.ui.mapa.componentes.BannerState
import kotlinx.serialization.Serializable

/**
 * Representa una zona de riesgo de inundación en el mapa.
 * 
 * Una zona de riesgo está definida por un polígono (lista de coordenadas)
 * y un nivel de riesgo (Warning, Danger, Safe).
 * 
 * @property id Identificador único de la zona de riesgo
 * @property area Lista de coordenadas que forman el polígono de la zona
 * @property state Nivel de riesgo de la zona (Warning, Danger, Safe)
 */
@Serializable
/*
data class RiskZone(
    val id: String,
    val area: List<SerializableLatLng>,
    val state: BannerState
)*/

data class RiskZone(
    val id: String,
    val name: String,
    val riskLevel: String, // Podría ser un enum en el futuro para más seguridad de tipos
    val area: List<SerializableLatLng>
)