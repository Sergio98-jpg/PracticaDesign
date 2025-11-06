package com.example.practicadesign.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import androidx.wear.compose.foundation.weight
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.User
import com.example.practicadesign.ui.mapa.componentes.FloatingMenu

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit = {}
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Logo ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 32.dp, bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0891B2), Color(0xFF06B6D4))
                            )
                        ),

                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Droplets,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text("Yáanal Ha'", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("Mérida, YUC", fontSize = 14.sp, color = Color(0xFF64748B))
                }
            }

            // --- Tabs ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TabButton("Iniciar Sesión", isLogin) { isLogin = true }
                TabButton("Registrarse", !isLogin) { isLogin = false }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Animated Content (slide between login/register) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(8.dp, RoundedCornerShape(24.dp)) // ✅ sombra real recortada
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .animateContentSize(animationSpec = tween(300, easing = LinearOutSlowInEasing))
            ) {
                AnimatedContent(
                    targetState = isLogin,
                    transitionSpec = {
                        if (targetState) {
                            (slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth: Int -> -fullWidth }
                            ) + fadeIn()).togetherWith(
                                slideOutHorizontally(
                                                                animationSpec = tween(300),
                                                                targetOffsetX = { fullWidth: Int -> fullWidth }
                                                            ) + fadeOut())
                        } else {
                            (slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { fullWidth: Int -> fullWidth }
                            ) + fadeIn()).togetherWith(
                                slideOutHorizontally(
                                                                animationSpec = tween(300),
                                                                targetOffsetX = { fullWidth: Int -> -fullWidth }
                                                            ) + fadeOut())
                        }
                    },
                    label = "FormTransition"
                ) { loginMode ->
                    if (loginMode) {
                        // --- LOGIN FORM ---
                        LoginForm(
                            email = email,
                            password = password,
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            onLogin = {
                                val role = if (email.startsWith("admin")) "admin" else "user"
                                onLoginSuccess(role)
                            }
                        )
                    } else {
                        // --- REGISTER FORM ---
                        RegisterForm(
                            name = name,
                            email = email,
                            phone = phone,
                            password = password,
                            confirmPassword = confirmPassword,
                            onNameChange = { name = it },
                            onEmailChange = { email = it },
                            onPhoneChange = { phone = it },
                            onPasswordChange = { password = it },
                            onConfirmPasswordChange = { confirmPassword = it },
                            onRegister = { /* TODO: Lógica de registro */ }
                        )
                    }
                }
            }
        }
    }
}

/* -------------------------------------------------
   COMPONENTES REUTILIZABLES
------------------------------------------------- */

@Composable
fun RowScope.TabButton( // <-- ✅ 1. Cambia el receptor a RowScope
    text: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier // <-- ✅ 2. Usa el Modifier que viene del scope
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = if (active)
                    Brush.linearGradient(listOf(Color(0xFF0891B2), Color(0xFF06B6D4)))
                else
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    )  {
        Text(
            text = text,
            color = if (active) Color.White else Color(0xFF64748B),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


/* -------------------------------------------------
   FORMULARIOS
------------------------------------------------- */

@Composable
fun LoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido de nuevo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(label = "Correo electrónico", value = email, onValueChange = onEmailChange, icon = Lucide.Mail)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(label = "Contraseña", value = password, onValueChange = onPasswordChange, icon = Lucide.Lock, isPassword = true)

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "¿Olvidaste tu contraseña?",
            color = Color(0xFF0891B2),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0891B2)
            )
        ) {
            Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RegisterForm(
    name: String,
    email: String,
    phone: String,
    password: String,
    confirmPassword: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear cuenta nueva", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(label = "Nombre completo", value = name, onValueChange = onNameChange, icon = Lucide.User)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(label = "Correo electrónico", value = email, onValueChange = onEmailChange, icon = Lucide.Mail)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(label = "Teléfono", value = phone, onValueChange = onPhoneChange, icon = Lucide.Phone)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(label = "Contraseña", value = password, onValueChange = onPasswordChange, icon = Lucide.Lock, isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(label = "Confirmar contraseña", value = confirmPassword, onValueChange = onConfirmPasswordChange, icon = Lucide.Lock, isPassword = true)

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2))
        ) {
            Text("Crear Cuenta", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Al registrarte, aceptas nuestros Términos de Servicio y Política de Privacidad",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
    }
}

/* -------------------------------------------------
   CAMPO DE TEXTO CON ÍCONO
------------------------------------------------- */
@Composable
fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isPassword: Boolean = false
) {

    // 1. Estado para controlar la visibilidad
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF94A3B8))
        },// 2. Lógica de visibilidad
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },// 3. Opcional: Para el tipo de teclado correcto
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else if (label.contains("Correo")) {
            KeyboardOptions(keyboardType = KeyboardType.Email)
        } else if (label.contains("Teléfono")) {
            KeyboardOptions(keyboardType = KeyboardType.Phone)
        } else {
            KeyboardOptions.Default
        },
        // 4. Botón para alternar la visibilidad
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible)
                    Lucide.Eye // Ícono de ojo abierto
                else
                    Lucide.EyeOff // Ícono de ojo cerrado

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle visibility", tint = Color(0xFF94A3B8))
                }
            }
        },
       // visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0891B2),
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAccessScreen() {
    LoginScreen()
}
