package com.example.practicadesign.data

import com.example.practicadesign.ui.mapa.componentes.BannerState
//import com.example.practicadesign.ui.mapa.componentes.FloodedStreet
//import com.example.practicadesign.ui.mapa.componentes.RiskZone
import com.google.android.gms.maps.model.LatLng
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
/**
 * Repositorio encargado de gestionar los datos relacionados con el mapa.
 *
 * Esta clase actúa como una única fuente de verdad (Single Source of Truth) para los datos del mapa,
 * abstrayendo los orígenes de datos (API REST, WebSockets, datos simulados) del ViewModel.
 *
 * Proporciona métodos para obtener:
 * - Refugios (`getShelters`) a través de una llamada a una API REST con Ktor.
 * - Zonas de riesgo y calles inundadas (`getMockRiskZones`, `getMockFloodedStreets`) a partir de datos simulados.
 * - Actualizaciones en tiempo real de las zonas de riesgo (`getRiskZoneUpdates`) a través de un WebSocket.
 *
 * En una implementación futura, los métodos de datos simulados (`getMock...`) serían reemplazados por
 * llamadas reales a un servicio de API.
 */

// En una app real, aquí inyectarías tu ApiService de Retrofit
class MapRepository() {

    private val webSocketListener = RiskZoneWebSocketListener()
    private val client = KtorClient.httpClient
    // El ViewModel llamará a esta función para obtener TODOS los datos del mapa
/*    suspend fun getMapData(): MapDataResult {
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
                riskZones = getMockRiskZones(),
                shelters = getShelters(),
                floodedStreets = getMockFloodedStreets()
            )
        }
    }*/

    // ✅ CENTRALIZA LA URL BASE - Fácil de cambiar cuando deploys a producción
    companion object {
       // private const val BASE_URL = "http://10.0.2.2:8080/api"
        private const val BASE_URL = "http://192.168.0.21:8080/api"
     //   private const val BASE_URL = "http://10.154.219.198:8080/api"
        // Cuando uses un dispositivo físico, cambia a tu IP local:
        // private const val BASE_URL = "http://192.168.1.X:8080/api"
        // En producción:
        // private const val BASE_URL = "https://tudominio.com/api"
    }

    // ✅ REFUGIOS REALES DESDE EL BACKEND con mejor manejo de errores
    fun getShelters(): Flow<List<Shelter>> = flow {
        // 1. Obtener la respuesta completa del backend
        val response = client.get("$BASE_URL/refugios").body<SheltersApiResponse>()

        // 2. Mapear los DTOs a objetos de dominio
        val shelters = response.data.map { it.toDomain() }

        // 3. Emitir la lista mapeada
        emit(shelters)
/*        try {
            // 1. Intentamos obtener la respuesta completa del backend
            println("ℹ️ MapRepository: Solicitando refugios desde $BASE_URL/refugios...")
            val response = client.get("$BASE_URL/refugios").body<SheltersApiResponse>()
            println("✅ MapRepository: Respuesta recibida para refugios.")

            // 2. Mapeamos los DTOs a objetos de dominio
            println(response)
            if (response.success) {
                val shelters = response.data.map { it.toDomain() }
                // 3. Emitimos la lista mapeada
                emit(shelters)
                println("✅ MapRepository: ${shelters.size} refugios emitidos a la UI.")
            } else {
                // El backend respondió, pero con un error de lógica interna
                throw Exception("Error de API: ${response.message}")
            }

        } catch (e: ClientRequestException) {
            // Error 4xx: La petición es incorrecta (ej: URL mal formada, endpoint no encontrado).
            println("❌ MapRepository [ERROR 4xx]: Petición de cliente inválida: ${e.response.status}")
            // Relanzamos la excepción para que el .catch() del ViewModel la reciba.
            throw IOException("No se pudo encontrar el recurso solicitado. Verifique la URL.", e)

        } catch (e: ServerResponseException) {
            // Error 5xx: El servidor backend falló.
            println("❌ MapRepository [ERROR 5xx]: Error en el servidor: ${e.response.status}")
            throw IOException("El servidor no pudo procesar la solicitud. Intente más tarde.", e)

        } catch (e: IOException) {
            // Error de red general: No hay conexión, DNS no resuelve, timeout, etc.
            // ESTE ES EL ERROR QUE PROBABLEMENTE VERÁS CON TU IP INCORRECTA.
            println("❌ MapRepository [ERROR de Red]: No se pudo conectar al servidor: ${e.message}")
            throw IOException("Error de conexión. Verifique su red y la dirección del servidor.", e)

        } catch (e: Exception) {
            // Captura cualquier otro error inesperado (ej: deserialización, etc.).
            println("❌ MapRepository [ERROR Inesperado]: ${e.javaClass.simpleName} - ${e.message}")
            throw IOException("Ocurrió un error inesperado al procesar los datos.", e)
        }*/
    }

    // --- DATOS SIMULADOS (MOCK) ---
    // ✅ ¡NUEVA FUNCIÓN! Devuelve las zonas de riesgo simuladas como un Flow.
    // ✅ La función ahora es mucho más limpia.
    fun getMockRiskZones(): Flow<List<RiskZone>> = flow {
        delay(500) // Simula una pequeña demora de red
        // Simplemente emite la lista desde el objeto MockData.
        emit(MockData.riskZones)
    }

    // ✅ Igual de limpia que la anterior.
    fun getMockFloodedStreets(): Flow<List<FloodedStreet>> = flow {
        delay(700) // Simula otra demora
        // Emite la lista desde el objeto MockData.
        emit(MockData.floodedStreets)
    }


    // ✅ NUEVA FUNCIÓN: Devuelve un Flow que emite actualizaciones de RiskZone
    fun getRiskZoneUpdates(): Flow<RiskZone> {
        return webSocketListener.listenForRiskZoneUpdates()
    }

    // Función para limpiar cuando el ViewModel se destruya
    fun closeWebSocket() {
        webSocketListener.close()
    }
}

