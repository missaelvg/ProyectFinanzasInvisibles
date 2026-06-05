package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.data.Gasto
import com.example.proyectfinanzasinvisibles.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.network.GastoRepository

@Composable
fun DashboardScreen() {
    val repository = remember { GastoRepository(GastoDatabase) }
    
    // Estado de la lista de gastos
    var gastos by remember { mutableStateOf(GastoDatabase.obtenerGastosLocales()) }
    
    // Cálculo dinámico del Resumen basado en la lista actual
    // Esto hace que el Front-end reaccione a cualquier cambio en los datos
    val totalGastado = remember(gastos) { gastos.sumOf { it.monto } }
    val totalHormiga = remember(gastos) { 
        gastos.filter { it.categoria == "Antojos" || it.categoria == "Café" || it.categoria == "General" }
              .sumOf { it.monto } 
    }

    // Colores
    val backgroundColor = Color(0xFF0F172A)
    val cardColor = Color(0xFF1E293B)
    val textColor = Color(0xFFF8FAFC)
    val textMuted = Color(0xFF94A3B8)
    val dangerRed = Color(0xFFEF4444)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp),
    ) {
        Text(
            text = "Resumen Financiero",
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
        )

        // Tarjeta de Saldo DINÁMICA
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Total Gastado (Semana)", color = textMuted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                // Aquí usamos el valor calculado dinámicamente
                Text(
                    text = "$${totalGastado}", 
                    color = textColor, 
                    fontSize = 36.sp, 
                    fontWeight = FontWeight.ExtraBold,
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Gastos Hormiga Detectados: ", color = textMuted, fontSize = 14.sp)
                    Text(
                        text = "$${totalHormiga}", 
                        color = dangerRed, 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Transacciones Recientes", 
            color = textColor, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.SemiBold,
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Gastos
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(gastos) { gasto ->
                GastoItem(gasto, cardColor, textColor)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Interacción: Al presionar, se actualiza la lista y por ende el resumen
        Button(
            onClick = {
                val nuevoId = gastos.size + 1
                repository.agregarNuevoGasto("Compra #$nuevoId", 50.0)
                gastos = GastoDatabase.obtenerGastosLocales() // Actualiza la lista
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
        ) {
            Text("Simular Intercepción de Gasto", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GastoItem(gasto: Gasto, cardColor: Color, textColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = gasto.descripcion, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = gasto.categoria, color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
            Text(text = "-$${gasto.monto}", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
