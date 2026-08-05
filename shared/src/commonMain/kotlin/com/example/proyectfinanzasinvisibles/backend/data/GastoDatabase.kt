package com.example.proyectfinanzasinvisibles.backend.data

import androidx.compose.runtime.mutableStateListOf

object GastoDatabase {
    private val _gastos = mutableStateListOf<Gasto>()

    fun inicializarGastos(nuevosGastos: List<Gasto>) {
        _gastos.clear()
        _gastos.addAll(nuevosGastos)
    }

    fun guardarGastoLocal(gasto: Gasto) {
        _gastos.add(0, gasto)
    }

    fun aceptarGasto(id: String) {
        val index = _gastos.indexOfFirst { it.id == id }
        if (index != -1) {
            val gasto = _gastos[index]
            _gastos[index] = gasto.copy(estado = "Aceptado")
        }
    }

    fun obtenerGastosLocales(): List<Gasto> = _gastos.toList()

    fun eliminarGasto(gasto: Gasto) {
        _gastos.remove(gasto)
    }

    fun limpiarBaseDeDatos() {
        _gastos.clear()
    }
}
