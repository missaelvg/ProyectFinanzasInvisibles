package com.example.proyectfinanzasinvisibles.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.example.proyectfinanzasinvisibles.auth.backend.AuthRepository
import com.example.proyectfinanzasinvisibles.ui.theme.InvisibleInsightsTheme
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    InvisibleInsightsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título con estilo de la app
                Text(
                    text = "Finanzas Invisibles",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = if (isLogin) "Bienvenido de nuevo" else "Crea tu cuenta",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            trailingIcon = {
                                val image = if (passwordVisible) "Ocultar" else "Mostrar"
                                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(image, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        )

                        if (!isLogin) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirmar Contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                trailingIcon = {
                                    val image = if (confirmPasswordVisible) "Ocultar" else "Mostrar"
                                    TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Text(image, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            )
                        }

                        errorMsg?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val trimmedEmail = email.trim()
                                if (trimmedEmail.isBlank() || password.isBlank()) {
                                    errorMsg = "Por favor completa todos los campos"
                                    return@Button
                                }
                                scope.launch {
                                    isLoading = true
                                    if (isLogin) {
                                        val result = authRepository.login(trimmedEmail, password)
                                        if (result.isSuccess) {
                                            onLoginSuccess()
                                        } else {
                                            val exception = result.exceptionOrNull()
                                            Log.e("AUTH_ERROR", "Login fail: ${exception?.message}")
                                            errorMsg = "Error: Credenciales incorrectas"
                                        }
                                    } else {
                                        if (password != confirmPassword) {
                                            errorMsg = "Las contraseñas no coinciden"
                                            isLoading = false
                                            return@launch
                                        }
                                        if (password.length < 6) {
                                            errorMsg = "La contraseña debe tener al menos 6 caracteres"
                                            isLoading = false
                                            return@launch
                                        }
                                        val result = authRepository.signUp(trimmedEmail, password)
                                        if (result.isSuccess) {
                                            onLoginSuccess()
                                        } else {
                                            val exception = result.exceptionOrNull()
                                            Log.e("AUTH_ERROR", "Signup fail: ${exception?.message}")
                                            errorMsg = when {
                                                exception?.message?.contains("already in use", ignoreCase = true) == true -> "Este correo ya está registrado"
                                                exception?.message?.contains("badly formatted", ignoreCase = true) == true -> "Correo inválido (ej: usuario@correo.com)"
                                                else -> "Error: ${exception?.localizedMessage ?: "Fallo al crear cuenta"}"
                                            }
                                        }
                                    }
                                    isLoading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text(if (isLogin) "Iniciar Sesión" else "Registrarse", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { isLogin = !isLogin; errorMsg = null }) {
                    Text(
                        text = if (isLogin) "¿No tienes cuenta? Regístrate aquí" else "¿Ya tienes cuenta? Inicia sesión",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
