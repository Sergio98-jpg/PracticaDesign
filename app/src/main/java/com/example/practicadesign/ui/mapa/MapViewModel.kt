package com.example.practicadesign.ui.mapa

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.example.practicadesign.data.DirectionsRepository
import com.example.practicadesign.data.FloodedStreet
import com.example.practicadesign.data.MapRepository
import com.example.practicadesign.data.RiskZone
import com.example.practicadesign.data.Shelter
import com.example.practicadesign.data.toGoogleLatLng
import com.example.practicadesign.ui.mapa.componentes.BannerState
import com.example.practicadesign.ui.mapa.componentes.SearchResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil.containsLocation
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale


/**
 * Estado de la UI del mapa que contiene toda la información necesaria
 * para renderizar la pantalla del mapa y sus componentes.
 */
data class MapUiState(
    val isLoading: Boolean = true,
    val networkError: NetworkErrorState = NetworkErrorState.None,
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
    val searchResults: List<SearchResult> = emptyList(),
    val route: List<LatLng>? = null, // Ruta de navegación actual
    val isRouteLoading: Boolean = false // Indica si se está cargando una ruta
)

/**
 * Filtros para mostrar/ocultar diferentes elementos en el mapa.
 */
data class MapFilters(
    val showRiskZones: Boolean = true,
    val showShelters: Boolean = true,
    val showFloodedStreets: Boolean = true
)

/**
 * ViewModel para la pantalla del mapa.
 * 
 * Gestiona el estado del mapa, incluyendo:
 * - Ubicación del usuario
 * - Zonas de riesgo, refugios y calles inundadas
 * - Filtros de visualización
 * - Búsqueda de elementos en el mapa
 * 
 * Sigue el patrón MVVM separando la lógica de negocio de la UI.
 */
class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()
    // Pasa el contexto de la aplicación para habilitar el cache local
    private val mapRepository = MapRepository(getApplication())
    private val directionsRepository = DirectionsRepository(getApplication())
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplication())
    private val geocoder = Geocoder(getApplication(), Locale.getDefault())

    /**
     * Inicializa el ViewModel cargando los datos iniciales del mapa
     * y configurando los listeners de actualizaciones en tiempo real.
     */
    init {
        fetchInitialMapData()
        listenForRealTimeUpdates()
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Obtenemos la nueva LatLng
                val newLatLng = LatLng(location.latitude, location.longitude)

                // Verifica si el usuario está dentro de una zona de riesgo
                checkUserLocationAgainstZones(newLatLng)
                
                // Realiza la geocodificación inversa para obtener el nombre de la ubicación
                try {
                    val addresses = try {
                        // Usa la API deprecada pero compatible con todas las versiones
                        // Nota: En Android 13+ se recomienda usar la nueva API con callback,
                        // pero para mantener compatibilidad usamos la deprecada con supresión
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    } catch (e: IOException) {
                        null // Geocoder falló (sin internet, etc.)
                    }

                    // Procesa la dirección si está disponible
                    val fullLocationName = if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        val mainLocation = address.locality ?: address.subLocality
                        val adminArea = address.adminArea

                        when {
                            !mainLocation.isNullOrBlank() && !adminArea.isNullOrBlank() -> "$mainLocation, $adminArea"
                            !mainLocation.isNullOrBlank() -> mainLocation
                            !adminArea.isNullOrBlank() -> adminArea
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

    /**
     * Inicia las actualizaciones de ubicación del usuario.
     * Debe ser llamado desde la UI después de que se concedan los permisos de ubicación.
     */
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // Intervalo en milisegundos
        ).apply {
            setMinUpdateIntervalMillis(5000L) // Intervalo mínimo de actualización
            setWaitForAccurateLocation(false)
        }.build()

        if (ContextCompat.checkSelfPermission(
                getApplication(),
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

    /**
     * Limpia los recursos cuando el ViewModel es destruido.
     * Detiene las actualizaciones de ubicación y cierra el WebSocket.
     */
    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        mapRepository.closeWebSocket()
    }

    /**
     * Maneja el clic en el botón del menú lateral.
     * Alterna el estado de apertura/cierre del menú.
     */
    fun onMenuClicked() {
        _uiState.update { currentState ->
            currentState.copy(isMenuOpen = !currentState.isMenuOpen)
        }
    }

    /**
     * Carga los datos iniciales del mapa.
     * 
     * Carga en paralelo:
     * - Refugios desde el backend (conectado)
     * - Zonas de riesgo desde el backend (conectado)
     * - Calles inundadas (simuladas)
     * 
     * Maneja errores de red de forma robusta para evitar crashes.
     * Si alguna llamada falla, se usa una lista vacía como fallback.
     */
    private fun fetchInitialMapData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                var hadNetworkError = false
                var sheltersHadCache = false
                var zonesHadCache = false

                // Ejecuta todas las operaciones en paralelo con manejo individual de errores
                val sheltersDeferred = async {
                    var sheltersResult = emptyList<Shelter>()
                    var errorOccurred = false
                    var emissionCount = 0
                    
                    try {
                        // El Repository emite primero el cache (si existe), luego intenta obtener datos del backend
                        // Si el backend funciona, emite los datos frescos (segunda emisión)
                        // Si el backend falla, lanza una excepción pero ya emitió el cache
                        mapRepository.getShelters()
                            .catch { e ->
                                Log.e("MapViewModel", "Excepción al cargar refugios", e)
                                hadNetworkError = true
                                errorOccurred = true
                                // Si el Flow falla, no emitimos nada, el error ya fue propagado
                                // El último valor recopilado (si existe) es del cache
                            }
                            .collect { shelters ->
                                emissionCount++
                                sheltersResult = shelters
                                // Si recibimos más de un valor, significa que el backend funcionó
                                // (primer valor = cache, segundo valor = datos frescos del backend)
                            }
                    } catch (e: Exception) {
                        Log.e("MapViewModel", "Excepción al cargar refugios (catch externo)", e)
                        hadNetworkError = true
                        errorOccurred = true
                    }
                    
                    // SOLO si hubo error Y hay datos Y solo recibimos 1 emisión (cache), significa que estamos usando cache
                    // Si recibimos 2 emisiones, el backend funcionó (cache + datos frescos)
                    if (errorOccurred && sheltersResult.isNotEmpty() && emissionCount == 1) {
                        sheltersHadCache = true
                        Log.d("MapViewModel", "Refugios cargados desde cache (error de red detectado, 1 emisión)")
                    } else if (!errorOccurred && sheltersResult.isNotEmpty()) {
                        Log.d("MapViewModel", "Refugios cargados desde backend exitosamente ($emissionCount emisiones)")
                    }
                    
                    sheltersResult
                }
                
                val zonesDeferred = async {
                    var zonesResult = emptyList<RiskZone>()
                    var errorOccurred = false
                    var emissionCount = 0
                    
                    try {
                        // El Repository emite primero el cache (si existe), luego intenta obtener datos del backend
                        // Si el backend funciona, emite los datos frescos (segunda emisión)
                        // Si el backend falla, lanza una excepción pero ya emitió el cache
                        mapRepository.getRiskZones()
                            .catch { e ->
                                Log.e("MapViewModel", "Excepción al cargar zonas de riesgo", e)
                                hadNetworkError = true
                                errorOccurred = true
                                // Si el Flow falla, no emitimos nada, el error ya fue propagado
                                // El último valor recopilado (si existe) es del cache
                            }
                            .collect { zones ->
                                emissionCount++
                                zonesResult = zones
                                // Si recibimos más de un valor, significa que el backend funcionó
                                // (primer valor = cache, segundo valor = datos frescos del backend)
                            }
                    } catch (e: Exception) {
                        Log.e("MapViewModel", "Excepción al cargar zonas de riesgo (catch externo)", e)
                        hadNetworkError = true
                        errorOccurred = true
                    }
                    
                    // SOLO si hubo error Y hay datos Y solo recibimos 1 emisión (cache), significa que estamos usando cache
                    // Si recibimos 2 emisiones, el backend funcionó (cache + datos frescos)
                    if (errorOccurred && zonesResult.isNotEmpty() && emissionCount == 1) {
                        zonesHadCache = true
                        Log.d("MapViewModel", "Zonas de riesgo cargadas desde cache (error de red detectado, 1 emisión)")
                    } else if (!errorOccurred && zonesResult.isNotEmpty()) {
                        Log.d("MapViewModel", "Zonas de riesgo cargadas desde backend exitosamente ($emissionCount emisiones)")
                    }
                    
                    zonesResult
                }
                
                val streetsDeferred = async {
                    try {
                        mapRepository.getMockFloodedStreets().first()
                    } catch (e: Exception) {
                        Log.e("MapViewModel", "Excepción al cargar calles inundadas", e)
                        // Para calles inundadas mock no lo tratamos como error de red crítico
                        emptyList<FloodedStreet>()
                    }
                }

                // Espera a que todas terminen (cada una ya maneja sus errores internamente)
                val shelters = sheltersDeferred.await()
                val zones = zonesDeferred.await()
                val streets = streetsDeferred.await()

                // Determinar el estado de error de red
                // Si hubo error de red Y hay datos (cache), es UsingCache
                // Si hubo error de red Y NO hay datos, es NoConnection
                val networkErrorState = when {
                    !hadNetworkError -> NetworkErrorState.None
                    sheltersHadCache || zonesHadCache -> NetworkErrorState.UsingCache
                    else -> NetworkErrorState.NoConnection
                }

                // Actualiza todo de una vez
                _uiState.update {
                    it.copy(
                        shelters = shelters,
                        riskZones = zones,
                        floodedStreets = streets,
                        isLoading = false,
                        networkError = networkErrorState
                    )
                }
            } catch (e: Exception) {
                // Manejo de errores general: actualiza el estado con un mensaje de error
                // Este catch es una red de seguridad adicional por si algo inesperado ocurre
                Log.e("MapViewModel", "Error inesperado al cargar datos del mapa", e)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        networkError = NetworkErrorState.NoConnection
                    )
                }
            }
        }
    }

    /**
     * Maneja la selección de un refugio en el mapa.
     * Abre el bottom sheet con la información del refugio seleccionado.
     */
    fun onShelterSelected(shelter: Shelter) {
        _uiState.update { it.copy(selectedShelter = shelter, selectedRiskZone = null) }
    }

    /**
     * Maneja la selección de una zona de riesgo en el mapa.
     * Abre el bottom sheet con la información de la zona seleccionada.
     */
    fun onZoneRiskSelected(zone: RiskZone) {
        _uiState.update { it.copy(selectedRiskZone = zone, selectedShelter = null) }
    }

    /**
     * Maneja el cierre del bottom sheet.
     * Limpia las selecciones de refugio y zona de riesgo, y también la ruta.
     */
    fun onBottomSheetDismissed() {
        _uiState.update { 
            it.copy(
                selectedShelter = null, 
                selectedRiskZone = null,
                route = null // Limpiar la ruta al cerrar el bottom sheet
            ) 
        }
    }

    /**
     * Escucha actualizaciones en tiempo real de las zonas de riesgo a través de WebSocket.
     * Actualiza el estado cuando se recibe una nueva zona de riesgo.
     */
    private fun listenForRealTimeUpdates() {
        viewModelScope.launch {
            mapRepository.getRiskZoneUpdates().collect { newRiskZone ->
                _uiState.update { currentState ->
                    // Reemplaza la zona existente con el mismo ID o agrega una nueva
                    val updatedZones = currentState.riskZones.filterNot { it.id == newRiskZone.id } + newRiskZone
                    currentState.copy(riskZones = updatedZones)
                }
            }
        }
    }

    /**
     * Verifica si la ubicación del usuario está dentro de alguna zona de riesgo.
     * Actualiza el banner de estado según la zona de mayor riesgo encontrada.
     * 
     * Maneja errores de forma robusta para evitar crashes si hay problemas
     * con las coordenadas o las zonas de riesgo.
     * 
     * @param userLocation La ubicación actual del usuario
     */
    fun checkUserLocationAgainstZones(userLocation: LatLng) {
        viewModelScope.launch {
            try {
                val currentZones = uiState.value.riskZones
                if (currentZones.isEmpty()) return@launch // No hacer nada si las zonas no han cargado

                // Busca la zona de mayor riesgo que contenga la ubicación del usuario
                val highestRiskZone = currentZones
                    .filter { zone -> 
                        try {
                            // Verifica que la zona tenga al menos 3 puntos para formar un polígono válido
                            zone.area.size >= 3 && 
                            containsLocation(userLocation, zone.area.map { it.toGoogleLatLng() }, false)
                        } catch (e: Exception) {
                            // Si hay un error al verificar la ubicación (coordenadas inválidas, etc.), ignoramos esta zona
                            Log.w("MapViewModel", "Error al verificar ubicación en zona ${zone.id}", e)
                            false
                        }
                    }
                    // Compara usando la función de conversión a BannerState
                    .maxByOrNull { riskLevelToBannerState(it.riskLevel).ordinal }

                // Determina el nuevo estado. Si no se encontró ninguna zona, es 'Safe'
                val newBannerState = highestRiskZone?.let {
                    riskLevelToBannerState(it.riskLevel)
                } ?: BannerState.Safe

                // Actualiza el UiState solo si el estado ha cambiado, para evitar redibujos innecesarios
                if (newBannerState != uiState.value.bannerState) {
                    _uiState.update { it.copy(bannerState = newBannerState) }
                }
            } catch (e: Exception) {
                // Si hay un error inesperado, simplemente lo registramos y no actualizamos el estado
                // Esto evita crashes y permite que la app continúe funcionando
                Log.e("MapViewModel", "Error al verificar ubicación contra zonas de riesgo", e)
            }
        }
    }

    /**
     * Función de ayuda para convertir el String de nivel de riesgo a un BannerState.
     * Centraliza la lógica de "traducción".
     */
    private fun riskLevelToBannerState(riskLevel: String): BannerState {
        return when (riskLevel.uppercase()) {
            "ALTO" -> BannerState.Danger
            "MEDIO" -> BannerState.Warning
            "BAJO" -> BannerState.Safe // O puedes tener un BannerState.Info si quieres
            else -> BannerState.Safe
        }
    }
    // ==================== FILTROS ====================

    /**
     * Alterna la visibilidad de las zonas de riesgo en el mapa.
     */
    fun toggleRiskZonesVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showRiskZones = !it.filters.showRiskZones)
            it.copy(filters = updatedFilters)
        }
    }

    /**
     * Alterna la visibilidad de los refugios en el mapa.
     */
    fun toggleSheltersVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showShelters = !it.filters.showShelters)
            it.copy(filters = updatedFilters)
        }
    }

    /**
     * Alterna la visibilidad de las calles inundadas en el mapa.
     */
    fun toggleFloodedStreetsVisibility() {
        _uiState.update {
            val updatedFilters = it.filters.copy(showFloodedStreets = !it.filters.showFloodedStreets)
            it.copy(filters = updatedFilters)
        }
    }

    // ==================== BÚSQUEDA ====================

    /**
     * Maneja el cambio en el texto de búsqueda.
     * Actualiza el estado y filtra los resultados según la consulta.
     * 
     * @param newQuery El nuevo texto de búsqueda
     */
    fun onSearchQueryChange(newQuery: TextFieldValue) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        filterResults(newQuery.text)
    }

    /**
     * Activa el modo de búsqueda y muestra todos los resultados iniciales.
     */
    fun onSearchActive() {
        _uiState.update { it.copy(isSearching = true) }
        filterResults("") // Muestra todos los resultados iniciales
    }

    /**
     * Desactiva el modo de búsqueda y limpia el estado.
     */
    fun onSearchInactive() {
        _uiState.update { 
            it.copy(
                isSearching = false, 
                searchQuery = TextFieldValue(""), 
                searchResults = emptyList()
            ) 
        }
    }
    
    /**
     * Maneja la selección de un resultado de búsqueda.
     * Busca el elemento correspondiente (refugio o zona de riesgo) y lo selecciona
     * para mostrar en el bottom sheet.
     * 
     * @param searchResult El resultado de búsqueda seleccionado
     * @return La posición (LatLng) del elemento seleccionado para centrar la cámara,
     *         o null si no se encuentra el elemento
     */
    fun onSearchResultSelected(searchResult: SearchResult): LatLng? {
        val currentState = _uiState.value
        
        return when (searchResult.type) {
            "Refugio" -> {
                // Busca el refugio por ID
                val shelter = currentState.shelters.find { it.id == searchResult.id }
                if (shelter != null) {
                    // Selecciona el refugio para abrir el bottom sheet
                    onShelterSelected(shelter)
                    // Retorna la posición del refugio
                    shelter.position
                } else {
                    Log.w("MapViewModel", "Refugio no encontrado: ${searchResult.id}")
                    null
                }
            }
            "Zona de Riesgo" -> {
                // Busca la zona de riesgo por ID
                val riskZone = currentState.riskZones.find { it.id == searchResult.id }
                if (riskZone != null) {
                    // Selecciona la zona de riesgo para abrir el bottom sheet
                    onZoneRiskSelected(riskZone)
                    // Retorna el centro aproximado de la zona (primer punto del polígono)
                    // Idealmente, calcularíamos el centroide real del polígono
                    if (riskZone.area.isNotEmpty()) {
                        riskZone.area.first().toGoogleLatLng()
                    } else {
                        Log.w("MapViewModel", "Zona de riesgo sin área definida: ${searchResult.id}")
                        null
                    }
                } else {
                    Log.w("MapViewModel", "Zona de riesgo no encontrada: ${searchResult.id}")
                    null
                }
            }
            else -> {
                Log.w("MapViewModel", "Tipo de resultado desconocido: ${searchResult.type}")
                null
            }
        }
    }

    /**
     * Filtra los resultados de búsqueda según la consulta del usuario.
     * Busca en refugios y zonas de riesgo.
     * 
     * @param query El texto de búsqueda
     */
    private fun filterResults(query: String) {
        val allItems = buildList {
            // Agrega todos los refugios como resultados de búsqueda
            _uiState.value.shelters.forEach { shelter ->
                if (shelter.name.isNotBlank()) {
                    add(
                        SearchResult(
                            id = shelter.id,
                            type = "Refugio",
                            name = shelter.name,
                            address = shelter.address,
                            icon = Lucide.House,
                            iconColor = Color(0xFF0EA5E9)
                        )
                    )
                }
            }

// Agrega todas las zonas de riesgo como resultados de búsqueda
            _uiState.value.riskZones.forEach { riskZone ->
                // Usamos la función de ayuda para "traducir" el dato crudo a un estado de UI
                val bannerState = riskLevelToBannerState(riskZone.riskLevel)

                if (riskZone.id.isNotBlank()) {
                    add(
                        SearchResult(
                            id = riskZone.id,
                            type = "Zona de Riesgo",
                            // El nombre de la zona ahora viene de la propiedad 'name'
                            name = riskZone.name,
                            // La dirección describe el nivel de riesgo
                            address = "Área con nivel de riesgo ${riskZone.riskLevel.lowercase()}",
                            icon = Lucide.TriangleAlert,
                            // El color del icono depende del BannerState traducido
                            iconColor = if (bannerState == BannerState.Danger) {
                                Color(0xFFEF4444) // Rojo
                            } else {
                                Color(0xFFF59E0B) // Naranja/Amarillo
                            }
                        )
                    )
                }
            }
        }

        // Aplica el filtro si la query no está vacía
        val filteredList = if (query.isBlank()) {
            allItems // Si no hay búsqueda, muestra todo
        } else {
            allItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.address.contains(query, ignoreCase = true)
            }
        }

        // Actualiza el estado con la lista filtrada
        _uiState.update { it.copy(searchResults = filteredList) }
    }
    
    // ==================== NAVEGACIÓN ====================
    
    /**
     * Obtiene la ruta de navegación desde la ubicación del usuario hasta un destino.
     * 
     * @param destination Coordenadas del destino
     */
    fun getRouteToDestination(destination: LatLng) {
        val currentLocation = _uiState.value.userLocation
        if (currentLocation == null) {
            Log.w("MapViewModel", "No hay ubicación del usuario para calcular la ruta")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isRouteLoading = true, route = null) }
                
                val route = directionsRepository.getRoute(currentLocation, destination)
                
                if (route != null) {
                    _uiState.update { 
                        it.copy(
                            route = route,
                            isRouteLoading = false
                        ) 
                    }
                    Log.d("MapViewModel", "Ruta obtenida exitosamente: ${route.size} puntos")
                } else {
                    _uiState.update { it.copy(isRouteLoading = false) }
                    Log.w("MapViewModel", "No se pudo obtener la ruta")
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al obtener la ruta", e)
                _uiState.update { it.copy(isRouteLoading = false) }
            }
        }
    }
    
    /**
     * Limpia la ruta actual del mapa.
     */
    fun clearRoute() {
        _uiState.update { it.copy(route = null) }
    }
}
