package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.backend.data.*
import com.example.proyectfinanzasinvisibles.frontend.ui.components.BounceButton
import com.example.proyectfinanzasinvisibles.frontend.ui.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock

@Composable
fun MetasScreen() {
    val s = LocalStrings.current
    val viewModel: MetasViewModel = viewModel()
    
    // Observamos los gastos de forma reactiva
    val gastos = GastoDatabase.gastos
    
    val metas = viewModel.metas
    val isLoading = viewModel.isLoading

    val weekStart = Clock.System.now().toEpochMilliseconds() - 7L * 24L * 60L * 60L * 1000L
    val totalHormigaMes = remember(gastos.size, gastos.map { it.estado }) {
        gastos.filter { (it.categoria == "Hormiga" || it.tipo == "Gasto Hormiga") && it.estado == "Aceptado" && it.fecha >= weekStart }
            .sumOf { it.monto }
    }

    var showDialog by remember { mutableStateOf(false) }
    var metaParaEditar by remember { mutableStateOf<MetaAhorro?>(null) }
    var showApplySavingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = s.goals,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Tarjeta de Gasto Hormiga (Actual)
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = s.weeklyLeak,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "$${totalHormigaMes.toInt()}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (metas.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "REGISTRO DE AHORRO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Agrega solo dinero que realmente apartaste.", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    Button(
                        onClick = { showApplySavingsDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("REGISTRAR", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Text(
            text = "Metas Activas",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        viewModel.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 12.dp))
        }

        if (isLoading) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (metas.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No tienes metas creadas.", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(metas) { meta ->
                    MetaCard(
                        meta = meta,
                        progreso = viewModel.calcularPorcentajeProgreso(meta),
                        onEdit = { metaParaEditar = it },
                        onDelete = { viewModel.eliminarMeta(it.idMeta) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BounceButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(s.createNewGoal, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showDialog) {
        CreateMetaDialog(
            onDismiss = { showDialog = false },
            onConfirm = { titulo, monto ->
                viewModel.crearMeta(titulo, monto)
                showDialog = false
            }
        )
    }

    metaParaEditar?.let { meta ->
        EditMetaDialog(
            meta = meta,
            onDismiss = { metaParaEditar = null },
            onConfirm = { nuevoTitulo, nuevoMonto ->
                viewModel.editarMeta(meta.idMeta, nuevoTitulo, nuevoMonto)
                metaParaEditar = null
            }
        )
    }

    if (showApplySavingsDialog) {
        ApplySavingsDialog(
            metas = metas,
            onDismiss = { showApplySavingsDialog = false },
            onConfirm = { idMeta, monto ->
                viewModel.sumarAhorroAMeta(idMeta, monto)
                showApplySavingsDialog = false
            }
        )
    }
}

@Composable
fun ApplySavingsDialog(
    metas: List<MetaAhorro>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var selectedId by remember { mutableStateOf(metas.firstOrNull()?.idMeta.orEmpty()) }
    var amount by remember { mutableStateOf("") }
    val parsedAmount = amount.replace(',', '.').toDoubleOrNull()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar ahorro") },
        text = {
            Column {
                Text("Elige una meta e indica cuánto dinero apartaste realmente.")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto ahorrado") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(metas) { meta ->
                        TextButton(
                            onClick = { selectedId = meta.idMeta },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(meta.titulo, color = MaterialTheme.colorScheme.onSurface)
                                RadioButton(selected = selectedId == meta.idMeta, onClick = { selectedId = meta.idMeta })
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedId.isNotBlank() && parsedAmount != null && parsedAmount > 0.0,
                onClick = { onConfirm(selectedId, parsedAmount ?: 0.0) }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun MetaCard(
    meta: MetaAhorro,
    progreso: Float,
    onEdit: (MetaAhorro) -> Unit,
    onDelete: (MetaAhorro) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OBJETIVO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                
                Row {
                    IconButton(onClick = { onEdit(meta) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { onDelete(meta) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Text(
                text = meta.titulo,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Ahorrado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text(text = "\$${meta.montoAcumulado}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Meta", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text(text = "\$${meta.montoObjetivo}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progreso.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(2.dp).clip(RoundedCornerShape(1.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "${(progreso * 100).toInt()}% Completado",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Sugerencia: ${meta.mensajeMotivacional}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun EditMetaDialog(
    meta: MetaAhorro,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var titulo by remember { mutableStateOf(meta.titulo) }
    var monto by remember { mutableStateOf(meta.montoObjetivo.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Meta de Ahorro") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("¿Qué quieres ahorrar?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto Objetivo ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val m = monto.toDoubleOrNull() ?: 0.0
                if (titulo.isNotBlank() && m > 0) {
                    onConfirm(titulo, m)
                }
            }) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun CreateMetaDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Meta de Ahorro") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("¿Qué quieres ahorrar?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto Objetivo ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val m = monto.toDoubleOrNull() ?: 0.0
                if (titulo.isNotBlank() && m > 0) {
                    onConfirm(titulo, m)
                }
            }) {
                Text("Crear Meta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
