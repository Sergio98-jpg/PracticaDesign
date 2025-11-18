package com.example.practicadesign.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Entidad Room que representa una zona de riesgo en cache local.
 * 
 * Permite acceso rápido a los datos de zonas de riesgo sin necesidad de conexión a internet.
 * 
 * @property id ID único de la zona de riesgo
 * @property name Nombre identificador de la zona
 * @property riskLevel Nivel de riesgo (ALTO, MEDIO, BAJO)
 * @property areaJson JSON serializado de la lista de coordenadas (List<SerializableLatLng>)
 * @property lastUpdated Timestamp de la última actualización desde el backend
 */
@Entity(tableName = "risk_zones")
@TypeConverters(Converters::class)
data class RiskZoneEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val riskLevel: String,
    val areaJson: String, // Serializado como JSON string
    val lastUpdated: Long = System.currentTimeMillis()
)


