package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.backend.repositories.GastoRepository
import com.example.proyectfinanzasinvisibles.frontend.ui.LocalStrings
import kotlinx.coroutines.launch
import kotlin.math.round

@Composable
fun HistoryScreen() {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()
    val repository = remember { GastoRepository() }
    val pendientes = GastoDatabase.gastos
        .filter { it.estado == "Pendiente" }
        .sortedByDescending { it.fecha }
    var gastoParaEditar by remember { mutableStateOf<Gasto?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    fun updateStatus(gasto: Gasto, status: String) {
        GastoDatabase.actualizarGasto(gasto.copy(estado = status, sincronizado = false))
        scope.launch {
            val success = repository.actualizarEstadoGasto(gasto.id, status)
            if (success) GastoDatabase.marcarSincronizado(gasto.id)
            else message = "El cambio quedó guardado en el dispositivo y se sincronizará cuando haya conexión."
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(s.history, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(24.dp))
        Text("GASTOS POR REVISAR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        message?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(pendientes, key = { it.id }) { gasto ->
                PendingExpenseCard(
                    gasto = gasto,
                    onAceptar = { updateStatus(gasto, "Aceptado") },
                    onRechazar = { updateStatus(gasto, "Rechazado") },
                    onEditar = { gastoParaEditar = gasto }
                )
            }
            if (pendientes.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No tienes gastos pendientes", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    gastoParaEditar?.let { gasto ->
        EditExpenseDialog(
            gasto = gasto,
            onDismiss = { gastoParaEditar = null },
            onConfirm = { edited ->
                GastoDatabase.actualizarGasto(edited.copy(sincronizado = false))
                gastoParaEditar = null
                scope.launch {
                    val success = repository.actualizarGasto(edited)
                    if (success) GastoDatabase.marcarSincronizado(edited.id)
                    else message = "La edición quedó guardada localmente; falta sincronizarla."
                }
            }
        )
    }
}

@Composable
fun PendingExpenseCard(
    gasto: Gasto,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    onEditar: () -> Unit
) {
    val isHormiga = gasto.tipo == "Gasto Hormiga"
    val badgeColor = if (isHormiga) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Surface(color = badgeColor.copy(alpha = 0.14f), shape = RoundedCornerShape(10.dp)) {
                        Text(gasto.tipo.uppercase(), color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(gasto.descripcion, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(gasto.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                Text("$${gasto.monto.toMoney()}", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("EDITAR")
                }
                TextButton(onClick = onRechazar, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Close, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("DESCARTAR")
                }
                Spacer(Modifier.width(4.dp))
                Button(onClick = onAceptar, shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Default.Check, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("ACEPTAR")
                }
            }
        }
    }
}

private fun Double.toMoney(): String = (round(this * 100.0) / 100.0).toString()

@Composable
private fun EditExpenseDialog(gasto: Gasto, onDismiss: () -> Unit, onConfirm: (Gasto) -> Unit) {
    var description by remember { mutableStateOf(gasto.descripcion) }
    var amount by remember { mutableStateOf(gasto.monto.toString()) }
    var category by remember { mutableStateOf(gasto.categoria) }
    val validAmount = amount.replace(',', '.').toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Corregir gasto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(description, { description = it }, label = { Text("Descripción") }, singleLine = true)
                OutlinedTextField(amount, { amount = it }, label = { Text("Monto") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(category, { category = it }, label = { Text("Categoría") }, singleLine = true,
                    supportingText = { Text("Usa Fijo, Hormiga o Variable") })
            }
        },
        confirmButton = {
            Button(
                enabled = description.isNotBlank() && category.isNotBlank() && validAmount != null && validAmount > 0,
                onClick = {
                    val cleanCategory = category.trim().replaceFirstChar { it.uppercase() }
                    onConfirm(gasto.copy(
                        descripcion = description.trim(),
                        monto = validAmount ?: gasto.monto,
                        categoria = cleanCategory,
                        tipo = if (cleanCategory.equals("Hormiga", true)) "Gasto Hormiga" else "Gasto Normal"
                    ))
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
