package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.network.postCategorizarGasto
import kotlinx.coroutines.launch

@Composable
fun AnalisisScreen() {
    var resultado by remember { mutableStateOf("Esperando análisis...") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Asistente IA Financiero", 
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF3B82F6)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Último mensaje detectado:", fontWeight = FontWeight.Bold)
        Text(
            text = "Compra en OXXO por $45.00 MXN", 
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    resultado = "Analizando con Gemini..."
                    val respuesta = postCategorizarGasto("Compra en OXXO por $45.00 MXN")
                    resultado = respuesta
                }
            }
        ) {
            Text("Analizar con Gemini AI")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Resultado del Análisis:", fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.padding(top = 8.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = Color(0xFFF1F5F9)
            )
        ) {
            Text(
                text = resultado,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF1E293B),
                fontSize = 16.sp
            )
        }
    }
}

@Preview
@Composable
fun AnalisisScreenPreview() {
    MaterialTheme {
        AnalisisScreen()
    }
}
