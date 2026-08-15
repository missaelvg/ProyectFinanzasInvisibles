package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import kotlinx.datetime.Clock

@Composable
fun AlertasScreen() {
    val s = LocalStrings.current
    val weekStart = Clock.System.now().toEpochMilliseconds() - 7L * 24L * 60L * 60L * 1000L
    val gastos = GastoDatabase.obtenerGastosLocales()
        .filter { it.estado == "Aceptado" && it.fecha >= weekStart }
    val gastosHormiga = gastos.filter { it.categoria == "Hormiga" || it.tipo == "Gasto Hormiga" }
    val totalSemanal = gastos.sumOf { it.monto }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            text = s.alerts,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (gastosHormiga.isEmpty() && totalSemanal <= 1000.0) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = s.noNotifications,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (gastosHormiga.isNotEmpty()) {
                    item {
                        AlertCard(
                            title = "Alerta de gasto hormiga",
                            description = "Registraste ${gastosHormiga.size} gastos hormiga esta semana por $${gastosHormiga.sumOf { it.monto }.toInt()} MXN.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                if (totalSemanal > 1000.0) {
                    item {
                        AlertCard(
                            title = "Presupuesto Semanal",
                            description = "Tus gastos aceptados de los últimos 7 días suman $${totalSemanal.toInt()} MXN. Revisa el historial antes de seguir gastando.",
                            color = MaterialTheme.colorScheme.tertiary
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(4.dp).fillMaxHeight().background(color))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
            }
        }
    }
}
