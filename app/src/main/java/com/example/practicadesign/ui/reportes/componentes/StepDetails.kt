package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Paso 3: Detalles del reporte.
 * 
 * Permite al usuario ingresar el título, descripción y nivel de urgencia del reporte.
 * 
 * @param title Título del reporte
 * @param onTitleChange Callback cuando cambia el título
 * @param description Descripción del reporte
 * @param onDescriptionChange Callback cuando cambia la descripción
 * @param titleCount Contador de caracteres del título
 * @param descCount Contador de caracteres de la descripción
 * @param urgency Nivel de urgencia seleccionado (high, medium, low)
 * @param onUrgencyChange Callback cuando cambia el nivel de urgencia
 */
@Preview(showBackground = true)
@Composable
fun StepDetailsScreenPreview() {
    StepDetails(
        title = "",
        onTitleChange = {},
        description = "",
        onDescriptionChange = {},
        titleCount = 0,
        descCount = 0,
        urgency = "medium",
        onUrgencyChange = {}
    )
}

@Composable
fun StepDetails(
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
        Text(
            text = "$titleCount/60",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Right
        )

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
        Text(
            text = "$descCount/500",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Right
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Urgency selector
        Text("Nivel de urgencia", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            UrgencyButton(
                label = "Alta",
                emoji = "🔴",
                selected = urgency == "high",
                modifier = Modifier.weight(1f),
                onClick = { onUrgencyChange("high") }
            )
            UrgencyButton(
                label = "Media",
                emoji = "🟡",
                selected = urgency == "medium",
                modifier = Modifier.weight(1f),
                onClick = { onUrgencyChange("medium") }
            )
            UrgencyButton(
                label = "Baja",
                emoji = "🟢",
                selected = urgency == "low",
                modifier = Modifier.weight(1f),
                onClick = { onUrgencyChange("low") }
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

/**
 * Botón para seleccionar el nivel de urgencia.
 * 
 * @param label Etiqueta del botón (Alta, Media, Baja)
 * @param emoji Emoji que representa el nivel de urgencia
 * @param selected Indica si este botón está seleccionado
 * @param modifier Modificador de Compose para personalizar el layout
 * @param onClick Callback cuando se presiona el botón
 */
@Composable
private fun UrgencyButton(
    label: String,
    emoji: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        }
    }
}
