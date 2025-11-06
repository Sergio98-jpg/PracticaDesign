package com.example.practicadesign.data

import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Listener para actualizaciones en tiempo real de zonas de riesgo vía WebSocket.
 * 
 * Esta clase gestiona la conexión WebSocket con el servidor para recibir
 * actualizaciones en tiempo real de las zonas de riesgo.
 * 
 * Nota: El WebSocket aún no está completamente funcional y requiere:
 * - Configurar la URL del servidor (host, port, path)
 * - Asegurar que el backend soporte WebSocket en el endpoint especificado
 * 
 * @property client Cliente HTTP de Ktor configurado con soporte WebSocket
 */
class RiskZoneWebSocketListener {

    /**
     * Cliente HTTP de Ktor configurado con soporte WebSocket.
     */
    private val client = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Escucha actualizaciones en tiempo real de zonas de riesgo desde el servidor.
     * 
     * Esta función establece una conexión WebSocket y emite actualizaciones
     * de zonas de riesgo cuando se reciben del servidor.
     * 
     * Nota: La URL del servidor debe ser configurada antes de usar esta función.
     * Actualmente está como placeholder y requiere configuración.
     * 
     * @return Flow que emite objetos RiskZone cuando se reciben actualizaciones
     */
    fun listenForRiskZoneUpdates(): Flow<RiskZone> = flow {
        // TODO: Configurar la URL del servidor WebSocket
        // Por ahora, esta función no está completamente funcional
        client.webSocket(
            host = "tu-servidor.com", // ⚠️ CAMBIAR: Configurar la URL del servidor
            port = 8080,               // ⚠️ CAMBIAR: Configurar el puerto
            path = "/ws/risk-zones"    // ⚠️ CAMBIAR: Configurar la ruta del endpoint
        ) {
            // Convierte los mensajes entrantes en un Flow
            incoming.consumeAsFlow()
                .mapNotNull { frame ->
                    // Procesa cada mensaje que llega
                    if (frame is Frame.Text) {
                        try {
                            // Intenta decodificar el texto del mensaje a un objeto RiskZone
                            Json.decodeFromString<RiskZone>(frame.readText())
                        } catch (e: Exception) {
                            // Si el JSON es inválido, se ignora el mensaje
                            null
                        }
                    } else {
                        null
                    }
                }
                .collect { riskZone ->
                    // Emite cada objeto RiskZone válido en el Flow principal
                    emit(riskZone)
                }
        }
    }
        .catch { e ->
            // Captura errores de conexión o durante la sesión
            // El retry se encargará de reintentar si es necesario
        }
        .retry(retries = 3) { cause ->
            // Política de reintento: solo reintenta si el error es de red
            if (cause is IOException) {
                delay(5000) // Espera 5 segundos antes del siguiente intento
                true
            } else {
                // Para otros tipos de errores, no reintentamos
                false
            }
        }

    /**
     * Cierra la conexión WebSocket y libera los recursos del cliente.
     * 
     * Debe ser llamado cuando ya no se necesite la conexión para evitar fugas de memoria.
     */
    fun close() {
        client.close()
    }
}
