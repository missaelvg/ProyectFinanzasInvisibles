package com.example.proyectfinanzasinvisibles.network

import com.example.proyectfinanzasinvisibles.data.Gasto
import com.example.proyectfinanzasinvisibles.data.GastoDatabase

// Simulación de un Servicio de Backend / API
class GastoRepository(private val database: GastoDatabase) {

    // Simulación de Consumo de API (Endpoint Local)
    suspend fun fetchResumenDesdeAPI(): Map<String, Double> {
        // Simulamos un retraso de red
        kotlinx.coroutines.delay(500)
        return mapOf(
            "total_semana" to 1240.00,
            "gastos_hormiga" to 350.00
        )
    }

    // Método que une Front con Backend y Almacenamiento Local
    suspend fun sincronizarYObtenerGastos(): List<Gasto> {
        // Aquí se podría lógica de sincronización:
        // 1. Llamar a fetchResumenDesdeAPI()
        // 2. Guardar datos nuevos en database
        return database.obtenerGastosLocales()
    }

    fun agregarNuevoGasto(descripcion: String, monto: Double) {
        val nuevo = Gasto(
            id = (database.obtenerGastosLocales().maxOfOrNull { it.id } ?: 0) + 1,
            descripcion = descripcion,
            monto = monto,
            categoria = "General"
        )
        database.guardarGastoLocal(nuevo)
    }
}
