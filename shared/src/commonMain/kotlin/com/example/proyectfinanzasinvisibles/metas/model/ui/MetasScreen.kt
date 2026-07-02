package com.example.proyectfinanzasinvisibles.metas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.metas.viewmodel.MetasViewModel

@Composable
fun MetasScreen() {
    val viewModel = MetasViewModel()
    val meta = viewModel.metaActual
    val progreso = viewModel.calcularPorcentajeProgreso()

    // 🎨 Colores extraídos de la captura de pantalla de tu equipo
    val fondoOscuroBg = Color(0xFF111622)       // Fondo oscuro profundo
    val tarjetaGrisGris = Color(0xFF1E293B)     // Gris azulado de las tarjetas
    val textoBlanco = Color.White
    val textoGrisSecundario = Color(0xFF94A3B8)  // Gris claro para etiquetas secundarias
    val colorProgresoAzul = Color(0xFF3B82F6)    // El azul vibrante del botón inferior
    val rachaFuegoColor = Color(0xFFEF4444)      // Rojo/Naranja para la racha

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoOscuroBg) // Mismo fondo que el resto de la app
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la sección adaptado a su estilo
        Text(
            text = "Metas de Ahorro",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textoBlanco,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 20.dp, start = 4.dp)
        )

        // Tarjeta Principal de la Meta de Ahorro (Mismo estilo que "Total Gastado")
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaGrisGris)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Meta Activa Reto", fontSize = 13.sp, color = textoGrisSecundario)
                Text(
                    text = meta.titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textoBlanco,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Acumulado", fontSize = 12.sp, color = textoGrisSecundario)
                        Text(text = "\$${meta.montoAcumulado}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textoBlanco)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Objetivo", fontSize = 12.sp, color = textoGrisSecundario)
                        Text(text = "\$${meta.montoObjetivo}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = textoBlanco)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de progreso estilizada
                LinearProgressIndicator(
                    progress = progreso,
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = colorProgresoAzul,
                    trackColor = fondoOscuroBg
                )

                Text(
                    text = "${(progreso * 100).toInt()}% Completado",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.End).padding(top = 6.dp),
                    color = colorProgresoAzul
                )
            }
        }

        // Tarjeta de Rachas (Mismo estilo visual)
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaGrisGris)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔥", fontSize = 28.sp)
                    Text(text = "Racha Actual", fontSize = 12.sp, color = textoGrisSecundario)
                    Text(text = "${meta.rachaActualDias} días", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = rachaFuegoColor)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏆", fontSize = 28.sp)
                    Text(text = "Mejor Racha", fontSize = 12.sp, color = textoGrisSecundario)
                    Text(text = "${meta.mejorRachaDias} días", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textoBlanco)
                }
            }
        }

        // Tarjeta de Mensaje Motivacional / Sugerencia de la IA
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = tarjetaGrisGris)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💡 Sugerencia de Finanzas Invisibles",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorProgresoAzul
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = meta.mensajeMotivacional,
                    fontSize = 13.sp,
                    color = textoBlanco
                )
            }
        }
    }
}