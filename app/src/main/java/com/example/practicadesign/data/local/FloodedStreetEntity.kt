package com.example.practicadesign.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Entidad Room que representa una calle inundada en cache local.
 * 
 * Permite acceso rápido a los datos de calles inundadas sin necesidad de conexión a internet.
 * 
 * @property id ID único de la calle inundada
 * @property pathJson JSON serializado de la lista de coordenadas (List<SerializableLatLng>)
 * @property lastUpdated Timestamp de la última actualización desde el backend
 */
@Entity(tableName = "flooded_streets")
@TypeConverters(Converters::class)
data class FloodedStreetEntity(
    @PrimaryKey
    val id: String,
    val pathJson: String, // Serializado como JSON string
    val lastUpdated: Long = System.currentTimeMillis()
)


