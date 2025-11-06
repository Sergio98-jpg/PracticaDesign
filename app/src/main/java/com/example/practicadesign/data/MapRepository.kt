package com.example.practicadesign.data

import com.example.practicadesign.ui.mapa.componentes.BannerState
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException

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
 *
 * @property webSocketListener Listener para actualizaciones en tiempo real vía WebSocket
 * @property client Cliente HTTP de Ktor para realizar peticiones REST
 */
class MapRepository() {

    private val webSocketListener = RiskZoneWebSocketListener()
    private val client = KtorClient.httpClient

    /**
     * URL base del backend.
     * 
     * Nota: Esta URL debe cambiarse según el entorno:
     * - Emulador: http://10.0.2.2:8080/api
     * - Dispositivo físico: http://TU_IP_LOCAL:8080/api
     * - Producción: https://tudominio.com/api
     */
    companion object {
        private const val BASE_URL = "http://192.168.0.19:8080/api"
    }

    /**
     * Obtiene la lista de refugios desde el backend.
     * 
     * Esta función realiza una petición HTTP GET al endpoint `/refugios` del backend,
     * mapea los DTOs a objetos de dominio y los emite como un Flow.
     * 
     * Maneja diferentes tipos de errores:
     * - Errores 4xx (petición incorrecta)
     * - Errores 5xx (error del servidor)
     * - Errores de red (conexión, timeout, etc.)
     * - Errores de deserialización
     * 
     * @return Flow que emite la lista de refugios o lanza una excepción en caso de error
     */
    fun getShelters(): Flow<List<Shelter>> = flow {
        try {
           // System.out.println("Entre al try")
            // 1. Obtener la respuesta completa del backend
            val response = client.get("$BASE_URL/refugios").body<SheltersApiResponse>()
            System.out.println("Response:" + response)
            // 2. Verificar que la respuesta sea exitosa
            if (!response.success) {
                System.out.println("Entre al if del try")
                throw IOException("Error de API: ${response.message}")
            }

            // 3. Mapear los DTOs a objetos de dominio
            val shelters = response.data.map { it.toDomain() }
            System.out.println("Shelter:" + shelters)
            // 4. Emitir la lista mapeada
            emit(shelters)
            System.out.println("Acabo el try")
        } catch (e: ClientRequestException) {
            println("Entre al catch")
            // Error 4xx: La petición es incorrecta (ej: URL mal formada, endpoint no encontrado)
            throw IOException(
                "No se pudo encontrar el recurso solicitado. Verifique la URL.",
                e
            )
        } catch (e: ServerResponseException) {
            // Error 5xx: El servidor backend falló
            throw IOException(
                "El servidor no pudo procesar la solicitud. Intente más tarde.",
                e
            )
        } catch (e: IOException) {
            // Error de red general: No hay conexión, DNS no resuelve, timeout, etc.
            throw IOException(
                "Error de conexión. Verifique su red y la dirección del servidor.",
                e
            )
        } catch (e: Exception) {
            // Captura cualquier otro error inesperado (ej: deserialización, etc.)
            if (e is kotlinx.coroutines.CancellationException) {
                throw e
            }
            // Para cualquier OTRA excepción, la envolvemos en una IOException.
            throw IOException("Ocurrió un error inesperado al procesar los datos: ${e.message}", e)
        }
    }

    fun getRiskZones(): Flow<List<RiskZone>> = flow {
        try {
            println("Entre al try RiskZone")
            // 1. Obtener la respuesta completa del backend
            val response = client.get("$BASE_URL/zonas-riesgo").body<RiskZoneApiResponse>()
            System.out.println("Response RiskZone: " + response)
            // 2. Verificar que la respuesta sea exitosa
            if (!response.success) {
                System.out.println("Entre al if del try de RiskZone")
                throw IOException("Error de API: ${response.message}")
            }

            // 3. Mapear los DTOs a objetos de dominio
            val riskZone = response.data.map { it.toDomain() }
            System.out.println("RiskZone:" + riskZone)
            // 4. Emitir la lista mapeada
            emit(riskZone)
            System.out.println("Acabo el try de RiskZone")
        } catch (e: ClientRequestException) {
            println("Entre al catch")
            // Error 4xx: La petición es incorrecta (ej: URL mal formada, endpoint no encontrado)
            throw IOException(
                "No se pudo encontrar el recurso solicitado. Verifique la URL.",
                e
            )
        } catch (e: ServerResponseException) {
            // Error 5xx: El servidor backend falló
            throw IOException(
                "El servidor no pudo procesar la solicitud. Intente más tarde.",
                e
            )
        } catch (e: IOException) {
            // Error de red general: No hay conexión, DNS no resuelve, timeout, etc.
            throw IOException(
                "Error de conexión. Verifique su red y la dirección del servidor.",
                e
            )
        } catch (e: Exception) {
            // Captura cualquier otro error inesperado (ej: deserialización, etc.)
            if (e is kotlinx.coroutines.CancellationException) {
                throw e
            }
            // Para cualquier OTRA excepción, la envolvemos en una IOException.
            throw IOException("Ocurrió un error inesperado al procesar los datos: ${e.message}", e)
        }
    }
    // ==================== DATOS SIMULADOS (MOCK) ====================
    // Estos métodos devuelven datos simulados. En una implementación futura,
    // serían reemplazados por llamadas reales a un servicio de API.

    /**
     * Obtiene las zonas de riesgo simuladas.
     *
     * Nota: Este método devuelve datos simulados. En una implementación futura,
     * se reemplazaría por una llamada real al endpoint `/risk-zones` del backend.
     *
     * @return Flow que emite la lista de zonas de riesgo simuladas
     */

/*    fun getMockRiskZones(): Flow<List<RiskZone>> = flow {
        delay(500) // Simula una pequeña demora de red
        emit(MockData.riskZones)
    }*/

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

