package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConfiguracionScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Configuración del Lector", fontWeight = FontWeight.Bold)
        Switch(checked = true, onCheckedChange = { /* Inicia o detiene el Service */ })
        Text("Activar lectura en segundo plano")
    }
}

@Preview
@Composable
fun ConfiguracionScreenPreview() {
    MaterialTheme {
        ConfiguracionScreen()
    }
}