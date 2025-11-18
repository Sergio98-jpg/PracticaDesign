package com.example.practicadesign.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones con refugios en cache.
 * 
 * Define todas las operaciones de base de datos relacionadas con los refugios:
 * - Insertar/actualizar refugios
 * - Obtener todos los refugios
 * - Eliminar refugios antiguos
 * - Observar cambios en los refugios
 */
@Dao
interface ShelterDao {
    
    /**
     * Inserta o actualiza una lista de refugios.
     * 
     * Si un refugio ya existe (mismo id), lo actualiza.
     * Si no existe, lo inserta como nuevo.
     * 
     * @param shelters Lista de refugios a insertar o actualizar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateShelters(shelters: List<ShelterEntity>)
    
    /**
     * Obtiene todos los refugios como Flow.
     * 
     * Retorna un Flow que emite la lista de refugios y se actualiza
     * automáticamente cuando hay cambios en la base de datos.
     * 
     * @return Flow que emite la lista de refugios
     */
    @Query("SELECT * FROM shelters ORDER BY name ASC")
    fun getAllShelters(): Flow<List<ShelterEntity>>
    
    /**
     * Obtiene todos los refugios de forma síncrona.
     * 
     * @return Lista de todos los refugios
     */
    @Query("SELECT * FROM shelters ORDER BY name ASC")
    suspend fun getAllSheltersSync(): List<ShelterEntity>
    
    /**
     * Obtiene un refugio por su ID.
     * 
     * @param id ID del refugio
     * @return El refugio con el ID especificado, o null si no existe
     */
    @Query("SELECT * FROM shelters WHERE id = :id")
    suspend fun getShelterById(id: String): ShelterEntity?
    
    /**
     * Elimina todos los refugios.
     */
    @Query("DELETE FROM shelters")
    suspend fun deleteAllShelters()
    
    /**
     * Elimina refugios que no han sido actualizados en un período determinado.
     * 
     * Útil para limpiar cache antiguo.
     * 
     * @param maxAgeMillis Edad máxima en milisegundos (ej: 24 horas = 86400000)
     */
    @Query("DELETE FROM shelters WHERE lastUpdated < :maxAgeMillis")
    suspend fun deleteOldShelters(maxAgeMillis: Long)
}


