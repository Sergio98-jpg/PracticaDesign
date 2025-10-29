package com.example.practicadesign.ui.refugios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.practicadesign.ui.mapa.MapScreen

@Preview(showBackground = true)
@Composable
fun SheltersScreenPreview() {
    SheltersScreen(/*navController = NavController(LocalContext.current)*/)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheltersScreen(
   // navController: NavController,
    sheltersViewModel: SheltersViewModel = viewModel()
) {
    val uiState by sheltersViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Refugios Disponibles") }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.shelters) { shelter ->
                    ShelterListItem(shelter = shelter)
                }
            }
        }
    }
}

@Composable
fun ShelterListItem(shelter: com.example.practicadesign.ui.mapa.componentes.Shelter) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = shelter.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(text = shelter.address, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Capacidad: ${shelter.currentOccupancy} / ${shelter.capacity}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = if (shelter.isOpen) "Estado: Abierto" else "Estado: Cerrado",
                style = MaterialTheme.typography.bodySmall,
                color = if (shelter.isOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}