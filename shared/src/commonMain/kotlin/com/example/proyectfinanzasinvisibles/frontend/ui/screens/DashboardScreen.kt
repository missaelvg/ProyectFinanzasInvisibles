package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import proyectfinanzasinvisibles.shared.generated.resources.Res
import proyectfinanzasinvisibles.shared.generated.resources.logo_finanzas
import com.example.proyectfinanzasinvisibles.frontend.ui.components.BounceButton
import com.example.proyectfinanzasinvisibles.frontend.ui.*
import com.example.proyectfinanzasinvisibles.backend.data.*
import com.example.proyectfinanzasinvisibles.backend.repositories.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Composable
fun DashboardScreen(onOpenAlerts: () -> Unit = {}) {
    val s = LocalStrings.current
    val authRepository = remember { AuthRepository() }
    val gastoRepository = remember { GastoRepository() }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    val gastos = GastoDatabase.gastos
    var streak by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        streak = authRepository.actualizarRacha()
        if (streak == 0) {
            val profile = authRepository.getUserProfile()
            streak = (profile.getOrNull()?.get("racha") as? Number)?.toInt() ?: 0
        }
    }

    val weekStart = Clock.System.now().toEpochMilliseconds() - 7L * 24L * 60L * 60L * 1000L
    val gastosSemanales = gastos.filter { it.estado == "Aceptado" && it.fecha >= weekStart }
    val totalGastado = gastosSemanales.sumOf { it.monto }
    val totalHormiga = gastosSemanales
        .filter { it.categoria == "Hormiga" || it.tipo == "Gasto Hormiga" }
            .sumOf { it.monto }

    val gastoPendiente = gastos.firstOrNull { it.estado == "Pendiente" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.logo_finanzas),
                    contentDescription = "Logo FI",
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = s.silentAssistant,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = s.activeNow,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            IconButton(onClick = onOpenAlerts) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Abrir alertas",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = s.dashboard,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            letterSpacing = 2.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = s.weeklyLeak,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 48.sp,
                modifier = Modifier.weight(1f)
            )
            
            if (streak > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥", fontSize = 24.sp)
                    Text(
                        text = "$streak DÍAS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GastoHormigaCard(
            monto = totalHormiga,
            porcentaje = (totalHormiga / 1000f).toFloat().coerceIn(0f, 1f)
        )

        gastoPendiente?.let { gasto ->
            Spacer(modifier = Modifier.height(16.dp))
            DeteccionInteligenteCard(
                gasto = gasto,
                onAction = { nuevoEstado ->
                    scope.launch {
                        GastoDatabase.actualizarGasto(gasto.copy(estado = nuevoEstado, sincronizado = false))
                        val exito = gastoRepository.actualizarEstadoGasto(gasto.id, nuevoEstado)
                        if (exito) GastoDatabase.marcarSincronizado(gasto.id)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DistribucionFugasCard(total = totalGastado, gastos = gastosSemanales)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = s.recentActivity,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        gastos.sortedByDescending { it.fecha }.take(5).forEach { gasto ->
            RecentItem(
                title = gasto.descripcion,
                category = gasto.categoria,
                amount = "-$${gasto.monto.toInt()}",
                time = relativeTime(gasto.fecha)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

private fun relativeTime(timestamp: Long): String {
    val elapsedMinutes = ((Clock.System.now().toEpochMilliseconds() - timestamp).coerceAtLeast(0L) / 60_000L)
    return when {
        elapsedMinutes < 1 -> "Ahora"
        elapsedMinutes < 60 -> "Hace $elapsedMinutes min"
        elapsedMinutes < 1_440 -> "Hace ${elapsedMinutes / 60} h"
        else -> "Hace ${elapsedMinutes / 1_440} días"
    }
}

@Composable
fun GastoHormigaCard(monto: Double, porcentaje: Float) {
    val s = LocalStrings.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = s.stealthLeak,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$${monto.toInt()}",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "MXN",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = s.accumulatedWeekly,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { porcentaje.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun DeteccionInteligenteCard(gasto: Gasto, onAction: (String) -> Unit) {
    val s = LocalStrings.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = s.intelligentDetection,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Se detectó: ${gasto.descripcion} por $${gasto.monto.toInt()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "¿Es un gasto necesario o una fuga?",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BounceButton(
                    onClick = { onAction("Aceptado") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp).weight(1f)
                ) {
                    Text(
                        text = "ES FUGA",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                BounceButton(
                    onClick = { onAction("Rechazado") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp).weight(1f)
                ) {
                    Text(
                        text = "IGNORAR",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun DistribucionFugasCard(total: Double, gastos: List<Gasto>) {
    val s = LocalStrings.current
    val categorias = gastos.filter { it.estado == "Aceptado" }.groupBy { it.categoria }
    val proportions = if (total > 0) {
        categorias.map { (it.value.sumOf { g -> g.monto } / total).toFloat() }
    } else listOf(1f)
    
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.outlineVariant, Color.DarkGray)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = s.spendingDistribution,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    modifier = Modifier.size(160.dp),
                    proportions = proportions,
                    colors = colors
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$${total.toInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = s.total,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            categorias.entries.take(5).forEachIndexed { index, entry ->
                DistributionItem(entry.key, "$${entry.value.sumOf { it.monto }.toInt()}", colors.getOrElse(index) { Color.Gray })
            }
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
                color = colors.getOrElse(index) { Color.Gray },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun DistributionItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = value, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RecentItem(title: String, category: String, amount: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(16.dp).background(MaterialTheme.colorScheme.outline, CircleShape))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Text(text = category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(text = amount, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(text = time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}
