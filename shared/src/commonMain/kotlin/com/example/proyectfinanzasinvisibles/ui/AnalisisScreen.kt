package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AnalisisScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Asistente IA", color = Color.Blue)
        Button(onClick = { /* Llama a postCategorizarGasto() */ }) {
            Text("Analizar último SMS")
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
