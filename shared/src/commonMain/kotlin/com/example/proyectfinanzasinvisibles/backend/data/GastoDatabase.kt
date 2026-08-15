package com.example.proyectfinanzasinvisibles.backend.data

import androidx.compose.runtime.mutableStateListOf

object GastoDatabase {
    private val _gastos = mutableStateListOf<Gasto>()
    val gastos: List<Gasto> get() = _gastos

    fun inicializarGastos(nuevosGastos: List<Gasto>) {
        val merged = nuevosGastos.associateBy { it.id }.toMutableMap()
        _gastos.filterNot { it.sincronizado }.forEach { merged[it.id] = it }
        _gastos.clear()
        _gastos.addAll(merged.values.sortedByDescending { it.fecha })
    }

    fun guardarGastoLocal(gasto: Gasto) {
        val index = _gastos.indexOfFirst { it.id == gasto.id }
        if (index == -1) {
            _gastos.add(0, gasto)
        } else {
            _gastos[index] = gasto
        }
    }

    fun cambiarEstado(id: String, nuevoEstado: String) {
        val index = _gastos.indexOfFirst { it.id == id }
        if (index != -1) {
            val gasto = _gastos[index]
            _gastos[index] = gasto.copy(estado = nuevoEstado)
        }
    }

    fun actualizarGasto(gasto: Gasto) = guardarGastoLocal(gasto)

    fun marcarSincronizado(id: String) {
        val index = _gastos.indexOfFirst { it.id == id }
        if (index != -1) {
            _gastos[index] = _gastos[index].copy(sincronizado = true)
        }
    }

    fun obtenerGastosLocales(): List<Gasto> = _gastos.toList()

    fun obtenerPendientesDeSincronizar(): List<Gasto> = _gastos.filterNot { it.sincronizado }

    fun limpiarBaseDeDatos() {
        _gastos.clear()
    }
}
