package com.example.proyectfinanzasinvisibles.network

class GeminiApi {
    // Consumo de API REST Externa
    suspend fun analizarTexto(textoBanco: String): String {
        // Aquí iría el cliente Ktor real haciendo el POST a Google Gemini
        println("Enviando a Gemini: $textoBanco")
        return "Categoría: Antojos, Monto: \$45.00"
    }
}

/**
 * Función global para facilitar el acceso desde la UI, 
 * simulando una llamada a la API de Gemini.
 */
suspend fun postCategorizarGasto(texto: String): String {
    val api = GeminiApi()
    // Simulamos un retraso de red
    kotlinx.coroutines.delay(1500)
    return api.analizarTexto(texto)
 * Función de conveniencia para categorizar gastos usando IA.
 */
suspend fun postCategorizarGasto(textoBanco: String): String {
    return GeminiApi().analizarTexto(textoBanco)
}
