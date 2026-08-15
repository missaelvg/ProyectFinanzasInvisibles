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
    private companion object {
        const val BACKEND_URL = "https://finanzas-invisibles-api-production.up.railway.app/api/analizar"
        val client = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 15_000
            }
        }
    }

    suspend fun analizarTexto(textoBanco: String): Result<String> {
        return try {
            val requestBody = buildJsonObject {
                put("textoBanco", textoBanco)
            }.toString()

            val response = client.post(BACKEND_URL) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                extraerRespuestaBackend(response.bodyAsText())
            } else {
                Result.failure(IllegalStateException("El servidor respondió ${response.status.value}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extraerRespuestaBackend(jsonText: String): Result<String> {
        return try {
            val root = Json.parseToJsonElement(jsonText).jsonObject

            val ok = root["ok"]?.jsonPrimitive?.contentOrNull
            val resultado = root["resultado"]?.jsonPrimitive?.contentOrNull
            val error = root["error"]?.jsonPrimitive?.contentOrNull

            if (ok == "true") {
                resultado?.takeIf { it.isNotBlank() }?.let { Result.success(it) }
                    ?: Result.failure(IllegalStateException("El backend no devolvió resultado"))
            } else {
                Result.failure(IllegalStateException(error ?: "Error desconocido del backend"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
