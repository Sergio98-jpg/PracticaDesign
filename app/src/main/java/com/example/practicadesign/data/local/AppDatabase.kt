package com.example.practicadesign.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Base de datos principal de la aplicación usando Room.
 * 
 * Esta es la única instancia de la base de datos en toda la aplicación (patrón Singleton).
 * 
 * Contiene todas las tablas (entities) y sus respectivos DAOs.
 * 
 * @property reportDraftDao DAO para operaciones con borradores de reportes
 * @property shelterDao DAO para operaciones con refugios en cache
 * @property riskZoneDao DAO para operaciones con zonas de riesgo en cache
 * @property floodedStreetDao DAO para operaciones con calles inundadas en cache
 */
@Database(
    entities = [
        ReportDraftEntity::class,
        ShelterEntity::class,
        RiskZoneEntity::class,
        FloodedStreetEntity::class
    ],
    version = 4,  // Incrementado para limpiar cache con coordenadas incorrectas
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * DAO para operaciones con borradores de reportes.
     */
    abstract fun reportDraftDao(): ReportDraftDao
    
    /**
     * DAO para operaciones con refugios en cache.
     */
    abstract fun shelterDao(): ShelterDao
    
    /**
     * DAO para operaciones con zonas de riesgo en cache.
     */
    abstract fun riskZoneDao(): RiskZoneDao
    
    /**
     * DAO para operaciones con calles inundadas en cache.
     */
    abstract fun floodedStreetDao(): FloodedStreetDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Obtiene la instancia única de la base de datos (Singleton).
         * 
         * Si la instancia no existe, la crea. Si ya existe, retorna la existente.
         * 
         * @param context Contexto de la aplicación
         * @return Instancia única de AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // En desarrollo: recrea la DB si cambia la versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

