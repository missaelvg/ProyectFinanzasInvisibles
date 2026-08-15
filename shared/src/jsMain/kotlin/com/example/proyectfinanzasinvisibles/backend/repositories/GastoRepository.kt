package com.example.proyectfinanzasinvisibles.backend.repositories

import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase

actual class GastoRepository {
    actual suspend fun sincronizarGasto(gasto: Gasto): Boolean = false
    actual suspend fun actualizarEstadoGasto(gastoId: String, nuevoEstado: String): Boolean {
        GastoDatabase.cambiarEstado(gastoId, nuevoEstado)
        return true
    }
    actual suspend fun actualizarGasto(gasto: Gasto): Boolean {
        GastoDatabase.actualizarGasto(gasto)
        return true
    }
    actual suspend fun obtenerGastos(): List<Gasto> = GastoDatabase.obtenerGastosLocales()
}

