package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.backend.ai.GeminiHelper
import com.example.proyectfinanzasinvisibles.backend.repositories.GastoRepository
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.frontend.ui.components.BounceButton
import com.example.proyectfinanzasinvisibles.frontend.ui.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun AnalisisScreen(onNavToHistory: () -> Unit = {}) {
    val s = LocalStrings.current
    var inputText by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf(if (s.language == "Language") "Enter an expense to analyze..." else "Ingresa un gasto para analizar...") }
    val scope = rememberCoroutineScope()
    val geminiHelper = remember { GeminiHelper() }
    val gastoRepository = remember { GastoRepository() }
    
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            text = s.ai,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (s.language == "Language") "SIMULATE BANK NOTIFICATION" else "SIMULAR NOTIFICACIÓN BANCARIA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ej: Pago Starbucks por $120", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BounceButton(
            onClick = {
                if (inputText.isBlank()) return@BounceButton
                scope.launch {
                    try {
                        isLoading = true
                        resultado = if (s.language == "Language") "Analyzing with IA..." else "Analizando con IA..."
                        
                        val nuevoGasto = geminiHelper.clasificarGasto(inputText)
                        
                        // Actualizar localmente de inmediato para mejorar la respuesta visual
                        GastoDatabase.guardarGastoLocal(nuevoGasto)
                        
                        // Timeout de 10s para Firestore
                        val exito = withTimeoutOrNull(10000) {
                            gastoRepository.sincronizarGasto(nuevoGasto)
                        } ?: false
                        
                        if (exito) {
                            resultado = if (s.language == "Language") 
                                "Detected: ${nuevoGasto.tipo}. Redirecting to History..." 
                                else "Detectado: ${nuevoGasto.tipo}. Redirigiendo a Historial..."
                            inputText = ""
                            
                            // Pequeña pausa para que el usuario lea el resultado antes de navegar
                            kotlinx.coroutines.delay(1500)
                            onNavToHistory()
                        } else {
                            resultado = if (s.language == "Language")
                                "Detected locally: ${nuevoGasto.tipo}. Cloud sync failed (Check backend)."
                                else "Detectado localmente: ${nuevoGasto.tipo}. Error al sincronizar (Backend lento o caído)."
                        }
                    } catch (e: Exception) {
                        resultado = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(
                    if (s.language == "Language") "Analyze and Categorize" else "Analizar y Categorizar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (s.language == "Language") "Analysis Result" else "Resultado del Análisis",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resultado,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = if (s.language == "Language") "AI can make mistakes. Always verify your expenses." else "La IA puede cometer errores. Verifica siempre tus gastos.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
    }
}
