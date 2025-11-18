package com.example.practicadesign.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Entidad Room que representa un refugio en cache local.
 * 
 * Permite acceso rápido a los datos de refugios sin necesidad de conexión a internet.
 * 
 * @property id ID único del refugio
 * @property name Nombre del refugio
 * @property address Dirección del refugio
 * @property latitude Latitud de la ubicación
 * @property longitude Longitud de la ubicación
 * @property capacity Capacidad total del refugio
 * @property currentOccupancy Ocupación actual
 * @property isOpen Indica si el refugio está abierto
 * @property phoneContact Número de teléfono de contacto
 * @property responsible Persona responsable del refugio
 * @property lastUpdated Timestamp de la última actualización desde el backend
 */
@Entity(tableName = "shelters")
@TypeConverters(Converters::class)
data class ShelterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    val currentOccupancy: Int,
    val isOpen: Boolean,
    val phoneContact: String? = null,
    val responsible: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)


