package com.example.proyectfinanzasinvisibles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.ui.DashboardScreen
import com.example.proyectfinanzasinvisibles.ui.HistoryScreen
import com.example.proyectfinanzasinvisibles.ui.GoalsScreen
import com.example.proyectfinanzasinvisibles.ui.OnboardingScreen
import com.example.proyectfinanzasinvisibles.ui.theme.InvisibleInsightsTheme

enum class Screen {
    Onboarding, Home, History, Alerts, Goals
}

@Composable
@Preview
fun App() {
    InvisibleInsightsTheme {
        var currentScreen by remember { mutableStateOf(Screen.Onboarding) }

        if (currentScreen == Screen.Onboarding) {
            OnboardingScreen(onStartClick = { currentScreen = Screen.Home })
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == Screen.Home,
                            onClick = { currentScreen = Screen.Home },
                            icon = { Box(Modifier.size(24.dp).background(if (currentScreen == Screen.Home) MaterialTheme.colorScheme.primary else Color.Gray, shape = MaterialTheme.shapes.small)) },
                            label = { Text("Inicio") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.History,
                            onClick = { currentScreen = Screen.History },
                            icon = { Box(Modifier.size(24.dp).background(if (currentScreen == Screen.History) MaterialTheme.colorScheme.primary else Color.Gray, shape = MaterialTheme.shapes.small)) },
                            label = { Text("Historial") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Alerts,
                            onClick = { currentScreen = Screen.Alerts },
                            icon = { Box(Modifier.size(24.dp).background(if (currentScreen == Screen.Alerts) MaterialTheme.colorScheme.primary else Color.Gray, shape = MaterialTheme.shapes.small)) },
                            label = { Text("Alertas") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Goals,
                            onClick = { currentScreen = Screen.Goals },
                            icon = { Box(Modifier.size(24.dp).background(if (currentScreen == Screen.Goals) MaterialTheme.colorScheme.primary else Color.Gray, shape = MaterialTheme.shapes.small)) },
                            label = { Text("Metas") }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                ) {
                    when (currentScreen) {
                        Screen.Home -> DashboardScreen()
                        Screen.History -> HistoryScreen()
                        Screen.Alerts -> AlertsScreen()
                        Screen.Goals -> GoalsScreen()
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun AlertsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Centro de Alertas",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay notificaciones nuevas en este momento.",
            color = Color.Gray
        )
    }
}
