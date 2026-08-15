package com.example.proyectfinanzasinvisibles.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation as ComposeVisualTransformation
import com.example.proyectfinanzasinvisibles.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalFocusManager
import com.example.proyectfinanzasinvisibles.backend.repositories.AuthRepository
import com.example.proyectfinanzasinvisibles.frontend.ui.theme.StealthMonochromeTheme
import com.example.proyectfinanzasinvisibles.frontend.ui.components.BounceButton
import com.example.proyectfinanzasinvisibles.frontend.ui.LocalStrings
import com.example.proyectfinanzasinvisibles.frontend.ui.Language
import com.example.proyectfinanzasinvisibles.frontend.ui.ProvideStrings
import com.example.proyectfinanzasinvisibles.sensores.rememberLocationProvider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import android.util.Patterns

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val requestLocation = rememberLocationProvider(
        onCityFetched = { ciudad = it },
        onError = { errorMsg = it }
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) requestLocation()
        else errorMsg = "Puedes escribir tu ciudad manualmente si no deseas compartir la ubicación."
    }
    val requestLocationSafely = {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) requestLocation()
        else locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    ProvideStrings(Language.ES) {
        val s = LocalStrings.current
        
        StealthMonochromeTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusManager.clearFocus() },
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo_finanzas),
                        contentDescription = "Logo FI",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(26.dp))
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = s.appName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 2.8.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isLogin) s.login else s.initialize,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!isLogin) {
                            StealthTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = s.name
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StealthTextField(
                                value = apellido,
                                onValueChange = { apellido = it },
                                label = s.surname
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StealthTextField(
                                value = fechaNacimiento,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }
                                    if (digits.length <= 8) {
                                        val isValidInput = when (digits.length) {
                                            1 -> digits[0].digitToInt() <= 3
                                            2 -> digits.toInt() in 1..31
                                            3 -> digits[2].digitToInt() <= 1
                                            4 -> {
                                                val month = digits.substring(2, 4).toInt()
                                                month in 1..12
                                            }
                                            else -> true
                                        }
                                        if (isValidInput) {
                                            fechaNacimiento = digits
                                        }
                                    }
                                },
                                label = s.birthDate,
                                placeholder = "DDMMYYYY",
                                visualTransformation = DateTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StealthTextField(
                                value = ciudad,
                                onValueChange = { ciudad = it },
                                label = s.city,
                                trailingIcon = {
                                    IconButton(onClick = requestLocationSafely) {
                                        Icon(
                                            imageVector = Icons.Default.MyLocation,
                                            contentDescription = "Get location",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        StealthTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = s.email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        StealthTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = s.password,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(
                                        if (passwordVisible) "OCULTAR" else "MOSTRAR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        )

                        if (!isLogin) {
                            Spacer(modifier = Modifier.height(16.dp))
                            StealthTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = s.confirmPassword,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )
                        }

                        errorMsg?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        BounceButton(
                            onClick = {
                                val trimmedEmail = email.trim()
                                val isRegister = !isLogin
                                
                                if (trimmedEmail.isBlank() || password.isBlank()) {
                                    errorMsg = s.fieldsRequired
                                    return@BounceButton
                                }
                                if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                                    errorMsg = "Escribe un correo electrónico válido."
                                    return@BounceButton
                                }
                                
                                if (isRegister) {
                                    if (nombre.isBlank() || apellido.isBlank() || fechaNacimiento.isBlank() || ciudad.isBlank() || confirmPassword.isBlank()) {
                                        errorMsg = s.fieldsRequired
                                        return@BounceButton
                                    }
                                    if (password.length < 6) {
                                        errorMsg = "La contraseña debe tener al menos 6 caracteres."
                                        return@BounceButton
                                    }

                                    if (fechaNacimiento.length == 8) {
                                        val day = fechaNacimiento.substring(0, 2).toIntOrNull() ?: 0
                                        val month = fechaNacimiento.substring(2, 4).toIntOrNull() ?: 0
                                        val year = fechaNacimiento.substring(4, 8).toIntOrNull() ?: 0
                                        
                                        if (!isValidBirthDate(day, month, year)) {
                                            errorMsg = "La fecha no existe. Usa el formato DD/MM/AAAA."
                                            return@BounceButton
                                        }
                                    } else {
                                        errorMsg = "Completa la fecha en formato DD/MM/AAAA."
                                        return@BounceButton
                                    }

                                    if (password != confirmPassword) {
                                        errorMsg = s.passwordsMismatch
                                        return@BounceButton
                                    }
                                }

                                scope.launch {
                                    isLoading = true
                                    errorMsg = null
                                    try {
                                        withTimeout(20000) { 
                                            if (isLogin) {
                                                val result = authRepository.login(trimmedEmail, password)
                                                if (result.isSuccess) {
                                                    onLoginSuccess()
                                                } else {
                                                    errorMsg = result.exceptionOrNull()?.message ?: s.invalidCredentials
                                                }
                                            } else {
                                                val result = authRepository.signUp(trimmedEmail, password, nombre, apellido, fechaNacimiento, ciudad)
                                                
                                                if (result.isSuccess) {
                                                    onLoginSuccess()
                                                } else {
                                                    val exception = result.exceptionOrNull()
                                                    val error = exception?.message ?: ""
                                                    if (exception is FirebaseAuthUserCollisionException || error.contains("already in use", ignoreCase = true)) {
                                                        val loginResult = authRepository.login(trimmedEmail, password)
                                                        if (loginResult.isSuccess) onLoginSuccess()
                                                        else errorMsg = loginResult.exceptionOrNull()?.message ?: s.invalidCredentials
                                                    } else {
                                                        errorMsg = error.ifBlank { s.initializationFailed }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: TimeoutCancellationException) {
                                        errorMsg = "Error de red: El servidor no responde. Revisa tu conexión."
                                    } catch (e: Exception) {
                                        errorMsg = e.message ?: "Error inesperado"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text(
                                    if (isLogin) s.login else s.initialize,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = { isLogin = !isLogin; errorMsg = null }) {
                        Text(
                            text = if (isLogin) s.needAccess else s.alreadyInitialized,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

private fun isValidBirthDate(day: Int, month: Int, year: Int): Boolean {
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    if (year !in 1900..currentYear) return false
    return try {
        java.util.GregorianCalendar(year, month - 1, day).apply {
            isLenient = false
            time
        }
        true
    } catch (_: IllegalArgumentException) {
        false
    }
}

@Composable
fun StealthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    visualTransformation: ComposeVisualTransformation = ComposeVisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(placeholder, color = MaterialTheme.colorScheme.outline)
                }
            },
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

class DateTransformation : ComposeVisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val out = StringBuilder()
        for (i in text.indices) {
            out.append(text[i])
            if (i == 1 || i == 3) out.append("/")
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
