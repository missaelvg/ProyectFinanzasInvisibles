package com.example.proyectfinanzasinvisibles.data

import androidx.compose.runtime.mutableStateListOf

object GastoDatabase {
    private val _gastos = mutableStateListOf<Gasto>(
        Gasto(1, "Oxxo - Antojos", 45.0, "Antojos"),
        Gasto(2, "Starbucks", 120.0, "Café"),
        Gasto(3, "Uber - Viaje", 85.0, "Transporte")
    )

    fun guardarGastoLocal(gasto: Gasto) {
        _gastos.add(0, gasto) // Agregar al inicio para que sea el más reciente
        println("Gasto guardado en caché local: ${gasto.descripcion}")
    }

    fun obtenerGastosLocales(): List<Gasto> = _gastos.toList()

    fun eliminarGasto(gasto: Gasto) {
        _gastos.remove(gasto)
    }
}
