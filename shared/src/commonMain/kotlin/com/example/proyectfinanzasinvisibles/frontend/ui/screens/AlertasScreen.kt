package com.example.proyectfinanzasinvisibles.frontend.ui.screens

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
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.frontend.ui.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun AlertasScreen() {
    val s = LocalStrings.current
    val gastos = GastoDatabase.obtenerGastosLocales()
    val antojosGastos = gastos.filter { it.categoria == "Antojos" || it.categoria == "Café" }
    
    val backgroundColor = Color(0xFF0F1115)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        Text(
            text = s.alerts,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (antojosGastos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = s.noNotifications,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    AlertCard(
                        title = "Alerta de Gasto Hormiga",
                        description = "Has realizado ${antojosGastos.size} gastos en antojos recientemente por un total de $${antojosGastos.sumOf { it.monto }.toInt()}.",
                        color = Color(0xFFFFA292)
                    )
                }
                
                if (gastos.sumOf { it.monto } > 1000) {
                    item {
                        AlertCard(
                            title = "Presupuesto Semanal",
                            description = "Has superado los $1,000 en gastos totales esta semana. Te recomendamos revisar tu historial.",
                            color = Color(0xFFFDE68A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCard(title: String, description: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(4.dp).fillMaxHeight().background(color))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
