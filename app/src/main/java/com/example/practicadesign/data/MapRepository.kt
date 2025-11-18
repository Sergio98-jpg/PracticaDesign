package com.example.practicadesign.data

import android.content.Context
import android.util.Log
import com.example.practicadesign.data.local.AppDatabase
import com.example.practicadesign.data.local.CacheMappers.toDomain
import com.example.practicadesign.data.local.CacheMappers.toEntity
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Repositorio encargado de gestionar los datos relacionados con el mapa.
 *
 * Esta clase actúa como una única fuente de verdad (Single Source of Truth) para los datos del mapa,
 * abstrayendo los orígenes de datos (API REST, WebSockets, cache local, datos simulados) del ViewModel.
 *
 * Implementa una estrategia cache-first:
 * 1. Primero muestra datos del cache local (si existen)
 * 2. Luego intenta actualizar desde el backend
 * 3. Si el backend falla, mantiene los datos del cache
 *
 * Proporciona métodos para obtener:
 * - Refugios (`getShelters`) con cache-first strategy
 * - Zonas de riesgo (`getRiskZones`) con cache-first strategy
 * - Calles inundadas (`getMockFloodedStreets`) a partir de datos simulados
 * - Actualizaciones en tiempo real de las zonas de riesgo (`getRiskZoneUpdates`) a través de un WebSocket
 *
 * @property context Contexto de la aplicación para acceder a la base de datos
 * @property webSocketListener Listener para actualizaciones en tiempo real vía WebSocket
 * @property client Cliente HTTP de Ktor para realizar peticiones REST
 * @property database Instancia de la base de datos Room para cache local
 */
class MapRepository(private val context: Context? = null) {

    private val webSocketListener = RiskZoneWebSocketListener()
    private val client = KtorClient.httpClient
    
    // Base de datos Room para cache local (solo si se proporciona contexto)
    private val database: AppDatabase? = context?.let { AppDatabase.getDatabase(it) }
    private val shelterDao = database?.shelterDao()
    private val riskZoneDao = database?.riskZoneDao()
    private val floodedStreetDao = database?.floodedStreetDao()
    
    companion object {
        // Tiempo máximo de validez del cache (24 horas)
        private const val CACHE_MAX_AGE_MS = 24 * 60 * 60 * 1000L // 24 horas
        
        /**
         * URL base del backend.
         *
         * Importante:
         * - Para emulador de Android, "localhost" del PC se mapea a 10.0.2.2.
         * - Tus rutas en Laravel están bajo el prefijo /api (ej. /api/refugios).
         *
         * Por eso dejamos por defecto:
         *   http://10.0.2.2/api
         *
         * Si ejecutas en un dispositivo físico, deberás cambiarla a:
         *   http://TU_IP_LOCAL/api
         */
        private const val BASE_URL = "http://192.168.0.26/api"
    }

    /**
     * Obtiene la lista de refugios usando estrategia cache-first.
     * 
     * Estrategia:
     * 1. Primero emite los datos del cache local (si existen y son válidos)
     * 2. Luego intenta obtener datos frescos del backend
     * 3. Si el backend es exitoso, actualiza el cache y emite los nuevos datos
     * 4. Si el backend falla, mantiene los datos del cache (si existen)
     * 
     * Maneja diferentes tipos de errores:
     * - Errores 4xx (petición incorrecta)
     * - Errores 5xx (error del servidor)
     * - Errores de red (conexión, timeout, etc.)
     * - Errores de deserialización
     * 
     * @return Flow que emite la lista de refugios (primero del cache, luego del backend si es exitoso)
     */
    fun getShelters(): Flow<List<Shelter>> = flow {
        // 1. Intentar obtener datos del cache local primero
        val cachedShelters: List<Shelter> = try {
            shelterDao?.getAllSheltersSync()?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.w("MapRepository", "Error al leer cache de refugios", e)
            emptyList()
        }
        
        // 2. Si hay datos en cache, emitirlos inmediatamente
        if (cachedShelters.isNotEmpty()) {
            emit(cachedShelters)
        }
        
        // 3. Intentar obtener datos frescos del backend
        try {
            val response = client.get("$BASE_URL/refugios").body<SheltersApiResponse>()
            
            if (!response.success) {
                throw IOException("Error de API: ${response.message}")
            }

            // Mapear los DTOs a objetos de dominio
            val freshShelters = response.data.map { it.toDomain() }
            
            // 4. Guardar en cache
            try {
                val entities = freshShelters.map { it.toEntity() }
                shelterDao?.insertOrUpdateShelters(entities)
                Log.d("MapRepository", "Cache de refugios actualizado: ${freshShelters.size} refugios")
            } catch (e: Exception) {
                Log.w("MapRepository", "Error al guardar refugios en cache", e)
                // Continuamos aunque falle el guardado en cache
            }
            
            // 5. Emitir los datos frescos (solo si son diferentes del cache)
            if (freshShelters != cachedShelters) {
                emit(freshShelters)
            }
            
        } catch (e: ClientRequestException) {
            // Error 4xx: petición inválida o recurso no encontrado
            Log.w("MapRepository", "Error 4xx al obtener refugios. Cache disponible: ${cachedShelters.isNotEmpty()}", e)
            // Siempre propagamos el error para que el ViewModel se entere
            // No emitimos nada aquí, el catch del ViewModel manejará la emisión
            throw e
        } catch (e: ServerResponseException) {
            // Error 5xx: problema en el servidor
            Log.w("MapRepository", "Error 5xx al obtener refugios. Cache disponible: ${cachedShelters.isNotEmpty()}", e)
            throw e
        } catch (e: IOException) {
            // Error de red (timeout, sin conexión, etc.)
            Log.w("MapRepository", "Error de red al obtener refugios. Cache disponible: ${cachedShelters.isNotEmpty()}", e)
            // Propagamos el error directamente sin emitir nada
            // El catch del ViewModel emitirá una lista vacía si es necesario
            throw e
        } catch (e: Exception) {
            // Captura cualquier otro error inesperado
            if (e is kotlinx.coroutines.CancellationException) {
                throw e
            }
            Log.w("MapRepository", "Error inesperado al obtener refugios. Cache disponible: ${cachedShelters.isNotEmpty()}", e)
            throw e
        }
    }

    /**
     * Obtiene la lista de zonas de riesgo usando estrategia cache-first.
     * 
     * Estrategia:
     * 1. Primero emite los datos del cache local (si existen y son válidos)
     * 2. Luego intenta obtener datos frescos del backend
     * 3. Si el backend es exitoso, actualiza el cache y emite los nuevos datos
     * 4. Si el backend falla, mantiene los datos del cache (si existen)
     * 
     * Maneja diferentes tipos de errores:
     * - Errores 4xx (petición incorrecta)
     * - Errores 5xx (error del servidor)
     * - Errores de red (conexión, timeout, etc.)
     * - Errores de deserialización
     * 
     * @return Flow que emite la lista de zonas de riesgo (primero del cache, luego del backend si es exitoso)
     */
    fun getRiskZones(): Flow<List<RiskZone>> = flow {
        // 1. Intentar obtener datos del cache local primero
        val cachedZones: List<RiskZone> = try {
            riskZoneDao?.getAllRiskZonesSync()?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.w("MapRepository", "Error al leer cache de zonas de riesgo", e)
            emptyList()
        }
        
        // 2. Si hay datos en cache, emitirlos inmediatamente
        if (cachedZones.isNotEmpty()) {
            emit(cachedZones)
        }
        
        // 3. Intentar obtener datos frescos del backend
        try {
            val response = client.get("$BASE_URL/zonas-riesgo").body<RiskZoneApiResponse>()
            
            if (!response.success) {
                throw IOException("Error de API: ${response.message}")
            }

            // Mapear los DTOs a objetos de dominio
            val freshZones = response.data.map { it.toDomain() }
            
            // 4. Guardar en cache
            try {
                val entities = freshZones.map { zone -> zone.toEntity() }
                riskZoneDao?.insertOrUpdateRiskZones(entities)
                Log.d("MapRepository", "Cache de zonas de riesgo actualizado: ${freshZones.size} zonas")
            } catch (e: Exception) {
                Log.w("MapRepository", "Error al guardar zonas de riesgo en cache", e)
                // Continuamos aunque falle el guardado en cache
            }
            
            // 5. Emitir los datos frescos (solo si son diferentes del cache)
            if (freshZones != cachedZones) {
                emit(freshZones)
            }
            
        } catch (e: ClientRequestException) {
            Log.w("MapRepository", "Error 4xx al obtener zonas de riesgo. Cache disponible: ${cachedZones.isNotEmpty()}", e)
            // Siempre propagamos el error para que el ViewModel se entere
            // No emitimos nada aquí, el catch del ViewModel manejará la emisión
            throw e
        } catch (e: ServerResponseException) {
            Log.w("MapRepository", "Error 5xx al obtener zonas de riesgo. Cache disponible: ${cachedZones.isNotEmpty()}", e)
            throw e
        } catch (e: IOException) {
            Log.w("MapRepository", "Error de red al obtener zonas de riesgo. Cache disponible: ${cachedZones.isNotEmpty()}", e)
            // Propagamos el error directamente sin emitir nada
            // El catch del ViewModel emitirá una lista vacía si es necesario
            throw e
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) {
                throw e
            }
            Log.w("MapRepository", "Error inesperado al obtener zonas de riesgo. Cache disponible: ${cachedZones.isNotEmpty()}", e)
            throw e
        }
    }
    // ==================== DATOS SIMULADOS (MOCK) ====================
    // Estos métodos devuelven datos simulados para uso cuando no hay conexión al backend.
    // Los datos mock se guardan en cache para permitir funcionamiento offline.

    /**
     * Obtiene refugios mock para uso cuando no hay conexión al backend.
     * 
     * @return Lista de refugios mock
     */
    private fun getMockShelters(): List<Shelter> {
        // Datos mock básicos para desarrollo/testing
        return listOf(
            Shelter(
                id = "mock_1",
                name = "Refugio Municipal Centro",
                isOpen = true,
                address = "Calle Principal 123, Centro",
                capacity = 200,
                currentOccupancy = 45,
                latitude = 19.4326,
                longitude = -99.1332,
                phoneContact = "555-1234",
                responsible = "Juan Pérez"
            ),
            Shelter(
                id = "mock_2",
                name = "Albergue San José",
                isOpen = true,
                address = "Av. Independencia 456",
                capacity = 150,
                currentOccupancy = 120,
                latitude = 19.4350,
                longitude = -99.1400,
                phoneContact = "555-5678",
                responsible = "María González"
            ),
            Shelter(
                id = "mock_3",
                name = "Centro de Acopio Norte",
                isOpen = false,
                address = "Boulevard Norte 789",
                capacity = 100,
                currentOccupancy = 0,
                latitude = 19.4400,
                longitude = -99.1500,
                phoneContact = "555-9012",
                responsible = "Carlos Ramírez"
            )
        )
    }
    
    /**
     * Obtiene zonas de riesgo mock para uso cuando no hay conexión al backend.
     * 
     * @return Lista de zonas de riesgo mock
     */
    private fun getMockRiskZones(): List<RiskZone> {
        // Datos mock básicos para desarrollo/testing
        return listOf(
            RiskZone(
                id = "mock_zone_1",
                name = "Zona Centro",
                riskLevel = "ALTO",
                area = listOf(
                    SerializableLatLng(19.4300, -99.1300),
                    SerializableLatLng(19.4350, -99.1300),
                    SerializableLatLng(19.4350, -99.1400),
                    SerializableLatLng(19.4300, -99.1400)
                )
            ),
            RiskZone(
                id = "mock_zone_2",
                name = "Zona Norte",
                riskLevel = "MEDIO",
                area = listOf(
                    SerializableLatLng(19.4400, -99.1500),
                    SerializableLatLng(19.4450, -99.1500),
                    SerializableLatLng(19.4450, -99.1600),
                    SerializableLatLng(19.4400, -99.1600)
                )
            )
        )
    }

    /**
     * Obtiene las calles inundadas simuladas.
     * 
     * Nota: Este método devuelve datos simulados. En una implementación futura,
     * se reemplazaría por una llamada real al endpoint `/flooded-streets` del backend.
     * 
     * @return Flow que emite la lista de calles inundadas simuladas
     */
    fun getMockFloodedStreets(): Flow<List<FloodedStreet>> = flow {
        delay(700) // Simula otra demora de red
        emit(MockData.floodedStreets)
    }

    // ==================== WEBSOCKET ====================

    /**
     * Obtiene actualizaciones en tiempo real de las zonas de riesgo a través de WebSocket.
     * 
     * Nota: El WebSocket aún no está completamente funcional y requiere configuración
     * de la URL del servidor en RiskZoneWebSocketListener.
     * 
     * @return Flow que emite actualizaciones de zonas de riesgo cuando se reciben
     */
    fun getRiskZoneUpdates(): Flow<RiskZone> {
        return webSocketListener.listenForRiskZoneUpdates()
    }

    /**
     * Cierra la conexión WebSocket y libera los recursos.
     * 
     * Debe ser llamado cuando el ViewModel se destruya para evitar fugas de memoria.
     */
    fun closeWebSocket() {
        webSocketListener.close()
    }
}

