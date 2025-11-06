// ReportScreen.kt
package com.example.practicadesign.ui.reportes

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.practicadesign.ui.mapa.MapScreen
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    ReportScreen(onClose = {})
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onClose: () -> Unit = {}
) {
    // State
    var currentStep by rememberSaveable { mutableIntStateOf(1) }
    val totalSteps = 5

    // Report data
    var selectedType by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedLocation by rememberSaveable { mutableStateOf<String?>(null) } // "current" or "map"
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var urgency by rememberSaveable { mutableStateOf("medium") } // high / medium / low
    var photos by rememberSaveable { mutableStateOf(List(3) { false }) } // boolean: has photo
    var successVisible by rememberSaveable { mutableStateOf(false) }

    // Char counts
    val titleCount = title.length
    val descCount = description.length

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Levantar Reporte", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        // behave like goBack() in HTML: if not first step go previous, else close
                        if (currentStep > 1) currentStep-- else onClose()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(2.dp)
            )
        },
        bottomBar = {
            BottomActions(
                currentStep = currentStep,
                totalSteps = totalSteps,
                onPrevious = { if (currentStep > 1) currentStep-- },
                onNext = {
                    if (currentStep < totalSteps) {
                        currentStep++
                        if (currentStep == totalSteps) {
                            // populate summary logic can go here if needed
                        }
                    } else {
                        // Send report (simulate)
                        successVisible = true
                    }
                }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF8FAFC))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                StepperHeader(currentStep = currentStep, totalSteps = totalSteps)

                // Animated Content between steps (slide horizontally)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally(
                                    initialOffsetX = { width -> width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeIn() with slideOutHorizontally(
                                    targetOffsetX = { width -> -width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeOut()
                            } else {
                                slideInHorizontally(
                                    initialOffsetX = { width -> -width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeIn() with slideOutHorizontally(
                                    targetOffsetX = { width -> width },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeOut()
                            }
                        },
                        label = "ReportStep"
                    ) { step ->
                        when (step) {
                            1 -> StepType(
                                selectedType = selectedType,
                                onSelect = { selectedType = it }
                            )
                            2 -> StepLocation(
                                selectedLocation = selectedLocation,
                                onSelect = { selectedLocation = it }
                            )
                            3 -> StepDetails(
                                title = title,
                                onTitleChange = { if (it.length <= 60) title = it },
                                description = description,
                                onDescriptionChange = { if (it.length <= 500) description = it },
                                titleCount = titleCount,
                                descCount = descCount,
                                urgency = urgency,
                                onUrgencyChange = { urgency = it }
                            )
                            4 -> StepEvidence(
                                photos = photos,
                                onTogglePhoto = { index ->
                                    photos = photos.toMutableList().also { it[index] = !it[index] }
                                }
                            )
                            5 -> StepSummary(
                                selectedType = selectedType,
                                selectedLocation = selectedLocation,
                                title = title,
                                urgency = urgency,
                                photosCount = photos.count { it }
                            )
                        }
                    }
                }
            }

            // Success animation overlay
            if (successVisible) {
                SuccessOverlay {
                    successVisible = false
                    // simulate close after animation
                    onClose()
                }
            }
        }
    }
}

/* -----------------------
   STEP HEADER / STEPPER
   ----------------------- */
@Composable
private fun StepperHeader(currentStep: Int, totalSteps: Int) {
    // val progress = (currentStep - 1).toFloat() / (totalSteps - 1)
    val targetProgress = (currentStep - 1).toFloat() / (totalSteps - 1)

    // ✅ 2. ANIMA EL PROGRESO
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "ProgressBarAnimation"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(4.dp)
    ) {
        // Progress bar background line
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE2E8F0))
            )
            // Foreground progress
            Box(
                modifier = Modifier
                    // ✅ 3. USA EL PROGRESO ANIMADO
                    .fillMaxWidth(animatedProgress)
                    .height(2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0891B2), Color(0xFF06B6D4))
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val labels = listOf("Tipo", "Ubicación", "Detalles", "Evidencia", "Confirmar")
            (1..totalSteps).forEach { idx ->
                val isCompleted = idx < currentStep
                val isActive = idx == currentStep
                val circleColor = when {
                    isCompleted -> Color(0xFF10B981)
                    isActive -> Color(0xFF0891B2)
                    else -> Color.White
                }
                val borderColor = when {
                    isCompleted -> Color(0xFF10B981)
                    isActive -> Color(0xFF0891B2)
                    else -> Color(0xFFE2E8F0)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(circleColor)
                            .border(BorderStroke(2.dp, borderColor), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text(
                                "$idx",
                                color = if (isActive) Color.White else Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = labels[idx - 1],
                        fontSize = 11.sp,
                        color = if (isActive) Color(0xFF0891B2) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

/* -----------------------
   STEP 1: TYPE SELECTION
   ----------------------- */
@Composable
private fun StepType(
    selectedType: String?,
    onSelect: (String) -> Unit
) {
    val types = listOf(
        "inundacion" to "Inundación" to "💧",
        "calle-bloqueada" to "Calle Bloqueada" to "🚧",
        "refugio-lleno" to "Refugio Lleno" to "🏠",
        "dano-infraestructura" to "Daño a Infraestructura" to "⚠️",
        "persona-riesgo" to "Persona en Riesgo" to "🆘",
        "otro" to "Otro" to "📝"
    )

    Column(modifier = Modifier
        .fillMaxSize()
       // .verticalScroll(rememberScrollState())
    ) {
        Text("¿Qué deseas reportar?", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Selecciona el tipo de incidente que quieres reportar", color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
          //  modifier = Modifier.fillMaxWidth(),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                items(types) { triple ->
                    // triple: Pair<Pair<String,String>, String> due to above build; adapt
                    val key = triple.first.first
                    val name = triple.first.second
                    val emoji = triple.second
                    val selected = selectedType == key

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .clickable { onSelect(key) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFF0FDFA) else Color.White),
                        border = BorderStroke(2.dp, if (selected) Color(0xFF0891B2) else Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) Brush.linearGradient(
                                            listOf(
                                                Color(
                                                    0xFF0891B2
                                                ), Color(0xFF06B6D4)
                                            )
                                        ) else SolidColor(Color(0xFFF1F5F9))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(name, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = Color(0xFF334155))
                        }
                    }
                }
            })
        Spacer(modifier = Modifier.height(120.dp))
    }
}

/* -----------------------
   STEP 2: LOCATION SELECTION
   ----------------------- */
@Composable
private fun StepLocation(
    selectedLocation: String?,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Text("¿Dónde ocurrió?", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Selecciona la ubicación del incidente", color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LocationOption(
                title = "Mi ubicación actual",
                subtitle = "Usar GPS para ubicación exacta",
                selected = selectedLocation == "current",
                onSelect = { onSelect("current") }
            )
            LocationOption(
                title = "Seleccionar en mapa",
                subtitle = "Elegir ubicación manualmente",
                selected = selectedLocation == "map",
                onSelect = { onSelect("map") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map preview placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0F2F1)),
            contentAlignment = Alignment.Center
        ) {
            // Marker
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0891B2))
                    .border(BorderStroke(4.dp, Color.White), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun LocationOption(title: String, subtitle: String, selected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFF0FDFA) else Color.White),
        border = BorderStroke(2.dp, if (selected) Color(0xFF0891B2) else Color(0xFFE2E8F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) Brush.linearGradient(
                            listOf(
                                Color(0xFF0891B2),
                                Color(0xFF06B6D4)
                            )
                        ) else SolidColor(Color(0xFFF1F5F9))
                    ),
                contentAlignment = Alignment.Center
            ) {
                // simple "gps" icon mock with circle
                Box(modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (selected) Color.White else Color(0xFF94A3B8)))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = Color(0xFF0F172A))
                Text(subtitle, color = Color(0xFF64748B))
            }
        }
    }
}

/* -----------------------
   STEP 3: DETAILS
   ----------------------- */
@Composable
private fun StepDetails(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    titleCount: Int,
    descCount: Int,
    urgency: String,
    onUrgencyChange: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Text("Detalles del reporte", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Proporciona información sobre el incidente", color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text("Título del reporte *", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ej: Inundación en Av. Principal") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF0891B2),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF0F172A),
                cursorColor = Color(0xFF0891B2)
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Text("$titleCount/60", modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp), color = Color(0xFF94A3B8), textAlign = androidx.compose.ui.text.style.TextAlign.Right)

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text("Descripción *", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            placeholder = { Text("Describe lo que está ocurriendo...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF0891B2),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF0F172A),
                cursorColor = Color(0xFF0891B2)
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Text("$descCount/500", modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp), color = Color(0xFF94A3B8), textAlign = androidx.compose.ui.text.style.TextAlign.Right)

        Spacer(modifier = Modifier.height(16.dp))

        // Urgency selector
        Text("Nivel de urgencia", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            UrgencyButton(label = "Alta", emoji = "🔴", selected = urgency == "high", onClick = { onUrgencyChange("high") })
            UrgencyButton(label = "Media", emoji = "🟡", selected = urgency == "medium", modifier = Modifier.weight(1f), onClick = { onUrgencyChange("medium") })
            UrgencyButton(label = "Baja", emoji = "🟢", selected = urgency == "low", onClick = { onUrgencyChange("low") })
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun UrgencyButton(label: String, emoji: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val border = when {
        selected && label == "Alta" -> Color(0xFFEF4444)
        selected && label == "Media" -> Color(0xFFF59E0B)
        selected && label == "Baja" -> Color(0xFF10B981)
        else -> Color(0xFFE2E8F0)
    }
    val bg = when {
        selected && label == "Alta" -> Color(0xFFFFF2F2)
        selected && label == "Media" -> Color(0xFFFFFBEB)
        selected && label == "Baja" -> Color(0xFFF0FDF4)
        else -> Color.White
    }

    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(88.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, border),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(8.dp)) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        }
    }
}

/* -----------------------
   STEP 4: EVIDENCE (photos)
   ----------------------- */
@Composable
private fun StepEvidence(
    photos: List<Boolean>,
    onTogglePhoto: (Int) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Text("Agregar evidencia", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Añade fotos del incidente (opcional)", color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        // Photo grid (3)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            photos.forEachIndexed { idx, has ->
                PhotoItem(hasPhoto = has, onClick = { onTogglePhoto(idx) })
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun RowScope.PhotoItem(hasPhoto: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, if (hasPhoto) Color(0xFF0891B2) else Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = if (hasPhoto) Color(0xFFF0FDFA) else Color.White)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (hasPhoto) {
                Text("✓", fontSize = 28.sp, color = Color(0xFF0891B2))
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Agregar foto", fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

/* -----------------------
   STEP 5: SUMMARY
   ----------------------- */
@Composable
private fun StepSummary(
    selectedType: String?,
    selectedLocation: String?,
    title: String,
    urgency: String,
    photosCount: Int
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Text("Confirmar reporte", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(6.dp))
        Text("Revisa la información antes de enviar", color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                SummaryRow(label = "Tipo de reporte", value = mapTypeKeyToName(selectedType))
                SummaryRow(label = "Ubicación", value = mapLocationKeyToName(selectedLocation))
                SummaryRow(label = "Título", value = title.ifBlank { "-" })
                SummaryRow(label = "Urgencia", value = mapUrgencyToLabel(urgency))
                SummaryRow(label = "Fotos adjuntas", value = photosCount.toString())
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF64748B))
        Text(value, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
    }
}

private fun mapTypeKeyToName(key: String?): String {
    return when (key) {
        "inundacion" -> "Inundación"
        "calle-bloqueada" -> "Calle Bloqueada"
        "refugio-lleno" -> "Refugio Lleno"
        "dano-infraestructura" -> "Daño a Infraestructura"
        "persona-riesgo" -> "Persona en Riesgo"
        "otro" -> "Otro"
        else -> "-"
    }
}

private fun mapLocationKeyToName(key: String?): String {
    return when (key) {
        "current" -> "Mi ubicación actual"
        "map" -> "Seleccionada en mapa"
        else -> "-"
    }
}

private fun mapUrgencyToLabel(u: String) = when (u) {
    "high" -> "🔴 Alta"
    "medium" -> "🟡 Media"
    "low" -> "🟢 Baja"
    else -> "-"
}

/* -----------------------
   BOTTOM ACTIONS
   ----------------------- */
@Composable
private fun BottomActions(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 1) {
                Button(
                    onClick = onPrevious,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Anterior", color = Color(0xFF334155))
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (currentStep == totalSteps) "Enviar Reporte" else "Siguiente", color = Color.White)
            }
        }
    }
}

/* -----------------------
   SUCCESS OVERLAY
   ----------------------- */
@Composable
private fun SuccessOverlay(onDone: () -> Unit) {
    // Simple overlay with scale animation and auto-dismiss
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        // keep shown 1.8s then auto-dismiss
        delay(1800)
        visible = false
        delay(250)
        onDone()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Reporte enviado!", color = Color.White, fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            }
        }
    }
}
