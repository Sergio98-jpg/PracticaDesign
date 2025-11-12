package com.example.practicadesign.ui.refugios

import com.example.practicadesign.data.Shelter

/**
 * Representa el estado de la UI para la pantalla de Refugios ([SheltersScreen]).
 *
 * Esta data class es la única fuente de verdad para la UI. Contiene toda la información
 * necesaria para que la pantalla se dibuje a sí misma en un momento dado.
 *
 * @property isLoading Indica si se está realizando una carga inicial de datos.
 * @property shelters La lista completa y sin filtrar de refugios obtenida del repositorio.
 * @property selectedFilter El filtro actualmente seleccionado por el usuario.
 * @property expandedShelterId El ID del refugio que está actualmente expandido para ver su detalle.
 *                           Es `null` si ningún refugio está expandido.
 */
data class SheltersUiState(
    val isLoading: Boolean = true,
    val shelters: List<Shelter> = emptyList(),
    val selectedFilter: ShelterFilter = ShelterFilter.ALL,
    val errorMessage: String? = null,
    val expandedShelterId: String? = null
) {
    /**
     * Propiedad computada que devuelve la lista de refugios ya filtrada.
     * La lógica de filtrado se centraliza aquí para mantener el ViewModel limpio
     * y asegurar que la UI siempre muestre la lista correcta basada en [selectedFilter].
     */
    val filteredShelters: List<Shelter>
        get() = when (selectedFilter) {
            ShelterFilter.ALL -> shelters
            ShelterFilter.OPEN -> shelters.filter { it.isOpen }
            ShelterFilter.WITH_AVAILABILITY -> shelters.filter { it.availableSpaces > 0 }
        }
}

/**
 * Define los tipos de filtros rápidos disponibles en la pantalla de refugios.
 * 
 * @property ALL Muestra todos los refugios sin filtrar
 * @property OPEN Muestra solo los refugios que están abiertos
 * @property WITH_AVAILABILITY Muestra solo los refugios con disponibilidad (espacios disponibles > 0)
 */
enum class ShelterFilter {
    ALL,
    OPEN,
    WITH_AVAILABILITY
}
