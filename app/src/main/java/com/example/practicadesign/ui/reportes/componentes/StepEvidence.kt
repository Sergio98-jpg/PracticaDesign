package com.example.practicadesign.ui.reportes.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Paso 4: Evidencia fotográfica del reporte.
 * 
 * Permite al usuario agregar hasta 3 fotos del incidente desde la galería o la cámara.
 * 
 * @param photos Lista de URIs de las fotos adjuntas (máximo 3)
 * @param onPhotoAdded Callback cuando se agrega una foto (recibe el índice y la URI)
 */
@Composable
fun StepEvidence(
    photos: List<Uri?>,
    onPhotoAdded: (Int, Uri?) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableStateOf(0) }

    // Lanzador para seleccionar imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                onPhotoAdded(selectedPhotoIndex, it)
            }
        }
    )

    // Lanzador para solicitar permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // TODO: Implementar captura de foto desde la cámara
            }
        }
    )

    // Lanzador para solicitar permiso de galería
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                imagePickerLauncher.launch("image/*")
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Agregar evidencia",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Añade fotos del incidente (opcional)",
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Cuadrícula de fotos (máximo 3)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            photos.forEachIndexed { idx, uri ->
                PhotoItem(
                    imageUri = uri,
                    onClick = {
                        selectedPhotoIndex = idx
                        showDialog = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }

    // Diálogo para elegir entre cámara o galería
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Seleccionar Fuente") },
            text = { Text(text = "¿Desde dónde quieres agregar la foto?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    val permission = Manifest.permission.READ_MEDIA_IMAGES
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            imagePickerLauncher.launch("image/*")
                        }
                        else -> {
                            galleryPermissionLauncher.launch(permission)
                        }
                    }
                }) {
                    Text(text = "Galería")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    val permission = Manifest.permission.CAMERA
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            // TODO: Implementar captura de foto desde la cámara
                        }
                        else -> {
                            cameraPermissionLauncher.launch(permission)
                        }
                    }
                }) {
                    Text(text = "Cámara")
                }
            }
        )
    }
}

/**
 * Componente para mostrar un elemento de foto en la cuadrícula.
 * 
 * @param imageUri URI de la imagen (null si no hay foto)
 * @param onClick Callback cuando se presiona el elemento
 */
@Composable
private fun RowScope.PhotoItem(
    imageUri: Uri?,
    onClick: () -> Unit
) {
    val hasPhoto = imageUri != null
    Card(
        modifier = Modifier
            .weight(1f)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            2.dp,
            if (hasPhoto) Color(0xFF0891B2) else Color(0xFFE2E8F0)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (hasPhoto) Color(0xFFF0FDFA) else Color.White
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (hasPhoto) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Evidencia fotográfica",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📷", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Agregar foto",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
