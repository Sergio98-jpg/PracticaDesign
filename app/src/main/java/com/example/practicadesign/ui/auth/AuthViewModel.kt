package com.example.practicadesign.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    fun onLoginSuccess(role: String) {
        _userRole.value = role
    }

    fun onLogout() {
        _userRole.value = null
    }
}