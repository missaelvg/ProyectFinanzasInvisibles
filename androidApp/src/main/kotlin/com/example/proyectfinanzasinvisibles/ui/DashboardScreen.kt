package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen() {
    // Colores Formales y Neutros
    val backgroundColor = Color(0xFF0F172A) // Slate 900
    val cardColor = Color(0xFF1E293B) // Slate 800
    val textColor = Color(0xFFF8FAFC) // Slate 50
    val textMuted = Color(0xFF94A3B8) // Slate 400
    val accentBlue = Color(0xFF3B82F6) // Blue 500
    val dangerRed = Color(0xFFEF4444) // Red 500

    val gastosRecientes = listOf(
        Pair("Oxxo - Antojos", "-$45.00"),
        Pair("Starbucks", "-$120.00"),
        Pair("Uber - Viaje", "-$85.00")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        // Encabezado Formal
        Text(
            text = "Resumen Financiero",
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )

        // Tarjeta Principal de Saldo
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Total Gastado (Semana)", color = textMuted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$1,240.00", color = textColor, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Gastos Hormiga Detectados: ", color = textMuted, fontSize = 14.sp)
                    Text(text = "$350.00", color = dangerRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Lista de Gastos Recientes
        Text(text = "Transacciones Recientes", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(gastosRecientes) { gasto ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = gasto.first, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(text = gasto.second, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}