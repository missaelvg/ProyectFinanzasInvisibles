package com.example.proyectfinanzasinvisibles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.proyectfinanzasinvisibles.ui.AnalisisScreen
import com.example.proyectfinanzasinvisibles.ui.theme.InvisibleInsightsTheme

enum class Screen {
    Onboarding, Home, History, Alerts, Goals, Analisis, Profile
}

@Composable
@Preview
fun App(onLogout: () -> Unit = {}) {
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
                            selected = currentScreen == Screen.Goals,
                            onClick = { currentScreen = Screen.Goals },
                            icon = { Box(Modifier.size(24.dp).background(if (currentScreen == Screen.Goals) MaterialTheme.colorScheme.primary else Color.Gray, shape = MaterialTheme.shapes.small)) },
                            label = { Text("Metas") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Analisis,
                            onClick = { currentScreen = Screen.Analisis },
                            icon = {
                                Box(
                                    Modifier
                                        .size(24.dp)
                                        .background(
                                            if (currentScreen == Screen.Analisis)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                Color.Gray,
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                            },
                            label = { Text("IA") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Profile,
                            onClick = { currentScreen = Screen.Profile },
                            icon = { Box(Modifier.size(24.dp).background(if (currentScreen == Screen.Profile) MaterialTheme.colorScheme.primary else Color.Gray, shape = MaterialTheme.shapes.small)) },
                            label = { Text("Perfil") }
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
                        Screen.Analisis -> AnalisisScreen()
                        Screen.Profile -> ProfileScreen(onLogout = onLogout)
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
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
