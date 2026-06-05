package com.example.proyectfinanzasinvisibles.network

/**
 * Simulación de un servicio backend local para sincronizar gastos.
 */
object BackendLocal {
    // End-Point: POST /api/v1/gastos/sincronizar
    suspend fun postSincronizarGasto(gastoProcesado: String): Boolean {
        // Recibe el gasto ya procesado y lo guarda en la base de datos central
        println("Sincronizando con el servidor local: $gastoProcesado")
        // Aquí iría la lógica real de red (Ktor, etc.)
        return true
    }
}
