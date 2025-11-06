package com.example.practicadesign.data

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Cliente HTTP singleton de Ktor configurado para la aplicación.
 * 
 * Este objeto proporciona un cliente HTTP único y compartido con:
 * - Configuración de deserialización JSON automática
 * - Logging de peticiones HTTP para depuración
 * - Manejo de claves desconocidas en respuestas JSON
 * 
 * Utiliza el motor Android de Ktor para realizar las peticiones HTTP.
 */
object KtorClient {

    val httpClient = HttpClient(Android) {
        // Plugin para deserializar automáticamente JSON
        install(ContentNegotiation) {
            json(Json {
                // Ignora claves desconocidas en la respuesta JSON para evitar crashes
                ignoreUnknownKeys = true
                // Permite que campos con valores por defecto no necesiten estar en el JSON
                isLenient = true
                coerceInputValues = true // ⬅️ Agrega esto
                encodeDefaults = true     // ⬅️ Y esto también
            })
        }

        // Plugin para ver los logs de las peticiones en el Logcat (muy útil para depurar)
        install(Logging) {
            level = LogLevel.ALL
        }
    }
}
