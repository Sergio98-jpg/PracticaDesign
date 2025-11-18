package com.example.practicadesign.ui.reportes

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.example.practicadesign.ui.reportes.componentes.*

/**
 * Pantalla para levantar un reporte de incidente.
 * 
 * Sigue el patrón MVVM utilizando ReportViewModel para gestionar el estado.
 * El formulario se divide en 5 pasos:
 * 1. Selección de tipo de reporte
 * 2. Selección de ubicación
 * 3. Detalles (título, descripción, urgencia)
 * 4. Evidencia (fotos)
 * 5. Confirmación y envío
 * 
 * @param onClose Callback cuando se cierra la pantalla
 * @param reportViewModel ViewModel que gestiona el estado del formulario
 */
@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    ReportScreen(onClose = {})
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onClose: () -> Unit = {},
    reportViewModel: ReportViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by reportViewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            ReportTopBar(
                currentStep = uiState.currentStep,
                onClose = onClose,
                onPreviousStep = { reportViewModel.onPreviousStep() }
            )
        },
        bottomBar = {
            BottomActions(
                currentStep = uiState.currentStep,
                totalSteps = ReportUiState.TOTAL_STEPS,
                onPrevious = { reportViewModel.onPreviousStep() },
                onNext = { reportViewModel.onNextStep() }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                StepperHeader(
                    currentStep = uiState.currentStep,
                    totalSteps = ReportUiState.TOTAL_STEPS
                )

                // Contenido animado entre pasos (slide horizontal)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    AnimatedContent(
                        targetState = uiState.currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally(
                                    initialOffsetX = { width -> width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeIn() togetherWith slideOutHorizontally(
                                    targetOffsetX = { width -> -width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeOut()
                            } else {
                                slideInHorizontally(
                                    initialOffsetX = { width -> -width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeIn() togetherWith slideOutHorizontally(
                                    targetOffsetX = { width -> width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeOut()
                            }
                        },
                        label = "ReportStep"
                    ) { step ->
                        when (step) {
                            1 -> StepType(
                                selectedType = uiState.selectedType,
                                onSelect = { reportViewModel.onTypeSelected(it) },
                                error = uiState.getFieldError("type")
                            )
                            2 -> StepLocation(
                                selectedLocation = uiState.selectedLocation,
                                currentCoordinates = uiState.locationCoordinates,
                                onSelect = { locationType, coordinates ->
                                    reportViewModel.onLocationSelected(locationType, coordinates)
                                },
                                error = uiState.getFieldError("location")
                            )
                            3 -> StepDetails(
                                title = uiState.title,
                                onTitleChange = { reportViewModel.onTitleChange(it) },
                                description = uiState.description,
                                onDescriptionChange = { reportViewModel.onDescriptionChange(it) },
                                titleCount = uiState.title.length,
                                descCount = uiState.description.length,
                                urgency = uiState.urgency,
                                onUrgencyChange = { reportViewModel.onUrgencyChange(it) },
                                titleError = uiState.getFieldError("title"),
                                descriptionError = uiState.getFieldError("description")
                            )
                            4 -> StepEvidence(
                                photos = uiState.photos,
                                onPhotoAdded = { index, uri ->
                                    reportViewModel.onPhotoAdded(index, uri)
                                }
                            )
                            5 -> StepSummary(
                                selectedType = uiState.selectedType,
                                selectedLocation = uiState.selectedLocation,
                                title = uiState.title,
                                urgency = uiState.urgency,
                                photosCount = uiState.photos.count { it != null }
                            )
                        }
                    }
                }
            }

            // Overlay de éxito cuando se envía el reporte
            if (uiState.successVisible) {
                SuccessOverlay {
                    // Resetea el estado del formulario completamente
                    reportViewModel.startNewReport()
                    // Cierra la pantalla después de la animación
                    onClose()
                }
            }
        }
    }
}

/**
 * Barra superior de la pantalla de reportes.
 * 
 * @param currentStep Paso actual del formulario
 * @param onClose Callback cuando se presiona el botón de cerrar
 * @param onPreviousStep Callback cuando se presiona el botón de paso anterior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTopBar(
    currentStep: Int,
    onClose: () -> Unit,
    onPreviousStep: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Levantar Reporte",
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .clickable { onClose() }
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar Reporte",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        modifier = Modifier.padding(bottom = 4.dp),
        windowInsets = WindowInsets(0.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.DarkGray
        ),
        navigationIcon = {
            // Mantiene espacio idéntico incluso sin botón
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentStep > 1) {
                    IconButton(onClick = onPreviousStep) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Paso Anterior"
                        )
                    }
                }
            }
        },
        actions = {}
    )
}