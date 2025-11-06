package com.example.practicadesign.data

import com.example.practicadesign.ui.mapa.componentes.BannerState
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

/* -------------------------
   Zona de riesgo (Estructura)
   ------------------------- */
@Serializable
data class RiskZone(
    val id: String,
    val area: List<SerializableLatLng>,
    val state: BannerState
)