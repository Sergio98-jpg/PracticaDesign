package com.example.practicadesign.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones con calles inundadas en cache.
 * 
 * Define todas las operaciones de base de datos relacionadas con las calles inundadas:
 * - Insertar/actualizar calles inundadas
 * - Obtener todas las calles inundadas
 * - Eliminar calles inundadas antiguas
 * - Observar cambios en las calles inundadas
 */
@Dao
interface FloodedStreetDao {
    
    /**
     * Inserta o actualiza una lista de calles inundadas.
     * 
     * Si una calle ya existe (mismo id), la actualiza.
     * Si no existe, la inserta como nueva.
     * 
     * @param floodedStreets Lista de calles inundadas a insertar o actualizar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFloodedStreets(floodedStreets: List<FloodedStreetEntity>)
    
    /**
     * Obtiene todas las calles inundadas como Flow.
     * 
     * Retorna un Flow que emite la lista de calles inundadas y se actualiza
     * automáticamente cuando hay cambios en la base de datos.
     * 
     * @return Flow que emite la lista de calles inundadas
     */
    @Query("SELECT * FROM flooded_streets ORDER BY lastUpdated DESC")
    fun getAllFloodedStreets(): Flow<List<FloodedStreetEntity>>
    
    /**
     * Obtiene todas las calles inundadas de forma síncrona.
     * 
     * @return Lista de todas las calles inundadas
     */
    @Query("SELECT * FROM flooded_streets ORDER BY lastUpdated DESC")
    suspend fun getAllFloodedStreetsSync(): List<FloodedStreetEntity>
    
    /**
     * Obtiene una calle inundada por su ID.
     * 
     * @param id ID de la calle inundada
     * @return La calle inundada con el ID especificado, o null si no existe
     */
    @Query("SELECT * FROM flooded_streets WHERE id = :id")
    suspend fun getFloodedStreetById(id: String): FloodedStreetEntity?
    
    /**
     * Elimina todas las calles inundadas.
     */
    @Query("DELETE FROM flooded_streets")
    suspend fun deleteAllFloodedStreets()
    
    /**
     * Elimina calles inundadas que no han sido actualizadas en un período determinado.
     * 
     * Útil para limpiar cache antiguo.
     * 
     * @param maxAgeMillis Edad máxima en milisegundos (ej: 1 hora = 3600000)
     */
    @Query("DELETE FROM flooded_streets WHERE lastUpdated < :maxAgeMillis")
    suspend fun deleteOldFloodedStreets(maxAgeMillis: Long)
}


