package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.ui.theme.PrimaryBlue

@Composable
fun GoalsScreen() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Cabecera de metas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(24.dp).background(PrimaryBlue, CircleShape))
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

        // Tarjeta de resumen mensual de ahorros
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "RESUMEN MENSUAL", 
                    color = Color.Gray, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "-$1,200", 
                    color = Color.White, 
                    fontSize = 40.sp, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Has evitado $1,200 en fugas este mes. El sistema está optimizando tus gastos.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = PrimaryBlue,
                    trackColor = Color(0xFF2C2C2E)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Retos Activos", 
                color = Color.White, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "VER TODOS", 
                color = PrimaryBlue, 
                fontSize = 10.sp, 
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta de progreso de retos específicos
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 0.66f },
                        color = PrimaryBlue,
                        trackColor = Color(0xFF2C2C2E),
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "2/3", 
                        color = Color.White, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "3 días sin gastos hormiga", 
                        color = Color.White, 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Meta: $90 proyectados", 
                        color = Color.Gray, 
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Perspectiva de Ahorro", 
            color = Color.White, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Gastos menores semanales", 
                    color = Color.Gray, 
                    fontSize = 12.sp
                )
                Text(
                    text = "$350", 
                    color = Color(0xFFFFA292), 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue), 
                        contentAlignment = Alignment.Center
                    ) {
                        Box(Modifier.size(20.dp).background(Color.White, CircleShape))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Equivalencia en servicios", 
                            color = Color.White, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Impacto directo en presupuesto", 
                            color = Color.Gray, 
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "\"Pequeños cambios constantes generan resultados visibles.\"",
            color = Color(0xFFD0F8E2),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Metas de Ahorro", 
            color = Color.White, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SavingGoalCard(modifier = Modifier.weight(1f), title = "Viaje Japón", amount = "$45,000", progress = 0.3f)
            SavingGoalCard(modifier = Modifier.weight(1f), title = "MacBook M3", amount = "$32,000", progress = 0.7f)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                text = "+ Crear nueva meta", 
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SavingGoalCard(modifier: Modifier, title: String, amount: String, progress: Float) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(Modifier.size(16.dp).background(PrimaryBlue, CircleShape))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = title, color = Color.Gray, fontSize = 12.sp)
            Text(text = amount, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFF2C2C2E)
            )
        }
    }
}
