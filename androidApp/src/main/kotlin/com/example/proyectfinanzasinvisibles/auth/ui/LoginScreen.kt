package com.example.proyectfinanzasinvisibles.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation as ComposeVisualTransformation
import com.example.proyectfinanzasinvisibles.R
import com.example.proyectfinanzasinvisibles.auth.backend.AuthRepository
import com.example.proyectfinanzasinvisibles.ui.theme.InvisibleInsightsTheme
import com.example.proyectfinanzasinvisibles.ui.components.BounceButton
import com.example.proyectfinanzasinvisibles.ui.LocalStrings
import com.example.proyectfinanzasinvisibles.ui.Language
import com.example.proyectfinanzasinvisibles.ui.ProvideStrings
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Use a fixed language for AuthScreen or sync with App level if possible.
    // Here we provide Spanish as default for Auth.
    ProvideStrings(Language.ES) {
        val s = LocalStrings.current
        
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
                    Image(
                        painter = painterResource(id = R.drawable.logo_finanzas),
                        contentDescription = "Logo FI",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 24.dp)
                    )

                    Text(
                        text = s.appName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 4.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isLogin) s.login else s.initialize,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 40.dp)
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
                                onValueChange = { 
                                    if (it.length <= 8) {
                                        fechaNacimiento = it.filter { char -> char.isDigit() }
                                    }
                                },
                                label = s.birthDate,
                                visualTransformation = DateTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                                visualTransformation = PasswordVisualTransformation()
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
                                if (trimmedEmail.isBlank() || password.isBlank()) {
                                    errorMsg = s.fieldsRequired
                                    return@BounceButton
                                }
                                scope.launch {
                                    isLoading = true
                                    if (isLogin) {
                                        val result = authRepository.login(trimmedEmail, password)
                                        if (result.isSuccess) {
                                            onLoginSuccess()
                                        } else {
                                            errorMsg = s.invalidCredentials
                                        }
                                    } else {
                                        if (password != confirmPassword) {
                                            errorMsg = s.passwordsMismatch
                                            isLoading = false
                                            return@launch
                                        }
                                        val result = authRepository.signUp(trimmedEmail, password, nombre, apellido, fechaNacimiento)
                                        if (result.isSuccess) {
                                            // Trigger success and navigate to main app
                                            onLoginSuccess()
                                        } else {
                                            errorMsg = s.initializationFailed
                                        }
                                    }
                                    isLoading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
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
                }
            }
        }
    }
}

@Composable
fun StealthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
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
            modifier = Modifier.fillMaxWidth().background(Color(0xFF090909), RoundedCornerShape(8.dp)),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
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
