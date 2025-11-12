package com.example.practicadesign.ui.profile.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Barra superior de la pantalla de perfil con gradiente azul claro.
 * 
 * @param onBack Callback cuando se presiona el botón de retroceso
 * @param onOptionsClick Callback cuando se presiona el botón de opciones
 */

@Preview(showBackground = true, name = "Filtros (Todos seleccionados)")
@Composable
private fun ProfileTopBarPreview() {
    ProfileTopBar( onBack = {}, onOptionsClick = {})
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    onBack: () -> Unit,
    onOptionsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Perfil",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Atrás",
                    tint = Color(0xFF0F172A)
                )
            }
        },
        actions = {
            IconButton(onClick = onOptionsClick) {
                Icon(
                    Icons.Outlined.MoreHoriz,
                    contentDescription = "Opciones",
                    tint = Color(0xFF0F172A)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFBFDBFE), // Azul claro superior
                        Color(0xFFDBEAFE), // Azul muy claro medio
                        Color.Transparent  // Transparente inferior
                    )
                )
            )
            .padding(bottom = 16.dp)
    )
}

