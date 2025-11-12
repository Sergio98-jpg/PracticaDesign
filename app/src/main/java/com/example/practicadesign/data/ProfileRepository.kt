package com.example.practicadesign.data

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
 * Repositorio encargado de gestionar los datos relacionados con el perfil del usuario.
 *
 * Esta clase actúa como una única fuente de verdad (Single Source of Truth) para los datos del perfil,
 * abstrayendo los orígenes de datos (API REST, datos simulados) del ViewModel.
 *
 * Proporciona métodos para obtener:
 * - Perfil del usuario (`getProfile`) a través de una llamada a una API REST con Ktor.
 * - Datos simulados del perfil (`getMockProfile`) para desarrollo y testing.
 *
 * @property client Cliente HTTP de Ktor para realizar peticiones REST
 */
class ProfileRepository() {

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
     * Obtiene el perfil del usuario desde el backend.
     * 
     * Esta función realiza una petición HTTP GET al endpoint `/perfil` del backend,
     * mapea el DTO a un objeto de dominio y lo emite como un Flow.
     * 
     * Maneja diferentes tipos de errores:
     * - Errores 4xx (petición incorrecta)
     * - Errores 5xx (error del servidor)
     * - Errores de red (conexión, timeout, etc.)
     * - Errores de deserialización
     * 
     * @return Flow que emite el perfil del usuario o lanza una excepción en caso de error
     */
    fun getProfile(): Flow<Profile> = flow {
        try {
            // 1. Obtener la respuesta completa del backend
            val response = client.get("$BASE_URL/perfil").body<ProfileApiResponse>()
            
            // 2. Verificar que la respuesta sea exitosa
            if (!response.success) {
                throw IOException("Error de API: ${response.message}")
            }

            // 3. Mapear el DTO a un objeto de dominio
            val profile = response.data.toDomain()
            
            // 4. Emitir el perfil mapeado
            emit(profile)
        } catch (e: ClientRequestException) {
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
    // Estos métodos devuelven datos simulados. Se utilizan cuando no hay conexión
    // con el backend o para desarrollo y testing.

    /**
     * Obtiene datos simulados del perfil del usuario.
     * 
     * Nota: Este método devuelve datos simulados. En una implementación futura,
     * se reemplazaría por una llamada real al endpoint `/perfil` del backend.
     * 
     * @return Flow que emite el perfil simulado del usuario
     */
    fun getMockProfile(): Flow<Profile> = flow {
        delay(500) // Simula una pequeña demora de red
        emit(
            Profile(
                id = "1",
                name = "Carlos Morales",
                location = "Mérida, Yucatán",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAKUqdD0pUxYXneCnEV0RYVELaxv6w7xjMlVTJYjuilLnIEDLoJR8KarT4kUYzwBAjVwnrPvmZUJzUS7eG-9QaishPt0jHS-Xpci-4tvTZVsqba0E9foG1vPat-FZnhh9pDPW4zXDmIdYVT_eu7wghBjluqjJ44ZWMk2C1CbCUCDxXc1KWIKVXdkXMoNvvv7WuAn68v_wukqLWMlkuQ2Z_azIzBFYrKuVLs7Mc3jdp7Slgk_qVXE43IKgtSTCRkQ-lm5WQSRM0nEPk5",
                email = "carlos.morales@example.com",
                reportsSent = 12,
                alertsReceived = 5
            )
        )
    }
}

