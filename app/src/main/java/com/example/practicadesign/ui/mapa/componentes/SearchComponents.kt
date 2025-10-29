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
import com.composables.icons.lucide.HousePlug
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.TriangleAlert

// --- ✅ AÑADE ESTE MODELO GENÉRICO PARA LOS RESULTADOS ---
data class SearchResult(
    val id: String,
    val type: String,
    val name: String,
    val address: String,
    val icon: ImageVector,
    val iconColor: Color
)

@Preview(showBackground = true, name = "Search Overlay Preview")
@Composable
fun PreviewSearchOverlay() {
    // 1. Crea una lista de resultados de búsqueda de ejemplo.
    val sampleResults = listOf(
        SearchResult(
            id = "shelter_1",
            type = "Refugio",
            name = "Refugio Deportivo Benito Juárez",
            address = "Av. de los Insurgentes Sur 300",
            icon = Lucide.House, // Asegúrate de importar Lucide.Home
            iconColor = Color(0xFF0EA5E9)
        ),
        SearchResult(
            id = "zone_1",
            type = "Zona de Riesgo",
            name = "Peligro Alto",
            address = "Colonia Centro, cerca del río",
            icon = Lucide.TriangleAlert, // Y Lucide.TriangleAlert
            iconColor = Color(0xFFEF4444)
        ),
        SearchResult(
            id = "shelter_2",
            type = "Refugio",
            name = "Escuela Primaria Leona Vicario",
            address = "Calle 50 por 61 y 63, Centro",
            icon = Lucide.House,
            iconColor = Color(0xFF0EA5E9)
        )
    )

    // 2. Llama a tu Composable pasándole los datos de ejemplo y funciones vacías.
    SearchOverlay(
        searchResults = sampleResults,
        searchQuery = TextFieldValue(""), // Un campo de búsqueda vacío al inicio
        onQueryChange = {}, // Función vacía: en la preview no necesitamos que haga nada.
        onDismiss = {},     // Función vacía
        onItemSelected = {} // Función vacía
    )
}
// --- ✅ AÑADE ESTE COMPOSABLE PARA CADA ITEM EN LA LISTA ---
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

@Composable
fun SearchOverlay(
    // Ahora recibe la lista de resultados directamente
    searchResults: List<SearchResult>,
    searchQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onDismiss: () -> Unit,
    onItemSelected: (SearchResult) -> Unit
) {
    // ---- 1. GESTIÓN DEL CIERRE Y FONDO OSCURO (SCRIM) ----

    // Permite cerrar el Popup al presionar el botón de "atrás" del dispositivo
    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                indication = null, // sin efecto ripple
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss
            )
    )

    // ---- 2. EL CONTENIDO DEL POPUP ----
    Popup(
        alignment = Alignment.TopCenter, // Lo alineamos a la parte superior
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true) // Permite que el TextField reciba foco
    ) {
        // Usamos una Column para que el contenido se dimensione a sí mismo
        Column(
            modifier = Modifier
                // El padding superior empuja el contenido hacia abajo hasta la posición del SearchBar
                .padding(top = 178.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                // La altura máxima será el 60% de la pantalla
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.6f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val focusRequester = remember { FocusRequester() }

            // --- Barra de búsqueda interna ---
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
                colors = TextFieldDefaults.colors( // Usa el nuevo `colors` de M3
                    focusedContainerColor = Color(0x8AA29E9F),
                    unfocusedContainerColor = Color(0x8AA29E9F),
                    disabledContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )

            // Pide el foco del teclado automáticamente cuando aparece
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            HorizontalDivider()

            // --- Lista de resultados ---
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(searchResults) { result ->
                    // Reutilizamos nuestro SearchResultItem
                    SearchResultItem(result = result) {
                        onItemSelected(result)
                    }
                   // HorizontalDivider()
                }
            }
        }
    }
}