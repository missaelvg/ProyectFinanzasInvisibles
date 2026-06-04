package com.example.proyectfinanzasinvisibles.network

// End-Point: GET /api/v1/dashboard/resumen
suspend fun getResumenGastos(): String {
    // Retorna un JSON con el resumen
    return "{ \"total_semana\": 450.00, \"fugas\": \"Café, Oxxo\" }"
}