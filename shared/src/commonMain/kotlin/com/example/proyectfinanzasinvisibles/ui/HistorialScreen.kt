package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.proyectfinanzasinvisibles.data.Gasto
import com.example.proyectfinanzasinvisibles.data.GastoDatabase

@Composable
fun HistorialScreen() {
    val gastos = remember { GastoDatabase.obtenerGastosLocales() }
    val backgroundColor = Color(0xFF0F1115)
    val cardColor = Color(0xFF1C1F26)
    val accentGreen = Color(0xFF22C55E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(Color(0xFF3B82F6), CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Historial", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(40.dp).background(Color.Gray.copy(alpha = 0.3f), CircleShape))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("PROCESADOS HOY", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("24", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(accentGreen, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sistema activo al 99.2%", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Recientes", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(gastos) { gasto ->
                GastoHistorialItem(gasto, cardColor, accentGreen)
            }
        }
    }
}

@Composable
fun GastoHistorialItem(gasto: Gasto, cardColor: Color, accentGreen: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "CARGO COMPRA TD 7382", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(12.dp).background(accentGreen, CircleShape))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$${gasto.monto} - ${gasto.descripcion}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = gasto.categoria, 
                        color = Color.White, 
                        fontSize = 12.sp, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "hace 10m", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = {}) {
                    Text("CORREGIR", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
