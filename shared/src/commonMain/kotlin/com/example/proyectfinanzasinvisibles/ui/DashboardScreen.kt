package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.ui.theme.PrimaryBlue

@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Cabecera principal con estado del asistente
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).background(PrimaryBlue, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Asistente Silencioso",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Activo",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "DASHBOARD",
            color = PrimaryBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Resumen de Fugas\nSemanales",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta informativa de gastos hormiga
        GastoHormigaCard(monto = 400.0, porcentaje = 0.7f)

        Spacer(modifier = Modifier.height(16.dp))

        // Alerta de detección inteligente
        DeteccionInteligenteCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de distribución de gastos
        DistribucionFugasCard()

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Actividad Reciente",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Listado de transacciones simuladas
        RecentItem("OXXO (Gasto Hormiga)", "-$45.00", "Hoy, 4:30 PM")
        RecentItem("Uber a Casa", "-$120.00", "Ayer, 9:15 PM")
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun GastoHormigaCard(monto: Double, porcentaje: Float) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "GASTO HORMIGA", 
                    color = Color.Gray, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold
                )
                Box(Modifier.size(16.dp).background(PrimaryBlue, RoundedCornerShape(4.dp)))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$${monto.toInt()}", 
                    color = Color.White, 
                    fontSize = 40.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MXN", 
                    color = Color.Gray, 
                    fontSize = 16.sp, 
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Text(
                text = "Acumulado semanal en gastos menores", 
                color = Color.Gray, 
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { porcentaje },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFF2C2C2E)
            )
        }
    }
}

@Composable
fun DeteccionInteligenteCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).background(PrimaryBlue, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DETECCIÓN INTELIGENTE", 
                    color = PrimaryBlue, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Se detectó un posible gasto innecesario de $45 en OXXO",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "hace 10 min", 
                color = Color.Gray, 
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "¿Es necesario?", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DistribucionFugasCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Distribución de Gastos", 
                    color = Color.White, 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DonutChart(
                    modifier = Modifier.size(150.dp),
                    proportions = listOf(0.5f, 0.25f, 0.15f),
                    colors = listOf(PrimaryBlue, Color(0xFFD0F8E2), Color(0xFFFFA292))
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$850", 
                        color = Color.White, 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total", 
                        color = Color.Gray, 
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DistributionItem("Café", "$400 (50%)", PrimaryBlue)
            DistributionItem("Snacks", "$250 (25%)", Color(0xFFD0F8E2))
            DistributionItem("Transporte", "$200 (15%)", Color(0xFFFFA292))
        }
    }
}

@Composable
fun DonutChart(modifier: Modifier, proportions: List<Float>, colors: List<Color>) {
    Canvas(modifier = modifier) {
        var startAngle = -90f
        proportions.forEachIndexed { index, proportion ->
            val sweepAngle = proportion * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun DistributionItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = Color.White, fontSize = 14.sp)
        }
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RecentItem(title: String, amount: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2C2C2E)), 
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(20.dp).background(PrimaryBlue, CircleShape))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title, 
                    color = Color.White, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = time, 
                    color = Color.Gray, 
                    fontSize = 12.sp
                )
            }
        }
        Text(
            text = amount, 
            color = Color(0xFFFFA292), 
            fontSize = 14.sp, 
            fontWeight = FontWeight.Bold
        )
    }
}
