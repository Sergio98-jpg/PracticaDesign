package com.example.practicadesign.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones con zonas de riesgo en cache.
 * 
 * Define todas las operaciones de base de datos relacionadas con las zonas de riesgo:
 * - Insertar/actualizar zonas de riesgo
 * - Obtener todas las zonas de riesgo
 * - Eliminar zonas de riesgo antiguas
 * - Observar cambios en las zonas de riesgo
 */
@Dao
interface RiskZoneDao {
    
    /**
     * Inserta o actualiza una lista de zonas de riesgo.
     * 
     * Si una zona ya existe (mismo id), la actualiza.
     * Si no existe, la inserta como nueva.
     * 
     * @param riskZones Lista de zonas de riesgo a insertar o actualizar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRiskZones(riskZones: List<RiskZoneEntity>)
    
    /**
     * Obtiene todas las zonas de riesgo como Flow.
     * 
     * Retorna un Flow que emite la lista de zonas de riesgo y se actualiza
     * automáticamente cuando hay cambios en la base de datos.
     * 
     * @return Flow que emite la lista de zonas de riesgo
     */
    @Query("SELECT * FROM risk_zones ORDER BY name ASC")
    fun getAllRiskZones(): Flow<List<RiskZoneEntity>>
    
    /**
     * Obtiene todas las zonas de riesgo de forma síncrona.
     * 
     * @return Lista de todas las zonas de riesgo
     */
    @Query("SELECT * FROM risk_zones ORDER BY name ASC")
    suspend fun getAllRiskZonesSync(): List<RiskZoneEntity>
    
    /**
     * Obtiene una zona de riesgo por su ID.
     * 
     * @param id ID de la zona de riesgo
     * @return La zona de riesgo con el ID especificado, o null si no existe
     */
    @Query("SELECT * FROM risk_zones WHERE id = :id")
    suspend fun getRiskZoneById(id: String): RiskZoneEntity?
    
    /**
     * Elimina todas las zonas de riesgo.
     */
    @Query("DELETE FROM risk_zones")
    suspend fun deleteAllRiskZones()
    
    /**
     * Elimina zonas de riesgo que no han sido actualizadas en un período determinado.
     * 
     * Útil para limpiar cache antiguo.
     * 
     * @param maxAgeMillis Edad máxima en milisegundos (ej: 24 horas = 86400000)
     */
    @Query("DELETE FROM risk_zones WHERE lastUpdated < :maxAgeMillis")
    suspend fun deleteOldRiskZones(maxAgeMillis: Long)
}


