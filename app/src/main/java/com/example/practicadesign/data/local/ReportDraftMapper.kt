package com.example.practicadesign.data.local

import android.net.Uri
import com.example.practicadesign.ui.reportes.ReportUiState
import com.google.android.gms.maps.model.LatLng

/**
 * Funciones de mapeo entre ReportDraftEntity (Room) y ReportUiState (UI).
 * 
 * Permite convertir entre el formato de base de datos y el formato usado en la UI.
 */

/**
 * Convierte un ReportUiState a un ReportDraftEntity para guardarlo en Room.
 * 
 * @param uiState Estado actual del formulario de reportes
 * @return Entidad de Room lista para guardar
 */
fun ReportUiState.toEntity(): ReportDraftEntity {
        return ReportDraftEntity(
            currentStep = this.currentStep,
            selectedType = this.selectedType,
            selectedLocation = this.selectedLocation,
            locationLat = this.locationCoordinates?.latitude,
            locationLng = this.locationCoordinates?.longitude,
            title = this.title,
            description = this.description,
            urgency = this.urgency,
            photoUris = this.photos.mapNotNull { it?.toString() },
            updatedAt = System.currentTimeMillis()
        )
    }

/**
 * Convierte un ReportDraftEntity a un ReportUiState para restaurar el formulario.
 * 
 * @param entity Entidad de Room con el borrador guardado
 * @return Estado inicial del formulario restaurado
 */
fun ReportDraftEntity.toUiState(): ReportUiState {
        val locationCoordinates = if (locationLat != null && locationLng != null) {
            LatLng(locationLat, locationLng)
        } else {
            null
        }
        
        // Convertir las URIs de String a Uri
        val photos = if (photoUris.isEmpty()) {
            List(3) { null }
        } else {
            val photoList = photoUris.map { uriString ->
                try {
                    Uri.parse(uriString)
                } catch (e: Exception) {
                    null
                }
            }
            // Asegurar que siempre hay 3 elementos (rellenar con null si es necesario)
            photoList + List(3 - photoList.size) { null }
        }.take(3)
        
        return ReportUiState(
            currentStep = this.currentStep,
            selectedType = this.selectedType,
            selectedLocation = this.selectedLocation,
            locationCoordinates = locationCoordinates,
            title = this.title,
            description = this.description,
            urgency = this.urgency,
            photos = photos,
            isLoading = false,
            errorMessage = null,
            successVisible = false,
            fieldErrors = emptyMap()
        )
    }

