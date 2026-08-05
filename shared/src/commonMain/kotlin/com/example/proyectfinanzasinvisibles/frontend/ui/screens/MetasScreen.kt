package com.example.proyectfinanzasinvisibles.frontend.ui.screens

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
import com.example.proyectfinanzasinvisibles.backend.data.*
import com.example.proyectfinanzasinvisibles.frontend.ui.components.BounceButton
import com.example.proyectfinanzasinvisibles.frontend.ui.*

@Composable
fun MetasScreen() {
    val viewModel = remember { MetasViewModel() }
    val metas = viewModel.metas
    val isLoading = viewModel.isLoading

    val gastos = GastoDatabase.obtenerGastosLocales()
    val totalHormigaMes = remember(gastos) {
        gastos.filter { (it.categoria == "Hormiga" || it.tipo == "Gasto Hormiga") && it.estado == "Aceptado" }
            .sumOf { it.monto }
    }

    val fondoOscuroBg = Color(0xFF111622)
    val tarjetaGrisGris = Color(0xFF1E293B)
    val textoBlanco = Color.White
    val textoGrisSecundario = Color(0xFF94A3B8)
    val colorProgresoAzul = Color(0xFF3B82F6)

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoOscuroBg)
            .padding(16.dp)
    ) {
        Text(
            text = "Centro de Metas y Ahorro",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textoBlanco,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaGrisGris)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "RESUMEN DE FUGAS (MES)", fontSize = 12.sp, color = textoGrisSecundario, fontWeight = FontWeight.Bold)
                Text(text = "$${totalHormigaMes.toInt()}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = textoBlanco)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val feedback = when {
                    totalHormigaMes < 300 -> "¡Eres un maestro del ahorro! Sigue así."
                    totalHormigaMes < 800 -> "Buen control, pero podrías ahorrar un poco más."
                    else -> "Tus gastos hormiga están altos este mes. ¡Ajusta tu presupuesto!"
                }
                
                Text(text = feedback, fontSize = 14.sp, color = colorProgresoAzul)
            }
        }

        Text(
            text = "Metas Activas",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textoBlanco,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (isLoading) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorProgresoAzul)
            }
        } else if (metas.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No tienes metas creadas.", color = textoGrisSecundario)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(metas) { meta ->
                    MetaCard(
                        meta = meta,
                        progreso = viewModel.calcularPorcentajeProgreso(meta),
                        tarjetaColor = tarjetaGrisGris,
                        textoBlanco = textoBlanco,
                        textoGrisSecundario = textoGrisSecundario,
                        colorProgresoAzul = colorProgresoAzul
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BounceButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorProgresoAzul)
        ) {
            Text("+ Crear nueva meta", fontWeight = FontWeight.Bold, color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showDialog) {
        CreateMetaDialog(
            onDismiss = { showDialog = false },
            onConfirm = { titulo, monto ->
                viewModel.crearMeta(titulo, monto)
                showDialog = false
            }
        )
    }
}

@Composable
fun MetaCard(
    meta: MetaAhorro,
    progreso: Float,
    tarjetaColor: Color,
    textoBlanco: Color,
    textoGrisSecundario: Color,
    colorProgresoAzul: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tarjetaColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "OBJETIVO", fontSize = 10.sp, color = textoGrisSecundario, fontWeight = FontWeight.Bold)
            Text(
                text = meta.titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textoBlanco
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Ahorrado", fontSize = 12.sp, color = textoGrisSecundario)
                    Text(text = "\$${meta.montoAcumulado}", fontSize = 18.sp, color = textoBlanco, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Meta", fontSize = 12.sp, color = textoGrisSecundario)
                    Text(text = "\$${meta.montoObjetivo}", fontSize = 18.sp, color = textoBlanco, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progreso.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = colorProgresoAzul,
                trackColor = Color(0xFF0F172A)
            )

            Text(
                text = "${(progreso * 100).toInt()}% Completado",
                fontSize = 12.sp,
                color = colorProgresoAzul,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider(color = textoGrisSecundario.copy(alpha = 0.1f))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "💡 ${meta.mensajeMotivacional}",
                fontSize = 13.sp,
                color = textoBlanco.copy(alpha = 0.8f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun CreateMetaDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Meta de Ahorro") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("¿Qué quieres ahorrar?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto Objetivo ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val m = monto.toDoubleOrNull() ?: 0.0
                if (titulo.isNotBlank() && m > 0) {
                    onConfirm(titulo, m)
                }
            }) {
                Text("Crear Meta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
