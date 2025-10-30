package com.example.practicadesign.data

import com.example.practicadesign.ui.mapa.componentes.BannerState
import com.example.practicadesign.ui.mapa.componentes.FloodedStreet
import com.example.practicadesign.ui.mapa.componentes.RiskZone
import com.example.practicadesign.ui.mapa.componentes.Shelter
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow

// En una app real, aquí inyectarías tu ApiService de Retrofit
class MapRepository() {

    private val webSocketListener = RiskZoneWebSocketListener()
    // El ViewModel llamará a esta función para obtener TODOS los datos del mapa
    suspend fun getMapData(): MapDataResult {
        // Mueve la operación de red fuera del hilo principal para no bloquear la UI
        return withContext(Dispatchers.IO) {
            // ===================================================================
            // AQUÍ VIVIRÁ LA LÓGICA DE TU API CUANDO LA IMPLEMENTES
            // val riskZonesFromApi = apiService.getRiskZones()
            // val sheltersFromApi = apiService.getShelters()
            // ... etc
            // ===================================================================

            // Por ahora, devolvemos los datos simulados que antes estaban en el ViewModel
            MapDataResult(
                riskZones = getSimulatedRiskZones(),
                shelters = getSimulatedShelters(),
                floodedStreets = getSimulatedFloodedStreets()
            )
        }
    }

    // ✅ NUEVA FUNCIÓN: Devuelve un Flow que emite actualizaciones de RiskZone
    fun getRiskZoneUpdates(): Flow<RiskZone> {
        return webSocketListener.listenForRiskZoneUpdates()
    }

    // Función para limpiar cuando el ViewModel se destruya
    fun closeWebSocket() {
        webSocketListener.close()
    }
    // --- MÉTODOS PRIVADOS PARA DATOS SIMULADOS ---
    // (Estos métodos los borrarás cuando tu backend esté listo)

    private fun getSimulatedRiskZones(): List<RiskZone> {
        return listOf(
            RiskZone("zone_warning_1",listOf(LatLng(19.45, -99.15), LatLng(19.45, -99.14), LatLng(19.44, -99.14), LatLng(19.44, -99.15)),BannerState.Warning ),
            RiskZone("zone_danger_1", listOf(LatLng(19.42, -99.17), LatLng(19.42, -99.16), LatLng(19.41, -99.16), LatLng(19.41, -99.17)), BannerState.Danger),
            RiskZone("zone_danger_2", listOf(LatLng(19.44, -99.14), LatLng(19.44, -99.13), LatLng(19.43, -99.13), LatLng(19.43, -99.14)), BannerState.Warning)
        )
    }

    private fun getSimulatedShelters(): List<Shelter> {
        return listOf(
            Shelter("shelter_1", LatLng(19.428, -99.155), "Refugio Deportivo Benito Juárez", true, "Av. de los Insurgentes Sur 300", 150, 95),
            Shelter("shelter_2",  LatLng(19.419, -99.162), "Centro Comunitario Roma",true, "Av. de los Insurgentes Sur 300", 150, 95),
            Shelter("shelter_3",  LatLng(19.44, -99.13), "Escuela Primaria (Cerrado)",false, "Av. de los Insurgentes Sur 300", 150, 95)
        )
    }

    private fun getSimulatedFloodedStreets(): List<FloodedStreet> {
        return listOf(
            FloodedStreet("street_1", listOf(LatLng(19.435, -99.140), LatLng(19.435, -99.135), LatLng(19.436, -99.135))),
            FloodedStreet("street_2", listOf(LatLng(19.410, -99.165), LatLng(19.415, -99.168), LatLng(19.418, -99.170)))
        )
    }
}

// Clase contenedora para devolver todos los datos a la vez
data class MapDataResult(
    val riskZones: List<RiskZone>,
    val shelters: List<Shelter>,
    val floodedStreets: List<FloodedStreet>
)
