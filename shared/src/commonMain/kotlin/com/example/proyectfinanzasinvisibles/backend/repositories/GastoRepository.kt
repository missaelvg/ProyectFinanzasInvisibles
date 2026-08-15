package com.example.proyectfinanzasinvisibles.backend.repositories

import com.example.proyectfinanzasinvisibles.backend.data.Gasto

expect class GastoRepository() {
    suspend fun sincronizarGasto(gasto: Gasto): Boolean
    suspend fun actualizarEstadoGasto(gastoId: String, nuevoEstado: String): Boolean
    suspend fun actualizarGasto(gasto: Gasto): Boolean
    suspend fun obtenerGastos(): List<Gasto>
}
