package com.example.practicadesign.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicadesign.ui.profile.componentes.*

/**
 * Pantalla de perfil del usuario que muestra:
 * - Información del usuario (foto, nombre, ubicación)
 * - Estadísticas (reportes enviados, alertas recibidas)
 * - Opciones de configuración (información personal, notificaciones, seguridad)
 * - Opción de cerrar sesión
 * 
 * Sigue el patrón MVVM utilizando ProfileViewModel para gestionar el estado.
 * El bottomBar se maneja desde AppNavigation, por lo que no se incluye aquí.
 * 
 * @param onBack Callback cuando se presiona el botón de retroceso
 * @param onLogout Callback cuando se presiona el botón de cerrar sesión
 * @param profileViewModel ViewModel que gestiona el estado del perfil
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ProfileTopBar(
                onBack = onBack,
                onOptionsClick = { /* TODO: Implementar menú de opciones */ }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                // Muestra un indicador de carga centrado
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                // Muestra un mensaje de error con opción de reintentar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF64748B)
                        )
                        TextButton(onClick = { profileViewModel.retryLoadProfile() }) {
                            Text(
                                text = "Reintentar",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            else -> {
                // Muestra el contenido del perfil
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        ProfileHeader(
                            name = uiState.userInfo.name,
                            location = uiState.userInfo.location,
                            avatarUrl = uiState.userInfo.avatarUrl,
                            onEditClick = { profileViewModel.onEditProfileClick() }
                        )
                    }
                    
                    item {
                        Spacer(Modifier.height(16.dp))
                        StatsRow(
                            reportsSent = uiState.stats.reportsSent,
                            alertsReceived = uiState.stats.alertsReceived
                        )
                    }
                    
                    item {
                        Spacer(Modifier.height(24.dp))
                        SettingsSection(onLogout = onLogout)
                    }
                }
            }
        }
    }
}



/**
 * Preview de la pantalla de perfil para visualización en Android Studio.
 */
@Preview(
    showBackground = true,
    name = "Pantalla de Perfil",
   // showSystemUi = true
)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            onBack = {},
            onLogout = {}
        )
    }
}