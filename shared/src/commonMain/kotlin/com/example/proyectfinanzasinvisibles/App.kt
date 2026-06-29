package com.example.proyectfinanzasinvisibles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.ui.*

enum class Screen {
    Home, History, Alerts, Goals
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Home) }

        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1C1F26), contentColor = Color.White) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Home,
                        onClick = { currentScreen = Screen.Home },
                        icon = { Text("🏠") },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.History,
                        onClick = { currentScreen = Screen.History },
                        icon = { Text("📜") },
                        label = { Text("Historial") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Alerts,
                        onClick = { currentScreen = Screen.Alerts },
                        icon = { Text("🔔") },
                        label = { Text("Alertas") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Goals,
                        onClick = { currentScreen = Screen.Goals },
                        icon = { Text("🎯") },
                        label = { Text("Metas") }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(Color(0xFF0F1115))
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (currentScreen) {
                    Screen.Home -> DashboardScreen() 
                    Screen.History -> HistorialScreen()
                    Screen.Alerts -> AlertasScreen()
                    Screen.Goals -> AnalisisScreen()
                }
            }
        }
    }
}
