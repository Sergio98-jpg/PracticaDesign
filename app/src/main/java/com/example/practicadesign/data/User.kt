package com.example.practicadesign.data

// User.kt
data class User(
    val name: String,
    val location: String,
    val avatarUrl: String // Puedes usar una URL o un ID de drawable
)

data class Stats(
    val reports: Int,
    val alerts: Int
)

// Opcional: Puedes tener un modelo para las opciones de configuración
data class SettingItem(
    val title: String,
    val icon: String, // Puedes usar un Icon de Material Icons o un ID de drawable
    val action: () -> Unit
)