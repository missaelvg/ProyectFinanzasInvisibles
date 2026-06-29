package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun DashboardScreen() {
    val gastos by remember { mutableStateOf(GastoDatabase.obtenerGastosLocales()) }
    
    val totalGastado = remember(gastos) { gastos.sumOf { it.monto } }
    val totalHormiga = remember(gastos) { 
        gastos.filter { it.categoria == "Antojos" || it.categoria == "Café" || it.categoria == "General" }
              .sumOf { it.monto } 
    }

    val backgroundColor = Color(0xFF0F1115)
    val cardColor = Color(0xFF1C1F26)
    val accentBlue = Color(0xFF3B82F6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentBlue, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Asistente Silencioso", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Activo", color = accentBlue, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(40.dp).background(Color.Gray.copy(alpha = 0.3f), CircleShape))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("DASHBOARD", color = accentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Resumen de Fugas\nSemanales",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta Gasto Hormiga
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "GASTO HORMIGA", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.size(12.dp).background(accentBlue, RoundedCornerShape(4.dp)))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${totalHormiga.toInt()}", 
                        color = Color.White, 
                        fontSize = 42.sp, 
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = " MXN", 
                        color = Color.Gray, 
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(text = "Acumulado semanal en gastos menores", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { (totalHormiga / 1000f).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = accentBlue,
                    trackColor = Color.Gray.copy(alpha = 0.2f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tarjeta Detección Inteligente
        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(accentBlue, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "DETECCIÓN INTELIGENTE", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Se detectó un posible gasto innecesario de $45 en OXXO",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(text = "hace 10 min", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("¿Es necesario?", color = Color.White, fontSize = 13.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Simulación de Gráfico
        Text("Distribución de Gastos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
             Text("$${totalGastado.toInt()}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}
