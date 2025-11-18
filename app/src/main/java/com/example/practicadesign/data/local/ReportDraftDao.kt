package com.example.practicadesign.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones con borradores de reportes.
 * 
 * Define todas las operaciones de base de datos relacionadas con los borradores:
 * - Insertar/actualizar un borrador
 * - Obtener el borrador actual
 * - Eliminar un borrador
 * - Observar cambios en el borrador
 */
@Dao
interface ReportDraftDao {
    
    /**
     * Inserta o actualiza un borrador de reporte.
     * 
     * Si el borrador ya existe (mismo id), lo actualiza.
     * Si no existe, lo inserta como nuevo.
     * 
     * @param draft Borrador a insertar o actualizar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: ReportDraftEntity)
    
    /**
     * Obtiene el borrador más reciente como Flow.
     * 
     * Retorna un Flow que emite el borrador más reciente y se actualiza
     * automáticamente cuando hay cambios en la base de datos.
     * 
     * @return Flow que emite el borrador más reciente, o null si no hay borrador
     */
    @Query("SELECT * FROM report_drafts ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestDraft(): Flow<ReportDraftEntity?>
    
    /**
     * Obtiene el borrador más reciente de forma síncrona (para casos especiales).
     * 
     * @return El borrador más reciente, o null si no hay borrador
     */
    @Query("SELECT * FROM report_drafts ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestDraftSync(): ReportDraftEntity?
    
    /**
     * Elimina un borrador por su ID.
     * 
     * @param id ID del borrador a eliminar
     */
    @Query("DELETE FROM report_drafts WHERE id = :id")
    suspend fun deleteDraft(id: Long)
    
    /**
     * Elimina todos los borradores.
     * 
     * Útil para limpiar después de enviar un reporte exitosamente.
     */
    @Query("DELETE FROM report_drafts")
    suspend fun deleteAllDrafts()
    
    /**
     * Elimina el borrador más reciente.
     * 
     * Útil para limpiar después de enviar un reporte exitosamente.
     */
    @Query("DELETE FROM report_drafts WHERE id = (SELECT id FROM report_drafts ORDER BY updatedAt DESC LIMIT 1)")
    suspend fun deleteLatestDraft()
}

