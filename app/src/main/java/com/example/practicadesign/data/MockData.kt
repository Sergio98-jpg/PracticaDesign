package com.example.practicadesign.data

/**
 * Objeto singleton que contiene datos simulados (mock) para desarrollo y testing.
 * 
 * Estos datos se utilizan cuando no hay conexión con el backend o para
 * simular calles inundadas durante el desarrollo.
 * 
 * Nota: Las zonas de riesgo ahora se obtienen desde el backend mediante
 * el método `getRiskZones()` del `MapRepository`.
 * 
 * En producción, estos datos serían reemplazados por llamadas reales a la API.
 */
object MockData {

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
