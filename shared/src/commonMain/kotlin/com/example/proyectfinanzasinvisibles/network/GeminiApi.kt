package com.example.proyectfinanzasinvisibles.network


class GeminiApi {


    suspend fun analizarGastoConIA(textoNotificacion: String): String {

        println("Realizando petición REST HTTP POST a Gemini con: $textoNotificacion")


        val respuestaJsonMock = """
            {
                "monto": 45.0,
                "concepto": "Café OXXO",
                "categoria": "Antojos y Snacks",
                "alerta": "Gasto innecesario"
            }
        """.trimIndent()

        return respuestaJsonMock
    }
}