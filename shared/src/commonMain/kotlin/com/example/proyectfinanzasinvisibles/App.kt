package com.example.proyectfinanzasinvisibles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectfinanzasinvisibles.ui.*
import com.example.proyectfinanzasinvisibles.ui.theme.InvisibleInsightsTheme
import com.example.proyectfinanzasinvisibles.ui.components.BounceButton
import com.example.proyectfinanzasinvisibles.profile.ui.EditProfileScreen

enum class Screen {
    Onboarding, Home, History, Alerts, Goals, Analisis, Settings, EditProfile
}

@Composable
@Preview
fun App(onLogout: () -> Unit = {}) {
    var language by remember { mutableStateOf(Language.ES) }
    
    ProvideStrings(language) {
        val s = LocalStrings.current
        
        InvisibleInsightsTheme {
            var currentScreen by remember { mutableStateOf(Screen.Home) }

            if (currentScreen == Screen.Onboarding) {
                OnboardingScreen(onStartClick = { currentScreen = Screen.Home })
            } else if (currentScreen == Screen.EditProfile) {
                EditProfileScreen(onBack = { currentScreen = Screen.Settings })
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 0.dp
                        ) {
                            val navItems = listOf(
                                Triple(Screen.Home, s.home, Icons.Default.Home),
                                Triple(Screen.History, s.history, Icons.Default.History),
                                Triple(Screen.Goals, s.goals, Icons.Default.TrackChanges),
                                Triple(Screen.Analisis, s.ai, Icons.Default.AutoAwesome),
                                Triple(Screen.Settings, s.settings, Icons.Default.Settings)
                            )
                            
                            navItems.forEach { (screen, label, icon) ->
                                NavigationBarItem(
                                    selected = currentScreen == screen,
                                    onClick = { currentScreen = screen },
                                    icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.outline,
                                        unselectedTextColor = MaterialTheme.colorScheme.outline,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
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
                            Screen.Settings -> SettingsScreen(
                                onLogout = onLogout,
                                onEditProfile = { currentScreen = Screen.EditProfile },
                                currentLanguage = language,
                                onLanguageChange = { language = it }
                            )
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    currentLanguage: Language,
    onLanguageChange: (Language) -> Unit
) {
    val s = LocalStrings.current
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = s.settings,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = s.manageAccount,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(40.dp))

        // Language Selection
        Text(
            text = s.language,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LanguageButton(
                label = "Español",
                isSelected = currentLanguage == Language.ES,
                onClick = { onLanguageChange(Language.ES) },
                modifier = Modifier.weight(1f)
            )
            LanguageButton(
                label = "English",
                isSelected = currentLanguage == Language.EN,
                onClick = { onLanguageChange(Language.EN) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        BounceButton(
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(s.editProfile, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        BounceButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(s.logout, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LanguageButton(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BounceButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AlertsScreen() {
    val s = LocalStrings.current
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = s.alerts,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = s.controlCenter,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = s.noNotifications,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
