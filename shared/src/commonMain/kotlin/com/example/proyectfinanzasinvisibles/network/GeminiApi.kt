package com.example.proyectfinanzasinvisibles.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Clase encargada de conectar el frontend con el backend Ktor alojado en Railway.
 *
 * Este archivo representa el proceso de acceso a servicios web,
 * porque la app móvil envía información a una API externa propia
 * y recibe una respuesta procesada.
 */
class GeminiApi {

    /**
     * URL pública del backend Ktor desplegado en Railway.
     *
     * La app ya no se conecta directamente a Gemini API.
     * Ahora consume el endpoint POST /api/analizar del backend.
     */
    private val backendUrl =
        "https://finanzas-invisibles-api-production.up.railway.app/api/analizar"

    /**
     * Cliente HTTP de Ktor.
     *
     * Permite que la app Kotlin Multiplatform realice peticiones HTTP.
     */
    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
    }

    /**
     * Envía el texto del movimiento bancario al backend alojado en Railway.
     *
     * Al ser una función suspendida, se debe llamar desde una corrutina
     * para evitar bloquear la interfaz de usuario.
     */
    suspend fun analizarTexto(textoBanco: String): String {
        return try {
            val requestBody = buildJsonObject {
                put("textoBanco", textoBanco)
            }.toString()

            val responseText = client.post(backendUrl) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.bodyAsText()

            extraerRespuestaBackend(responseText)

        } catch (e: Exception) {
            "Error al conectar con el backend: ${e.message}"
        }
    }

    /**
     * Extrae el resultado del JSON que responde el backend.
     *
     * El backend responde algo como:
     * {
     *   "ok": true,
     *   "resultado": "Categoria: ...",
     *   "error": null
     * }
     */
    private fun extraerRespuestaBackend(jsonText: String): String {
        return try {
            val root = Json.parseToJsonElement(jsonText).jsonObject

            val ok = root["ok"]?.jsonPrimitive?.contentOrNull
            val resultado = root["resultado"]?.jsonPrimitive?.contentOrNull
            val error = root["error"]?.jsonPrimitive?.contentOrNull

            if (ok == "true") {
                resultado ?: "El backend respondió correctamente, pero no devolvió resultado."
            } else {
                "Error del backend: ${error ?: "Error desconocido"}"
            }

        } catch (e: Exception) {
            "Error al leer la respuesta del backend: ${e.message}"
        }
    }
}

/**
 * Función global para que la pantalla pueda llamar fácilmente al servicio.
 */
suspend fun postCategorizarGasto(textoBanco: String): String {
    val api = GeminiApi()
    return api.analizarTexto(textoBanco)
}