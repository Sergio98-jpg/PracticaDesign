package com.example.practicadesign.data

import com.example.practicadesign.ui.mapa.componentes.BannerState

/**
 * Objeto singleton que contiene datos simulados (mock) para desarrollo y testing.
 * 
 * Estos datos se utilizan cuando no hay conexión con el backend o para
 * simular zonas de riesgo y calles inundadas durante el desarrollo.
 * 
 * En producción, estos datos serían reemplazados por llamadas reales a la API.
 */
object MockData {

    /**
     * Lista de zonas de riesgo simuladas para testing y desarrollo.
     */
    val riskZones: List<RiskZone> = listOf(
        RiskZone(
            id = "zone_warning_1",
            area = listOf(SerializableLatLng(19.45, -99.15), SerializableLatLng(19.45, -99.14), SerializableLatLng(19.44, -99.14), SerializableLatLng(19.44, -99.15)),
            state = BannerState.Warning
        ),
        RiskZone(
            id = "zone_danger_1",
            area = listOf(SerializableLatLng(19.42, -99.17), SerializableLatLng(19.42, -99.16), SerializableLatLng(19.41, -99.16), SerializableLatLng(19.41, -99.17)),
            state = BannerState.Danger
        ),
        RiskZone(
            id = "zone_danger_2",
            area = listOf(SerializableLatLng(19.44, -99.14), SerializableLatLng(19.44, -99.13), SerializableLatLng(19.43, -99.13), SerializableLatLng(19.43, -99.14)),
            state = BannerState.Warning
        )
    )

    /**
     * Lista de calles inundadas simuladas para testing y desarrollo.
     */
    val floodedStreets: List<FloodedStreet> = listOf(
        FloodedStreet(
            id = "street_1",
            path = listOf(SerializableLatLng(19.435, -99.140), SerializableLatLng(19.435, -99.135), SerializableLatLng(19.436, -99.135))
        ),
        FloodedStreet(
            id = "street_2",
            path = listOf(SerializableLatLng(19.410, -99.165), SerializableLatLng(19.415, -99.168), SerializableLatLng(19.418, -99.170))
        )
    )
}
