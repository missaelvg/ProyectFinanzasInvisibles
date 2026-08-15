package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectfinanzasinvisibles.backend.ai.GeminiApi
import com.example.proyectfinanzasinvisibles.backend.data.MetaAhorro
import com.example.proyectfinanzasinvisibles.backend.repositories.MetaRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class MetasViewModel : ViewModel() {
    private val repository = MetaRepository()
    private val geminiApi = GeminiApi()

    var metas by mutableStateOf<List<MetaAhorro>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        cargarMetas()
    }

    fun cargarMetas() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            runCatching { repository.obtenerMetas() }
                .onSuccess { metas = it }
                .onFailure { errorMessage = "No fue posible cargar las metas." }
            isLoading = false
        }
    }

    fun calcularPorcentajeProgreso(meta: MetaAhorro): Float =
        if (meta.montoObjetivo <= 0.0) 0f
        else (meta.montoAcumulado / meta.montoObjetivo).toFloat().coerceIn(0f, 1f)

    fun crearMeta(titulo: String, objetivo: Double) {
        if (titulo.isBlank() || objetivo <= 0.0) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val prompt = "Da una recomendación breve y concreta para ahorrar $objetivo MXN para la meta: $titulo"
            val recommendation = geminiApi.analizarTexto(prompt).getOrNull()
                ?.trim()?.takeIf { it.isNotBlank() }?.take(180)
                ?: "Aparta una cantidad fija cada semana y registra aquí cada depósito."
            val meta = MetaAhorro(
                idMeta = "meta-${Clock.System.now().toEpochMilliseconds()}",
                titulo = titulo.trim(),
                montoObjetivo = objetivo,
                montoAcumulado = 0.0,
                rachaActualDias = 0,
                mejorRachaDias = 0,
                mensajeMotivacional = recommendation
            )
            if (repository.guardarMeta(meta)) cargarMetas()
            else errorMessage = "No fue posible guardar la meta."
            isLoading = false
        }
    }

    fun eliminarMeta(id: String) {
        viewModelScope.launch {
            if (repository.eliminarMeta(id)) cargarMetas()
            else errorMessage = "No fue posible eliminar la meta."
        }
    }

    fun editarMeta(id: String, titulo: String, objetivo: Double) {
        viewModelScope.launch {
            if (repository.editarMeta(id, titulo.trim(), objetivo)) cargarMetas()
            else errorMessage = "No fue posible editar la meta."
        }
    }

    fun sumarAhorroAMeta(idMeta: String, monto: Double) {
        val meta = metas.firstOrNull { it.idMeta == idMeta } ?: return
        if (monto <= 0.0) return
        viewModelScope.launch {
            isLoading = true
            val nuevoAcumulado = (meta.montoAcumulado + monto).coerceAtMost(meta.montoObjetivo)
            if (repository.actualizarProgresoMeta(idMeta, nuevoAcumulado)) cargarMetas()
            else errorMessage = "No fue posible registrar el ahorro."
            isLoading = false
        }
    }
}
