package com.example.proyectfinanzasinvisibles.frontend.ui.screens

import com.example.proyectfinanzasinvisibles.backend.repositories.MetaRepository
import com.example.proyectfinanzasinvisibles.backend.data.MetaAhorro
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MetasViewModel {
    private val repository = MetaRepository()
    private val scope = CoroutineScope(Dispatchers.Main)
    
    var metas by mutableStateOf<List<MetaAhorro>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set

    init {
        cargarMetas()
    }

    fun cargarMetas() {
        scope.launch {
            isLoading = true
            metas = repository.obtenerMetas()
            isLoading = false
        }
    }

    fun calcularPorcentajeProgreso(meta: MetaAhorro): Float {
        if (meta.montoObjetivo == 0.0) return 0f
        return (meta.montoAcumulado / meta.montoObjetivo).toFloat()
    }

    fun crearMeta(titulo: String, objetivo: Double) {
        val nuevaMeta = MetaAhorro(
            idMeta = "",
            titulo = titulo,
            montoObjetivo = objetivo,
            montoAcumulado = 0.0,
            rachaActualDias = 0,
            mejorRachaDias = 0,
            mensajeMotivacional = "¡Nueva meta creada! Empieza a ahorrar hoy."
        )
        scope.launch {
            val exito = repository.guardarMeta(nuevaMeta)
            if (exito) cargarMetas()
        }
    }

    fun eliminarMeta(id: String) {
        scope.launch {
            val exito = repository.eliminarMeta(id)
            if (exito) cargarMetas()
        }
    }

    fun editarMeta(id: String, titulo: String, objetivo: Double) {
        scope.launch {
            val exito = repository.editarMeta(id, titulo, objetivo)
            if (exito) cargarMetas()
        }
    }
}
