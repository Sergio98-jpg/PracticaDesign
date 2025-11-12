package com.example.practicadesign.ui.profile.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.practicadesign.ui.refugios.ShelterFilter
import com.example.practicadesign.ui.refugios.componentes.QuickFiltersRow

/**
 * Encabezado del perfil que muestra la foto, nombre y ubicación del usuario.
 * 
 * @param name Nombre del usuario
 * @param location Ubicación del usuario
 * @param avatarUrl URL de la foto de perfil
 * @param onEditClick Callback cuando se presiona el botón de editar
 */

@Preview(showBackground = true, name = "Filtros (Todos seleccionados)")
@Composable
private fun ProfileHeaderPreview() {
    ProfileHeader(
        name = "Nombre de Usuario",
        location = "Ubicación del Usuario",
        avatarUrl = "",
        onEditClick = {})
}
@Composable
fun ProfileHeader(
    name: String,
    location: String,
    avatarUrl: String,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil con botón de editar superpuesto
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl.ifBlank { null })
                    .crossfade(true)
                    .build(),
                contentDescription = "Foto de perfil del usuario",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE5B4), CircleShape) // Fondo naranja claro
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
            
            // Botón de editar superpuesto en la parte inferior derecha
            FloatingActionButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(36.dp),
                containerColor = Color(0xFF00A9E0),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(2.dp)
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Editar perfil",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Nombre del usuario
        Text(
            text = name.ifBlank { "Usuario" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // Ubicación del usuario
        Text(
            text = location.ifBlank { "Ubicación no disponible" },
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

