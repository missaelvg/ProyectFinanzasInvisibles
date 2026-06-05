package com.example.proyectfinanzasinvisibles.network

// End-Point: POST /api/v1/ia/categorizar
suspend fun postCategorizarGasto(textoBruto: String): String {
    val api = GeminiApi()
    val resultado = api.analizarTexto(textoBruto)
    return resultado
}

