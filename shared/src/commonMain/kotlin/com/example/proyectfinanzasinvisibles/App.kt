package com.example.proyectfinanzasinvisibles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.ui.DashboardScreen
import com.example.proyectfinanzasinvisibles.ui.AnalisisScreen
import com.example.proyectfinanzasinvisibles.ui.ConfiguracionScreen

@Composable
@Preview
fun App() {
    var screen by remember { mutableStateOf("dashboard") }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = screen == "dashboard",
                        onClick = { screen = "dashboard" },
                        label = { Text("Dashboard") },
                        icon = { /* Icono Dashboard */ }
                    )
                    NavigationBarItem(
                        selected = screen == "analisis",
                        onClick = { screen = "analisis" },
                        label = { Text("IA Análisis") },
                        icon = { /* Icono IA */ }
                    )
                    NavigationBarItem(
                        selected = screen == "configuracion",
                        onClick = { screen = "configuracion" },
                        label = { Text("Config") },
                        icon = { /* Icono Config */ }
                    )
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (screen) {
                    "dashboard" -> DashboardScreen()
                    "analisis" -> AnalisisScreen()
                    "configuracion" -> ConfiguracionScreen()
                }
            }
        }
    }
}
