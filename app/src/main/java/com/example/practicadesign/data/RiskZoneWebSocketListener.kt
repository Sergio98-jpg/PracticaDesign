package com.example.practicadesign.data

import com.example.practicadesign.ui.mapa.componentes.RiskZone
import io.ktor.client.*
//import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.delay // Para la espera antes de reintentar
import kotlinx.coroutines.flow.catch // Para capturar errores en el Flow
import kotlinx.coroutines.flow.retry // Para reintentar la conexión
class RiskZoneWebSocketListener {

    // 1. Configura el cliente de Ktor
    private val client = HttpClient(CIO) {
        install(WebSockets) // Habilita el plugin de WebSockets
        install(ContentNegotiation) { // Habilita la negociación de contenido (para JSON)
            json(Json {
                ignoreUnknownKeys = true // Ignora campos del JSON que no estén en tu data class
            })
        }
    }

    // 2. Crea una función que devuelve un Flow de RiskZones
    fun listenForRiskZoneUpdates(): Flow<RiskZone> {
        // ✅ Usa el constructor de Flow para envolver la lógica de suspensión
        return flow {
            // Ahora estamos dentro de un bloque de corrutina, por lo que podemos llamar a suspend functions
            println("WebSocket: Intentando conectar...") // Log para depuración
            client.webSocket( // Usamos client.webSocket que es más directo para sesiones continuas
                host = "tu-servidor.com", // <-- ⚠️ CAMBIA ESTO
                port = 8080,             // <-- ⚠️ CAMBIA ESTO
                path = "/ws/risk-zones"  // <-- ⚠️ CAMBIA ESTO
            ) { // El código dentro de este bloque se ejecuta mientras la sesión WebSocket está activa
                println("WebSocket: Conexión establecida.") // Log para depuración
                // Convierte los mensajes entrantes en un Flow
                incoming.consumeAsFlow()
                    .mapNotNull { frame ->
                        // Procesa cada mensaje que llega
                        if (frame is io.ktor.websocket.Frame.Text) {
                            try {
                                // Intenta decodificar el texto del mensaje a un objeto RiskZone
                                Json.decodeFromString<RiskZone>(frame.readText())
                            } catch (e: Exception) {
                                // Si el JSON es inválido o no es lo que esperas, lo ignoras
                                null
                            }
                        } else {
                            null
                        }
                    }
                    .collect { riskZone ->
                        // ✅ Emite cada objeto RiskZone válido en el Flow principal
                        emit(riskZone)
                    }
            }
        }
            .catch { e ->
                // ✅ 1. CAPTURAMOS CUALQUIER ERROR DE CONEXIÓN O DURANTE LA SESIÓN
                println("WebSocket: Error en la conexión: ${e.message}")
                // No hacemos nada más, 'retry' se encargará.
            }
            .retry(3) { cause ->
                // ✅ 2. POLÍTICA DE REINTENTO
                // Solo reintentamos si el error es de tipo IOException (problema de red).
                if (cause is java.io.IOException) {
                    println("WebSocket: Conexión perdida. Reintentando en 5 segundos...")
                    // Espera 5 segundos antes del siguiente intento.
                    delay(5000)
                    // Devuelve 'true' para indicar que queremos reintentar.
                    return@retry true
                } else {
                    // Para otros tipos de errores (ej. JSON malformado que no capturaste antes),
                    // no reintentamos. Devuelve 'false'.
                    return@retry false
                }
            }
    }

    // 4. Función para cerrar la conexión cuando ya no se necesite
    fun close() {
        client.close()
    }
}
