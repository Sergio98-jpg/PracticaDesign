package com.example.practicadesign.data.local

import com.example.practicadesign.data.FloodedStreet
import com.example.practicadesign.data.RiskZone
import com.example.practicadesign.data.Shelter
import com.example.practicadesign.data.SerializableLatLng
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Funciones de mapeo entre entidades Room (cache) y modelos de dominio.
 * 
 * Permite convertir entre el formato de base de datos y el formato usado en la aplicación.
 */
object CacheMappers {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    // ==================== SHELTER ====================
    
    /**
     * Convierte un Shelter (dominio) a un ShelterEntity (Room).
     */
    fun Shelter.toEntity(): ShelterEntity {
        return ShelterEntity(
            id = this.id,
            name = this.name,
            address = this.address,
            latitude = this.latitude,
            longitude = this.longitude,
            capacity = this.capacity,
            currentOccupancy = this.currentOccupancy,
            isOpen = this.isOpen,
            phoneContact = this.phoneContact.takeIf { it.isNotBlank() },
            responsible = this.responsible.takeIf { it.isNotBlank() },
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Convierte un ShelterEntity (Room) a un Shelter (dominio).
     */
    fun ShelterEntity.toDomain(): Shelter {
        return Shelter(
            id = this.id,
            name = this.name,
            isOpen = this.isOpen,
            address = this.address,
            capacity = this.capacity,
            currentOccupancy = this.currentOccupancy,
            latitude = this.latitude,
            longitude = this.longitude,
            phoneContact = this.phoneContact ?: "",
            responsible = this.responsible ?: ""
        )
    }
    
    // ==================== RISK ZONE ====================
    
    /**
     * Convierte un RiskZone (dominio) a un RiskZoneEntity (Room).
     */
    fun RiskZone.toEntity(): RiskZoneEntity {
        return RiskZoneEntity(
            id = this.id,
            name = this.name,
            riskLevel = this.riskLevel,
            areaJson = json.encodeToString(this.area),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Convierte un RiskZoneEntity (Room) a un RiskZone (dominio).
     */
    fun RiskZoneEntity.toDomain(): RiskZone {
        val area = try {
            json.decodeFromString<List<SerializableLatLng>>(this.areaJson)
        } catch (e: Exception) {
            emptyList()
        }
        
        return RiskZone(
            id = this.id,
            name = this.name,
            riskLevel = this.riskLevel,
            area = area
        )
    }
    
    // ==================== FLOODED STREET ====================
    
    /**
     * Convierte un FloodedStreet (dominio) a un FloodedStreetEntity (Room).
     */
    fun FloodedStreet.toEntity(): FloodedStreetEntity {
        return FloodedStreetEntity(
            id = this.id,
            pathJson = json.encodeToString(this.path),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Convierte un FloodedStreetEntity (Room) a un FloodedStreet (dominio).
     */
    fun FloodedStreetEntity.toDomain(): FloodedStreet {
        val path = try {
            json.decodeFromString<List<SerializableLatLng>>(this.pathJson)
        } catch (e: Exception) {
            emptyList()
        }
        
        return FloodedStreet(
            id = this.id,
            path = path
        )
    }
}

