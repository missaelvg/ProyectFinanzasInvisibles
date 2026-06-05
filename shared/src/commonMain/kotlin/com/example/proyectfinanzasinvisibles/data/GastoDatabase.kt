package com.example.proyectfinanzasinvisibles.data

// Usamos un objeto (Singleton) para que tanto la UI como el Servicio de Android
// compartan la misma instancia de datos en memoria durante la ejecución.
object GastoDatabase {
    private val gastos = mutableListOf<Gasto>(
        Gasto(1, "Oxxo - Antojos", 45.0, "Antojos"),
        Gasto(2, "Starbucks", 120.0, "Café"),
        Gasto(3, "Uber - Viaje", 85.0, "Transporte")
    )

    fun guardarGastoLocal(gasto: Gasto) {
        gastos.add(gasto)
        // Log para depuración
        println("Gasto guardado en caché local: ${gasto.descripcion}")
    }

    fun obtenerGastosLocales(): List<Gasto> = gastos.toList()
}
