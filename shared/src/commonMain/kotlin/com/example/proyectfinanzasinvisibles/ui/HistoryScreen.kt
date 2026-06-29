package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.ui.theme.PrimaryBlue

@Composable
@Preview
fun HistoryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Cabecera del historial
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(24.dp).background(PrimaryBlue, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Historial", 
                    color = Color.White, 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de resumen de procesamiento
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "PROCESADOS HOY", 
                    color = Color.Gray, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "24", 
                    color = Color.White, 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Green))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sistema activo al 99.2%", 
                        color = Color.Gray, 
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recientes", 
                color = Color.White, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Listado de transacciones históricas
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HistoryItem("CARGO COMPRA TD 7382", "$120.00 - Starbucks", "Café", "hace 10m")
            }
            item {
                HistoryItem("PAGO APP *UBER RIDE", "$185.50 - Uber", "Transporte", "hace 1h")
            }
            item {
                HistoryItem("COMERCIO *OX 0291", "$45.00 - OXXO", "Snacks", "hace 3h")
            }
            item {
                HistoryItem("CARGO AMAZON MX *ORDER", "$1,249.00 - Amazon", "Compras", "Ayer")
            }
        }
    }
}

@Composable
fun HistoryItem(label: String, detail: String, category: String, time: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = label, 
                        color = Color.Gray, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = detail, 
                        color = Color.White, 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C2C2E)), 
                    contentAlignment = Alignment.Center
                ) {
                    Box(Modifier.size(10.dp).background(Color.Green, CircleShape))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2C2C2E))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = category, color = Color.White, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = time, color = Color.Gray, fontSize = 12.sp)
                }
                TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                    Text(text = "CORREGIR", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
