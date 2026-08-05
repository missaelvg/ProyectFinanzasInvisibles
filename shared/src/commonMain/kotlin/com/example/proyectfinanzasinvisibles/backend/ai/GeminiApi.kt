package com.example.proyectfinanzasinvisibles.backend.ai

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Clase encargada de conectar el frontend con el backend Ktor alojado en Railway.
 */
class GeminiApi {

    private val backendUrl =
        "https://finanzas-invisibles-api-production.up.railway.app/api/analizar"

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000 // Aumentado a 30s por si el backend está "dormido"
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
    }

    suspend fun analizarTexto(textoBanco: String): String {
        return try {
            val requestBody = buildJsonObject {
                put("textoBanco", textoBanco)
            }.toString()

            val response = client.post(backendUrl) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                extraerRespuestaBackend(response.bodyAsText())
            } else {
                "Error del servidor: ${response.status.value}"
            }

        } catch (e: Exception) {
            "Error al conectar con el backend: ${e.message}"
        }
    }

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

suspend fun postCategorizarGasto(textoBanco: String): String {
    val api = GeminiApi()
    return api.analizarTexto(textoBanco)
}
