package com.example.practicadesign.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Entidad Room que representa un borrador de reporte guardado localmente.
 * 
 * Permite que el usuario pueda continuar un reporte aunque la app se cierre.
 * 
 * @property id ID único del borrador (auto-generado)
 * @property currentStep Paso actual del formulario (1-5)
 * @property selectedType Tipo de reporte seleccionado
 * @property selectedLocation Tipo de ubicación seleccionada
 * @property locationLat Latitud de la ubicación (null si no se ha seleccionado)
 * @property locationLng Longitud de la ubicación (null si no se ha seleccionado)
 * @property title Título del reporte
 * @property description Descripción del reporte
 * @property urgency Nivel de urgencia (high, medium, low)
 * @property photoUris Lista de URIs de las fotos (serializadas como String)
 * @property createdAt Timestamp de cuando se creó el borrador
 * @property updatedAt Timestamp de la última actualización
 */
@Entity(tableName = "report_drafts")
@TypeConverters(Converters::class)
data class ReportDraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val currentStep: Int = 1,
    val selectedType: String? = null,
    val selectedLocation: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val title: String = "",
    val description: String = "",
    val urgency: String = "medium",
    val photoUris: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

