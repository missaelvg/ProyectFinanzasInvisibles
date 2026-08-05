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
            try {
                isLoading = true
                val nuevasMetas = repository.obtenerMetas()
                metas = nuevasMetas
                // Log para depuración
                println("Metas cargadas: ${nuevasMetas.size}")
            } catch (e: Exception) {
                println("Error cargando metas: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun calcularPorcentajeProgreso(meta: MetaAhorro): Float {
        if (meta.montoObjetivo <= 0.0) return 0f
        return (meta.montoAcumulado / meta.montoObjetivo).toFloat().coerceIn(0f, 1f)
    }

    fun crearMeta(titulo: String, objetivo: Double) {
        if (titulo.isBlank() || objetivo <= 0) return

        val nuevaMeta = MetaAhorro(
            idMeta = "temp-${(100..999).random()}",
            titulo = titulo,
            montoObjetivo = objetivo,
            montoAcumulado = 0.0,
            rachaActualDias = 0,
            mejorRachaDias = 0,
            mensajeMotivacional = "¡Nueva meta creada! Empieza a ahorrar hoy."
        )
        
        scope.launch {
            isLoading = true
            val exito = repository.guardarMeta(nuevaMeta)
            if (exito) {
                cargarMetas()
            }
            isLoading = false
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

    fun sumarAhorroAMeta(idMeta: String, monto: Double) {
        val meta = metas.find { it.idMeta == idMeta } ?: return
        val nuevoAcumulado = meta.montoAcumulado + monto
        
        scope.launch {
            isLoading = true
            // 1. Actualizar la meta en Firestore
            val exitoMeta = repository.actualizarProgresoMeta(idMeta, nuevoAcumulado)
            
            if (exitoMeta) {
                // 2. Marcar los gastos como "Aplicado" en Firestore para que no se repitan
                val gastoRepo = com.example.proyectfinanzasinvisibles.backend.repositories.GastoRepository()
                val gastosRechazados = com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase.gastos
                    .filter { it.estado == "Rechazado" }
                
                gastosRechazados.forEach { gasto ->
                    gastoRepo.actualizarEstadoGasto(gasto.id, "Aplicado")
                }
                
                // 3. Actualizar la base de datos local
                val nuevosGastosLocales = com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase.gastos.map {
                    if (it.estado == "Rechazado") it.copy(estado = "Aplicado") else it
                }
                com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase.inicializarGastos(nuevosGastosLocales)
                
                cargarMetas()
            }
            isLoading = false
        }
    }
}
