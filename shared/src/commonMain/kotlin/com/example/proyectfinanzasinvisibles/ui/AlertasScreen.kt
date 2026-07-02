package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.data.GastoDatabase

@Composable
fun AlertasScreen() {
    val gastos = remember { GastoDatabase.obtenerGastosLocales() }
    val backgroundColor = Color(0xFF0F1115)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        Text(
            text = "Centro de Alertas",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (gastos.isEmpty()) {
            Text(
                text = "No hay notificaciones nuevas en este momento.",
                color = Color.Gray,
                fontSize = 16.sp
            )
        } else {
            // Si hay gastos, podríamos mostrar alertas reales, 
            // pero para seguir la imagen cuando está "limpio":
            Text(
                text = "No hay notificaciones nuevas en este momento.",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}
