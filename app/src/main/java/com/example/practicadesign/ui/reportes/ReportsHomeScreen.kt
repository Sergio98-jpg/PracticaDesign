package com.example.practicadesign.ui.reportes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Lucide

/**
 * Pantalla de inicio de reportes que muestra 3 opciones:
 * 1. Levantar nuevo reporte
 * 2. Continuar reporte (si hay borrador pendiente)
 * 3. Ver historial de reportes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsHomeScreen(
    onNavigateToNewReport: () -> Unit,
    onNavigateToContinueReport: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onBack: () -> Unit,
    reportViewModel: ReportViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    // Verificar si hay borrador pendiente
    val hasDraft by reportViewModel.hasDraft.collectAsState(initial = false)
    
    // Refrescar el estado cuando se muestra la pantalla
    androidx.compose.runtime.LaunchedEffect(Unit) {
        reportViewModel.refreshDraftStatus()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reportes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título de bienvenida
            Text(
                text = "Gestiona tus reportes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Selecciona una opción para continuar",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Opción 1: Levantar nuevo reporte
            ReportOptionCard(
                title = "Levantar Reporte",
                description = "Crea un nuevo reporte de incidente",
                icon = Lucide.Plus,
                iconColor = Color(0xFF0891B2),
                onClick = onNavigateToNewReport,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Opción 2: Continuar reporte (solo si hay borrador)
            if (hasDraft) {
                ReportOptionCard(
                    title = "Continuar Reporte",
                    description = "Continúa con el reporte que estabas creando",
                    icon = Lucide.FileText,
                    iconColor = Color(0xFFF59E0B),
                    onClick = onNavigateToContinueReport,
                    modifier = Modifier.fillMaxWidth(),
                    badge = "Pendiente"
                )
            }
            
            // Opción 3: Ver historial
            ReportOptionCard(
                title = "Historial de Reportes",
                description = "Consulta tus reportes anteriores",
                icon = Lucide.History,
                iconColor = Color(0xFF8B5CF6),
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Tarjeta de opción para la pantalla de inicio de reportes
 */
@Composable
private fun ReportOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ícono
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    
                    // Badge si existe
                    badge?.let {
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
            
            // Flecha
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

