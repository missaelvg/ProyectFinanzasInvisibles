package com.example.proyectfinanzasinvisibles.network

import com.example.proyectfinanzasinvisibles.data.Gasto
import com.example.proyectfinanzasinvisibles.data.GastoDatabase

class GastoRepository(private val database: GastoDatabase) {

    /**
     * Agrega un nuevo gasto a la base de datos local.
     * En una implementación real, esta función podría ser suspendida y llamar
     * a servicios externos para categorizar el gasto automáticamente.
     */
    fun agregarNuevoGasto(descripcion: String, monto: Double) {
        val nuevoGasto = Gasto(
            id = database.obtenerGastosLocales().size + 1,
            descripcion = descripcion,
            monto = monto,
            categoria = "General" // Por defecto, se podría mejorar con IA
        )
        database.guardarGastoLocal(nuevoGasto)
    }
}
