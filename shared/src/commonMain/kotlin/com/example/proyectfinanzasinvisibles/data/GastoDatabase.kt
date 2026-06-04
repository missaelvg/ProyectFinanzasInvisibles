package com.example.proyectfinanzasinvisibles.data

// Simulación de Base de Datos Local
class GastoDatabase {
    private val gastos = mutableListOf<String>()

    fun guardarGastoLocal(gasto: String) {
        gastos.add(gasto)
        println("Gasto guardado en caché local: $gasto")
    }

    fun obtenerGastosLocales(): List<String> = gastos
}