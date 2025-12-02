package com.example.practicadesign.ui.mapa.componentes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.TriangleAlert
import com.example.practicadesign.ui.theme.*

/**
 * Modelo de datos para un resultado de búsqueda.
 * 
 * Representa un elemento encontrado en la búsqueda, ya sea un refugio o una zona de riesgo.
 * 
 * @property id Identificador único del resultado
 * @property type Tipo de resultado ("Refugio" o "Zona de Riesgo")
 * @property name Nombre del resultado
 * @property address Dirección o ubicación del resultado
 * @property icon Ícono a mostrar para el resultado
 * @property iconColor Color del ícono
 */
data class SearchResult(
    val id: String,
    val type: String,
    val name: String,
    val address: String,
    val icon: ImageVector,
    val iconColor: Color
)

/**
 * Preview del overlay de búsqueda.
 */
@Preview(showBackground = true, name = "Search Overlay Preview")
@Composable
fun PreviewSearchOverlay() {
    PracticaDesignTheme(darkTheme = true) {
    val sampleResults = listOf(
        SearchResult(
            id = "shelter_1",
            type = "Refugio",
            name = "Refugio Deportivo Benito Juárez",
            address = "Av. de los Insurgentes Sur 300",
            icon = Lucide.House,
            iconColor = infoColor()
        ),
        SearchResult(
            id = "zone_1",
            type = "Zona de Riesgo",
            name = "Peligro Alto",
            address = "Colonia Centro, cerca del río",
            icon = Lucide.TriangleAlert,
            iconColor = riskHighColor()
        )
    )

    SearchOverlay(
        searchResults = sampleResults,
        searchQuery = TextFieldValue(""),
        onQueryChange = {},
        onDismiss = {},
        onItemSelected = {}
    )
}
}

/**
 * Componente individual para mostrar un resultado de búsqueda en la lista.
 * 
 * Muestra un ícono, el tipo, nombre y dirección del resultado.
 * 
 * @param result El resultado de búsqueda a mostrar
 * @param onClick Función a ejecutar cuando se hace clic en el resultado
 */
@Composable
fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono cuadrado a la izquierda
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(result.iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = result.icon,
                contentDescription = result.type,
                tint = result.iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Información a la derecha
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.type,
                style = MaterialTheme.typography.labelMedium,
                color = result.iconColor
            )
            Text(
                text = result.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = result.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Overlay de búsqueda que muestra un campo de búsqueda y una lista de resultados.
 * 
 * Se muestra como un popup sobre el mapa con un fondo oscuro semitransparente.
 * Permite buscar refugios y zonas de riesgo.
 * 
 * @param searchResults Lista de resultados de búsqueda a mostrar
 * @param searchQuery Valor actual del campo de búsqueda
 * @param onQueryChange Función a ejecutar cuando cambia el texto de búsqueda
 * @param onDismiss Función a ejecutar cuando se cierra el overlay
 * @param onItemSelected Función a ejecutar cuando se selecciona un resultado
 */
@Composable
fun SearchOverlay(
    searchResults: List<SearchResult>,
    searchQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onDismiss: () -> Unit,
    onItemSelected: (SearchResult) -> Unit
) {
    // Permite cerrar el overlay al presionar el botón de "atrás" del dispositivo
    BackHandler(onBack = onDismiss)

    // Fondo oscuro semitransparente que se puede hacer clic para cerrar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            )
    )

    // Contenido del popup
    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 178.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.6f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val focusRequester = remember { FocusRequester() }

            // Campo de búsqueda
            TextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar zona o dirección...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                leadingIcon = { Icon(Lucide.Search, contentDescription = null) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x8AA29E9F),
                    unfocusedContainerColor = Color(0x8AA29E9F),
                    disabledContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )

            // Solicita el foco del teclado automáticamente cuando aparece
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            HorizontalDivider()

            // Lista de resultados
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(searchResults) { result ->
                    SearchResultItem(result = result) {
                        onItemSelected(result)
                    }
                }
            }
        }
    }
}