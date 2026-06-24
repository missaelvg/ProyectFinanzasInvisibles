package com.example.proyectfinanzasinvisibles.ui

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
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.network.postCategorizarGasto
import kotlinx.coroutines.launch

@Composable
fun AnalisisScreen() {
    var resultado by remember { mutableStateOf("Esperando análisis...") }
    val scope = rememberCoroutineScope()

    // Colores consistentes con Dashboard
    val backgroundColor = Color(0xFF0F172A)
    val cardColor = Color(0xFF1E293B)
    val textColor = Color(0xFFF8FAFC)
    val textMuted = Color(0xFF94A3B8)
    val accentBlue = Color(0xFF3B82F6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        Text(
            text = "Asistente IA",
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )

        // Tarjeta de Entrada de Datos (Simulada)
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Última actividad detectada",
                    color = textMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SMS: Compra en OXXO por $45.00 MXN",
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    resultado = "Procesando con Gemini AI..."
                    val respuesta = postCategorizarGasto("Compra en OXXO por \$45.00 MXN")
                    resultado = respuesta
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentBlue)
        ) {
            Text("Analizar con Gemini AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Resultado del Análisis",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Tarjeta de Resultado
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
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
                    color = if (resultado.contains("Procesando")) accentBlue else textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "La IA puede cometer errores. Verifica siempre tus gastos.",
            color = textMuted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
    }
}

@Preview
@Composable
fun AnalisisScreenPreview() {
    MaterialTheme {
        AnalisisScreen()
    }
}
