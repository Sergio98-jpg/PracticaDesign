package com.example.practicadesign.data.local

import androidx.room.TypeConverter
import com.example.practicadesign.data.SerializableLatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

/**
 * Convertidores de tipos para Room Database.
 * 
 * Room no puede almacenar tipos complejos directamente, por lo que estos convertidores
 * transforman tipos complejos (List<String>, List<SerializableLatLng>) a tipos que Room puede almacenar (String).
 */
class Converters {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Convierte una lista de Strings a un String separado por comas.
     * 
     * @param value Lista de Strings
     * @return String con los valores separados por comas, o null si la lista está vacía
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(separator = ",") ?: ""
    }
    
    /**
     * Convierte un String separado por comas a una lista de Strings.
     * 
     * @param value String con valores separados por comas
     * @return Lista de Strings, o lista vacía si el String está vacío
     */
    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrBlank()) {
            emptyList()
        } else {
            value.split(",").filter { it.isNotBlank() }
        }
    }
    
    /**
     * Convierte una lista de SerializableLatLng a un String JSON.
     * 
     * @param value Lista de coordenadas
     * @return String JSON serializado, o string vacío si la lista está vacía
     */
    @TypeConverter
    fun fromLatLngList(value: List<SerializableLatLng>?): String {
        return if (value.isNullOrEmpty()) {
            "[]"
        } else {
            json.encodeToString(value)
        }
    }
    
    /**
     * Convierte un String JSON a una lista de SerializableLatLng.
     * 
     * @param value String JSON serializado
     * @return Lista de coordenadas, o lista vacía si el String está vacío o es inválido
     */
    @TypeConverter
    fun toLatLngList(value: String?): List<SerializableLatLng> {
        return if (value.isNullOrBlank() || value == "[]") {
            emptyList()
        } else {
            try {
                json.decodeFromString(value)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

