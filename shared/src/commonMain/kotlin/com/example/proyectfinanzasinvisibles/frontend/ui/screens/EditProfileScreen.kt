package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyectfinanzasinvisibles.backend.repositories.AuthRepository
import com.example.proyectfinanzasinvisibles.frontend.ui.components.BounceButton
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val repository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var originalEmail by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.getUserProfile()
            .onSuccess { data ->
                nombre = data["nombre"] as? String ?: ""
                apellido = data["apellido"] as? String ?: ""
                email = data["email"] as? String ?: ""
                originalEmail = email
                fechaNacimiento = data["fechaNacimiento"] as? String ?: ""
            }
            .onFailure {
                message = "No fue posible cargar el perfil."
                isError = true
            }
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EDITAR PERFIL", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 32.dp))

        ProfileField(nombre, { nombre = it }, "Nombre")
        Spacer(Modifier.height(16.dp))
        ProfileField(apellido, { apellido = it }, "Apellido")
        Spacer(Modifier.height(16.dp))
        ProfileField(email, { email = it }, "Correo electrónico", KeyboardType.Email)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(fechaNacimiento, {}, label = { Text("Fecha de nacimiento") },
            modifier = Modifier.fillMaxWidth(), enabled = false, shape = RoundedCornerShape(12.dp))

        Spacer(Modifier.height(24.dp)); HorizontalDivider(); Spacer(Modifier.height(24.dp))
        Text("Seguridad", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        Text("Para cambiar correo o contraseña, confirma primero tu contraseña actual.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.align(Alignment.Start).padding(top = 4.dp, bottom = 12.dp))

        PasswordField(currentPassword, { currentPassword = it }, "Contraseña actual")
        Spacer(Modifier.height(12.dp))
        PasswordField(newPassword, { newPassword = it }, "Nueva contraseña (opcional)")
        Spacer(Modifier.height(12.dp))
        PasswordField(confirmPassword, { confirmPassword = it }, "Confirmar nueva contraseña")

        message?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = if (isError) MaterialTheme.colorScheme.error else Color(0xFF66BB6A))
        }
        Spacer(Modifier.height(24.dp))

        BounceButton(
            onClick = {
                val emailChanged = email.trim() != originalEmail
                val passwordChanged = newPassword.isNotBlank()
                when {
                    nombre.isBlank() || apellido.isBlank() || email.isBlank() -> {
                        message = "Completa nombre, apellido y correo."; isError = true
                    }
                    passwordChanged && newPassword.length < 6 -> {
                        message = "La nueva contraseña debe tener al menos 6 caracteres."; isError = true
                    }
                    passwordChanged && newPassword != confirmPassword -> {
                        message = "Las contraseñas nuevas no coinciden."; isError = true
                    }
                    (emailChanged || passwordChanged) && currentPassword.isBlank() -> {
                        message = "Escribe tu contraseña actual para confirmar el cambio."; isError = true
                    }
                    else -> scope.launch {
                        isSaving = true; message = null
                        val reauth = if (emailChanged || passwordChanged) repository.reauthenticate(currentPassword)
                            else Result.success(Unit)
                        if (reauth.isFailure) {
                            message = "La contraseña actual no es correcta."; isError = true; isSaving = false
                            return@launch
                        }
                        val profile = repository.updateProfile(nombre.trim(), apellido.trim(), email.trim())
                        val password = if (profile.isSuccess && passwordChanged) repository.updatePassword(newPassword)
                            else Result.success(Unit)
                        isError = profile.isFailure || password.isFailure
                        message = when {
                            profile.isFailure -> "No fue posible actualizar el perfil: ${profile.exceptionOrNull()?.message.orEmpty()}"
                            password.isFailure -> "El perfil se actualizó, pero la contraseña no pudo cambiarse."
                            emailChanged -> "Perfil actualizado. Revisa el correo nuevo y abre el enlace de verificación."
                            else -> "Perfil actualizado correctamente."
                        }
                        if (!isError) {
                            currentPassword = ""; newPassword = ""; confirmPassword = ""
                        }
                        isSaving = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !isSaving
        ) {
            if (isSaving) CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
            else Text("Guardar cambios", fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) { Text("Volver") }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileField(value: String, onValueChange: (String) -> Unit, label: String,
                         keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(value, onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
        singleLine = true, shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = keyboardType))
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(value, onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
        singleLine = true, shape = RoundedCornerShape(12.dp), visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
}
