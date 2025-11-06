package com.example.practicadesign.data

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Usamos un 'object' para tener una única instancia (Singleton) del cliente.
object KtorClient {

    val httpClient = HttpClient(Android) {
        // Plugin para deserializar automáticamente JSON
        install(ContentNegotiation) {
            json(Json {
                // Ignora claves desconocidas en la respuesta JSON para evitar crashes
                ignoreUnknownKeys = true
                // Permite que campos con valores por defecto no necesiten estar en el JSON
                isLenient = true
            })
        }

        // Plugin para ver los logs de las peticiones en el Logcat (muy útil para depurar)
        install(Logging) {
            level = LogLevel.ALL
        }
    }
}
