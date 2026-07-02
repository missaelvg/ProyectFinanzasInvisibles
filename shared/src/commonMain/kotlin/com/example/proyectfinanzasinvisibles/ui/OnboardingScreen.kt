package com.example.proyectfinanzasinvisibles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.ui.theme.PrimaryBlue

@Composable
fun OnboardingScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Área del logo principal
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(60.dp).background(Color.White, RoundedCornerShape(8.dp)))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Bienvenido a tu Asistente\nSilencioso",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Automatiza el registro de tus gastos sin esfuerzo. El sistema procesa tu actividad financiera por ti.",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de funcionalidades principales
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(Modifier.size(20.dp).background(PrimaryBlue, CircleShape))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Escucha Inteligente", 
                        color = Color.White, 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Para automatizar tus finanzas, el sistema analiza las notificaciones bancarias. Solo se procesan montos y categorías; la privacidad está garantizada por diseño.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FeatureBullet("Privacidad garantizada localmente")
                FeatureBullet("Categorización automática en tiempo real")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                text = "Activar Asistente", 
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
        ) {
            Text(
                text = "Ver cómo funciona", 
                color = Color.White, 
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Requiere permisos de acceso a notificaciones",
            color = Color.Gray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun FeatureBullet(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(Modifier.size(6.dp).background(PrimaryBlue, CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}
