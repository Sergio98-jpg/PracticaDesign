package com.example.practicadesign.data

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException

/**
 * Repositorio encargado de gestionar los datos relacionados con los reportes.
 *
 * Esta clase actúa como una única fuente de verdad (Single Source of Truth) para los datos de reportes,
 * abstrayendo los orígenes de datos (API REST, datos simulados) del ViewModel.
 *
 * Proporciona métodos para:
 * - Enviar reportes (`submitReport`) a través de una llamada a una API REST con Ktor.
 * - Enviar reportes simulados (`submitMockReport`) para desarrollo y testing.
 *
 * @property client Cliente HTTP de Ktor para realizar peticiones REST
 */
class ReportRepository() {

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
     * Envía un reporte al backend.
     * 
     * Esta función realiza una petición HTTP POST al endpoint `/reportes` del backend,
     * envía el DTO del reporte y devuelve la respuesta como un Flow.
     * 
     * Maneja diferentes tipos de errores:
     * - Errores 4xx (petición incorrecta)
     * - Errores 5xx (error del servidor)
     * - Errores de red (conexión, timeout, etc.)
     * - Errores de deserialización
     * 
     * @param report El reporte a enviar
     * @return Flow que emite la respuesta del servidor o lanza una excepción en caso de error
     */
    fun submitReport(report: Report): Flow<ReportApiResponse> = flow {
        try {
            // 1. Convertir el modelo de dominio a DTO
            val reportDto = report.toDto()
            
            // 2. Enviar la petición POST al backend
            val response = client.post("$BASE_URL/reportes") {
                contentType(ContentType.Application.Json)
                setBody(reportDto)
            }.body<ReportApiResponse>()
            
            // 3. Verificar que la respuesta sea exitosa
            if (!response.success) {
                throw IOException("Error de API: ${response.message}")
            }
            
            // 4. Emitir la respuesta
            emit(response)
        } catch (e: ClientRequestException) {
            // Error 4xx: La petición es incorrecta (ej: datos inválidos, endpoint no encontrado)
            throw IOException(
                "No se pudo enviar el reporte. Verifique los datos e intente nuevamente.",
                e
            )
        } catch (e: ServerResponseException) {
            // Error 5xx: El servidor backend falló
            throw IOException(
                "El servidor no pudo procesar el reporte. Intente más tarde.",
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
            throw IOException("Ocurrió un error inesperado al enviar el reporte: ${e.message}", e)
        }
    }

    // ==================== DATOS SIMULADOS (MOCK) ====================
    // Estos métodos devuelven datos simulados. Se utilizan cuando no hay conexión
    // con el backend o para desarrollo y testing.

    /**
     * Envía un reporte simulado (mock) sin realmente conectarse al backend.
     * 
     * Nota: Este método simula el envío de un reporte. En una implementación futura,
     * se reemplazaría por una llamada real al endpoint `/reportes` del backend.
     * 
     * @param report El reporte a simular
     * @return Flow que emite una respuesta simulada exitosa
     */
    fun submitMockReport(report: Report): Flow<ReportApiResponse> = flow {
        // Simula una demora de red
        kotlinx.coroutines.delay(1000)
        
        // Simula una respuesta exitosa del servidor
        emit(
            ReportApiResponse(
                success = true,
                message = "Reporte enviado exitosamente",
                data = ReportResponseDto(
                    idReporte = (1000..9999).random(),
                    createdAt = java.time.LocalDateTime.now().toString()
                )
            )
        )
    }
}

