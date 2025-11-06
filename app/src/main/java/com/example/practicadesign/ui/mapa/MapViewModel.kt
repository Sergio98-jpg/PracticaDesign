// en /ui/map/MapViewModel.kt
package com.example.practicadesign.ui.mapa

import android.content.pm.PackageManager
import android.location.LocationRequest
import androidx.compose.animation.core.copy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.practicadesign.ui.mapa.componentes.BannerState
import com.example.practicadesign.ui.mapa.componentes.RiskZone

import com.google.android.gms.maps.model.LatLng // ✅ Importa LatLng
import androidx.lifecycle.viewModelScope // ✅ Importa viewModelScope
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import kotlinx.coroutines.launch // ✅ Importa launch
import com.google.maps.android.PolyUtil
import com.google.maps.android.PolyUtil.containsLocation
// import com.example.practicadesign.ui.mapa.componentes.Shelter
//import com.example.practicadesign.ui.mapa.componentes.FloodedStreet
import com.example.practicadesign.ui.mapa.componentes.SearchResult
import android.Manifest
import android.app.Application
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.*
import android.location.Geocoder
import java.util.Locale
import com.example.practicadesign.data.MapRepository
//import com.example.practicadesign.data.MapDataResult
import com.example.practicadesign.data.FloodedStreet
import com.example.practicadesign.data.RiskZone
import com.example.practicadesign.data.Shelter
import com.example.practicadesign.data.toGoogleLatLng
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first


// ====================================================================
// ✅ 1. DEFINICIÓN DEL ESTADO DE LA UI (DATA CLASSES)
// Estas clases son cruciales y deben estar aquí, en la parte superior.
// ====================================================================

data class MapUiState(
    val isLoading: Boolean = true,
    val networkError: String? = null,
    val isMenuOpen: Boolean = false,
    val userLocation: LatLng? = null,
    val bannerState: BannerState = BannerState.Safe,
    val riskZones: List<RiskZone> = emptyList(),
    val shelters: List<Shelter> = emptyList(),
    val floodedStreets: List<FloodedStreet> = emptyList(),
    val selectedShelter: Shelter? = null,
    val selectedRiskZone: RiskZone? = null,
    val filters: MapFilters = MapFilters(),
    val currentLocationName: String = "Cargando...",
    val isSearching: Boolean = false,
    val searchQuery: TextFieldValue = TextFieldValue(""),
    val searchResults: List<SearchResult> = emptyList()
)

data class MapFilters(
    val showRiskZones: Boolean = true,
    val showShelters: Boolean = true,
    val showFloodedStreets: Boolean = true
)
// ====================================================================
// ✅ 2. IMPLEMENTACIÓN DEL VIEWMODEL
// Esta es la lógica refactorizada y limpia que habíamos discutido.
// ====================================================================

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()
    private val mapRepository = MapRepository()
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private val geocoder = Geocoder(application, Locale.getDefault())

    // --- BLOQUE DE INICIALIZACIÓN ÚNICO ---
    init {
        fetchInitialMapData()
        //   loadMapDataFromBackend()
        listenForRealTimeUpdates()
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Obtenemos la nueva LatLng
                val newLatLng = LatLng(location.latitude, location.longitude)

                // --- LLAMA A LA FUNCIÓN DE COMPROBACIÓN AQUÍ ---
                // Cada vez que recibimos una nueva ubicación, verificamos
                // si el usuario está dentro de una zona de riesgo.
                checkUserLocationAgainstZones(newLatLng) // ✅ ¡INTEGRACIÓN CLAVE!
                // --- ✅ 2. REALIZA LA GEODIFICACIÓN INVERSA ---
                try {
                    val addresses = try {
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    } catch (e: IOException) {
                        null // Geocoder falló (sin internet, etc.)
                    }

                    // --- ✅ LÓGICA MEJORADA PARA OBTENER LA UBICACIÓN COMPLETA ---
                    val fullLocationName = if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0] // Guardamos la primera dirección en una variable

                        // 1. Obtenemos la parte principal (municipio o colonia)
                        val mainLocation = address.locality ?: address.subLocality

                        // 2. Obtenemos el estado o área administrativa
                        val adminArea = address.adminArea

                        // 3. Combinamos los dos, si existen
                        when {
                            // Si tenemos ambos, los unimos con una coma
                            !mainLocation.isNullOrBlank() && !adminArea.isNullOrBlank() -> "$mainLocation, $adminArea"
                            // Si solo tenemos el principal, lo mostramos
                            !mainLocation.isNullOrBlank() -> mainLocation
                            // Si solo tenemos el estado, lo mostramos (raro, pero posible)
                            !adminArea.isNullOrBlank() -> adminArea
                            // Si no tenemos nada, mostramos el mensaje por defecto
                            else -> "Ubicación desconocida"
                        }
                    } else {
                        "Ubicación desconocida"
                    }

                    // Actualiza el uiState con el nombre completo
                    _uiState.update {
                        it.copy(
                            userLocation = newLatLng,
                            currentLocationName = fullLocationName
                        )
                    }

                } catch (e: Exception) {
                    // Si Geocoder falla, al menos actualizamos la coordenada
                    _uiState.update {
                        it.copy(
                            userLocation = newLatLng,
                            currentLocationName = "Buscando ubicación..."
                        )
                    }
                }
            }
        }
    }

    // Esta función será llamada desde la UI cuando se concedan los permisos
    fun startLocationUpdates() {
        // ✅ Forma moderna usando Builder
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // intervalo en milisegundos
        ).apply {
            setMinUpdateIntervalMillis(5000L) // equivalente a fastestInterval
            setWaitForAccurateLocation(false)
        }.build()

        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // Limpia el callback cuando el ViewModel es destruido
    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        mapRepository.closeWebSocket()
    }
    // --- FIN DE LA LÓGICA DE UBICACIÓN ---

    // 2. Crea la función para manejar la acción del menú
    fun onMenuClicked() {
        _uiState.update { currentState ->
            currentState.copy(isMenuOpen = !currentState.isMenuOpen)
        }
    }

    // --- 1. CARGA DE DATOS ---

/*    private fun fetchInitialMapData() {
        _uiState.update { it.copy(isLoading = true) }

        // --- 1. Cargar Refugios REALES ---
        mapRepository.getShelters()
            .onEach { sheltersFromBackend ->
                _uiState.update { currentState ->
                    currentState.copy(
                        shelters = sheltersFromBackend,
                        networkError = null // Limpia cualquier error previo
                    )
                }
            }
            .catch { e ->
                // ✅ Manejo específico de errores de red
                println("❌ Error al cargar refugios: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        networkError = "No se pudieron cargar los refugios",
                        shelters = emptyList() // Lista vacía como fallback
                    )
                }
            }
            .launchIn(viewModelScope)

        // --- 2. Cargar Zonas de Riesgo SIMULADAS ---
        mapRepository.getMockRiskZones()
            .onEach { mockZones ->
                _uiState.update { currentState ->
                    currentState.copy(riskZones = mockZones)
                }
            }
            .catch { e ->
                println("❌ Error al cargar zonas de riesgo: ${e.message}")
                e.printStackTrace()
            }
            .launchIn(viewModelScope)

        // --- 3. Cargar Calles Inundadas SIMULADAS ---
        mapRepository.getMockFloodedStreets()
            .onEach { mockStreets ->
                _uiState.update { currentState ->
                    currentState.copy(floodedStreets = mockStreets)
                }
            }
            .catch { e ->
                println("❌ Error al cargar calles inundadas: ${e.message}")
                e.printStackTrace()
            }
            .launchIn(viewModelScope)

        // --- 4. Desactivar el 'isLoading' cuando TODO termine ---
        combine(
            mapRepository.getShelters().catch { emit(emptyList()) }, // ✅ Emite lista vacía si falla
            mapRepository.getMockRiskZones().catch { emit(emptyList()) },
            mapRepository.getMockFloodedStreets().catch { emit(emptyList()) }
        ) { _, _, _ ->
            false // isLoading = false
        }
            .onEach { isLoadingState ->
                _uiState.update { it.copy(isLoading = isLoadingState) }
            }
            .catch { e ->
                println("❌ Error crítico al cargar datos: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)
    }*/
private fun fetchInitialMapData() {
    _uiState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
        try {
            // Ejecuta todas las operaciones en paralelo
            val sheltersDeferred = async {
                mapRepository.getShelters().catch { emit(emptyList()) }.first()
            }
            val zonesDeferred = async {
                mapRepository.getMockRiskZones().catch { emit(emptyList()) }.first()
            }
            val streetsDeferred = async {
                mapRepository.getMockFloodedStreets().catch { emit(emptyList()) }.first()
            }

            // Espera a que todas terminen
            val shelters = sheltersDeferred.await()
            val zones = zonesDeferred.await()
            val streets = streetsDeferred.await()

            // Actualiza todo de una vez
            _uiState.update {
                it.copy(
                    shelters = shelters,
                    riskZones = zones,
                    floodedStreets = streets,
                    isLoading = false,
                    networkError = null
                )
            }
        } catch (e: Exception) {
            println("❌ Error crítico al cargar datos: ${e.message}")
            _uiState.update {
                it.copy(
                    isLoading = false,
                    networkError = "Error al cargar los datos del mapa"
                )
            }
        }
    }
}
    fun onShelterSelected(shelter: Shelter) {
        _uiState.update { it.copy(selectedShelter = shelter, selectedRiskZone = null) }
    }

    fun onZoneRiskSelected(zone: RiskZone) {
        _uiState.update { it.copy(selectedRiskZone = zone, selectedShelter = null) }
    }

    fun onBottomSheetDismissed() {
        _uiState.update { it.copy(selectedShelter = null, selectedRiskZone = null) }
    }

    private fun listenForRealTimeUpdates() {
        viewModelScope.launch {
            mapRepository.getRiskZoneUpdates().collect { newRiskZone ->
                _uiState.update { currentState ->
                    val updatedZones = currentState.riskZones.filterNot { it.id == newRiskZone.id } + newRiskZone
                    currentState.copy(riskZones = updatedZones)
                }
            }
        }
    }

    // ✅ AÑADE ESTA NUEVA FUNCIÓN PÚBLICA
    fun checkUserLocationAgainstZones(userLocation: LatLng) {
        viewModelScope.launch {
            val currentZones = uiState.value.riskZones
            if (currentZones.isEmpty()) return@launch // No hacer nada si las zonas no han cargado

            // 1. Busca la zona de mayor riesgo que contenga la ubicación del usuario.
            //    maxByOrNull usa el orden natural del enum (Danger > Warning > Safe).
            val highestRiskZone = currentZones
                .filter { zone -> containsLocation(userLocation, zone.area.map { it.toGoogleLatLng() }, false) }
                .maxByOrNull { it.state.ordinal }

            // 2. Determina el nuevo estado. Si no se encontró ninguna zona, es 'Safe'.
            val newBannerState = highestRiskZone?.state ?: BannerState.Safe

            // 3. Actualiza el UiState SOLO si el estado ha cambiado, para evitar redibujos innecesarios.
            if (newBannerState != uiState.value.bannerState) {
                _uiState.update { it.copy(bannerState = newBannerState) }
            }
        }
    }

    //Filtros
    fun toggleRiskZonesVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showRiskZones = !it.filters.showRiskZones)
            it.copy(filters = updatedFilters)
        }
    }

    fun toggleSheltersVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showShelters = !it.filters.showShelters)
            it.copy(filters = updatedFilters)
        }
    }

    fun toggleFloodedStreetsVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showFloodedStreets = !it.filters.showFloodedStreets)
            it.copy(filters = updatedFilters)
        }
    }

    //Busqueda

    fun onSearchQueryChange(newQuery: TextFieldValue) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        // ✅ LLAMA AL FILTRO AQUÍ
        filterResults(newQuery.text)
    }

    fun onSearchActive() {
        _uiState.update { it.copy(isSearching = true) }
        // ✅ Y LLAMA AL FILTRO AQUÍ TAMBIÉN
        filterResults("") // Pasa una cadena vacía para mostrar todos los resultados iniciales
    }

    fun onSearchInactive() {
        // Al desactivar, limpiamos el texto y el estado de búsqueda
        _uiState.update { it.copy(isSearching = false, searchQuery = TextFieldValue(""), searchResults = emptyList()) }
    }

    private fun filterResults(query: String) {
        // Tomamos la lista completa de refugios y zonas del estado actual
        val allItems = buildList {
            _uiState.value.shelters.forEach { shelter ->
                if (shelter.name.isNotBlank()) { // Valida antes de agregar
                    addAll(_uiState.value.shelters.map {
                        SearchResult(
                            id = it.id, type = "Refugio", name = it.name, address = it.address,
                            icon = Lucide.House, iconColor = Color(0xFF0EA5E9)
                        )
                    })
                }
            }

            _uiState.value.riskZones.forEach { riskZone ->
                if (riskZone.id.isNotBlank()) { // Valida antes de agregar
                    addAll(_uiState.value.riskZones.map {
                        SearchResult(
                            id = it.id, type = "Zona de Riesgo", name = it.state.name,
                            address = "Área con nivel ${it.state}", icon = Lucide.TriangleAlert,
                            iconColor = if (it.state == BannerState.Danger) Color(0xFFEF4444) else Color(0xFFF59E0B)
                        )
                    })
                }
            }


        }

        // Aplicamos el filtro si la query no está vacía
        val filteredList = if (query.isBlank()) {
            allItems // Si no hay búsqueda, muestra todo
        } else {
            allItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true)
            }
        }
        // Actualizamos el estado con la lista filtrada
        _uiState.update { it.copy(searchResults = filteredList) }
    }
}
