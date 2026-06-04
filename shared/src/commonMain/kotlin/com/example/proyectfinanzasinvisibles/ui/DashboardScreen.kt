package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.layout.Arrangement
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
    // Inicialización simple de dependencias (En una app real usaríamos DI)
    val database = remember { GastoDatabase() }
    val repository = remember { GastoRepository(database) }
    
    // Estados de la UI
    var gastos by remember { mutableStateOf(database.obtenerGastosLocales()) }
    var resumen by remember { mutableStateOf(mapOf("total_semana" to 0.0, "gastos_hormiga" to 0.0)) }

    // Simulación de carga de datos (Consumo de API y Local)
    LaunchedEffect(Unit) {
        resumen = repository.fetchResumenDesdeAPI()
        gastos = repository.sincronizarYObtenerGastos()
    }

    // Colores Formales
    val backgroundColor = Color(0xFF0F172A)
    val cardColor = Color(0xFF1E293B)
    val textColor = Color(0xFFF8FAFC)
    val textMuted = Color(0xFF94A3B8)
    val dangerRed = Color(0xFFEF4444)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        Text(
            text = "Resumen Financiero",
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )

        // Tarjeta Principal (Datos que vendrían de la API)
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Total Gastado (Semana)", color = textMuted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${resumen["total_semana"]}", 
                    color = textColor, 
                    fontSize = 36.sp, 
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Gastos Hormiga Detectados: ", color = textMuted, fontSize = 14.sp)
                    Text(
                        text = "$${resumen["gastos_hormiga"]}", 
                        color = dangerRed, 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Transacciones Locales", 
            color = textColor, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Gastos (Almacenamiento Local)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(gastos) { gasto ->
                GastoItem(gasto, cardColor, textColor)
            }
        }
        
        // Botón para simular interacción Front -> Back -> Local
        Button(
            onClick = {
                repository.agregarNuevoGasto("Nueva Compra", 50.0)
                gastos = database.obtenerGastosLocales().toList() // Refrescar lista
            },
            modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
        ) {
            Text("Simular Intercepción de Gasto")
        }
    }
}

@Composable
fun GastoItem(gasto: Gasto, cardColor: Color, textColor: Color) {
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
            Text(text = gasto.descripcion, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = "-$${gasto.monto}", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
