package com.example.proyectfinanzasinvisibles.data

class GastoDatabase {
    private val gastos = mutableListOf<Gasto>(
        Gasto(1, "Oxxo - Antojos", 45.0, "Antojos"),
        Gasto(2, "Starbucks", 120.0, "Café"),
        Gasto(3, "Uber - Viaje", 85.0, "Transporte")
    )

    fun guardarGastoLocal(gasto: Gasto) {
        gastos.add(gasto)
        println("Gasto guardado en caché local: ${gasto.descripcion}")
    }

    fun obtenerGastosLocales(): List<Gasto> = gastos
}
