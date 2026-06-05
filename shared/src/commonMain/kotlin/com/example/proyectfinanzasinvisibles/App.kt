package com.example.proyectfinanzasinvisibles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.ui.ConfiguracionScreen
import org.jetbrains.compose.resources.painterResource

import proyectfinanzasinvisibles.shared.generated.resources.Res
import proyectfinanzasinvisibles.shared.generated.resources.compose_multiplatform

enum class Screen {
    Home, Configuration
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Home) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Home,
                        onClick = { currentScreen = Screen.Home },
                        icon = { Text("🏠") },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Configuration,
                        onClick = { currentScreen = Screen.Configuration },
                        icon = { Text("⚙️") },
                        label = { Text("Config") }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (currentScreen) {
                    Screen.Home -> HomeScreen()
                    Screen.Configuration -> ConfiguracionScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var showContent by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }
    }
}
