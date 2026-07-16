package com.example.proyectfinanzasinvisibles.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import proyectfinanzasinvisibles.shared.generated.resources.Res
import proyectfinanzasinvisibles.shared.generated.resources.logo_finanzas

@Composable
fun BounceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: androidx.compose.ui.graphics.Shape = ButtonDefaults.shape,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scope = rememberCoroutineScope()
    var isClickEnabled by remember { mutableStateOf(true) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f)
    )

    Button(
        onClick = {
            if (isClickEnabled) {
                isClickEnabled = false
                onClick()
                scope.launch {
                    delay(500) // Debounce
                    isClickEnabled = true
                }
            }
        },
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale),
        enabled = enabled && isClickEnabled,
        colors = colors,
        shape = shape,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun BounceOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    border: androidx.compose.foundation.BorderStroke? = ButtonDefaults.outlinedButtonBorder,
    shape: androidx.compose.ui.graphics.Shape = ButtonDefaults.outlinedShape,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scope = rememberCoroutineScope()
    var isClickEnabled by remember { mutableStateOf(true) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f)
    )

    OutlinedButton(
        onClick = {
            if (isClickEnabled) {
                isClickEnabled = false
                onClick()
                scope.launch {
                    delay(500)
                    isClickEnabled = true
                }
            }
        },
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale),
        enabled = enabled && isClickEnabled,
        border = border,
        shape = shape,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun LoadingScreen(messages: List<String> = listOf("Cargando todo...", "Casi listo...", "Preparando tus finanzas...", "Analizando datos...")) {
    var currentMessageIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        val messageJob = launch {
            while(true) {
                delay(1500)
                currentMessageIndex = (currentMessageIndex + 1) % messages.size
            }
        }
        val progressJob = launch {
            while(progress < 1f) {
                delay(20)
                progress += 0.01f
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.logo_finanzas),
                contentDescription = "Logo FI",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp)
            )

            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = messages[currentMessageIndex],
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
