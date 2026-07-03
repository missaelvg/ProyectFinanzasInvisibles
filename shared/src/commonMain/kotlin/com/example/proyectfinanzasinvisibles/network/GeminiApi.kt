package com.example.proyectfinanzasinvisibles.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Clase encargada de conectarse realmente con Gemini API.
 *
 * Este archivo representa el proceso de acceso a servicios web,
 * porque la app envía información a un servicio externo y recibe
 * una respuesta procesada.
 */
class GeminiApi {

    /**
     * API Key de Gemini.
     *
     * Coloca aquí tu API Key real solo para probar localmente.
     * Antes de subir a GitHub, vuelve a dejar "TU_API_KEY_AQUI".
     */
    private val apiKey = "TU_API_KEY_AQUI"

    /**
     * Modelo de Gemini que se usará para analizar el gasto.
     */
    private val modelo = "gemini-2.5-flash"

    /**
     * Cliente HTTP de Ktor.
     *
     * Permite enviar solicitudes a internet desde Kotlin Multiplatform.
     */
    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
    }

    /**
     * Envía un texto de gasto a Gemini API y devuelve el análisis.
     *
     * Al ser suspend, se ejecuta dentro de una corrutina para no congelar
     * la interfaz de usuario.
     */
    suspend fun analizarTexto(textoBanco: String): String {
        if (apiKey == "TU_API_KEY_AQUI") {
            return "Error: falta configurar la API Key de Gemini."
        }

        return try {
            val prompt = """
                Analiza el siguiente movimiento bancario:
                
                "$textoBanco"
                
                Identifica:
                1. Categoría del gasto.
                2. Monto.
                3. Si parece gasto hormiga.
                4. Una recomendación breve para el usuario.
                
                Responde exactamente con este formato:
                Categoría: ...
                Monto: ...
                Gasto hormiga: Sí/No
                Recomendación: ...
            """.trimIndent()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelo:generateContent"

            val requestBody = buildJsonObject {
                put("contents", kotlinx.serialization.json.buildJsonArray {
                    add(buildJsonObject {
                        put("parts", kotlinx.serialization.json.buildJsonArray {
                            add(buildJsonObject {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }.toString()

            val responseText = client.post(url) {
                header("x-goog-api-key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.bodyAsText()

            extraerRespuestaGemini(responseText)

        } catch (e: Exception) {
            "Error al conectar con Gemini: ${e.message}"
        }
    }

    /**
     * Extrae el texto de la respuesta JSON enviada por Gemini.
     *
     * Gemini responde en JSON y el texto normalmente viene en:
     * candidates -> content -> parts -> text
     */
    private fun extraerRespuestaGemini(jsonText: String): String {
        return try {
            val root = Json.parseToJsonElement(jsonText).jsonObject

            if (root["error"] != null) {
                return "Error de Gemini: ${root["error"]}"
            }

            val candidates = root["candidates"]?.jsonArray
            val firstCandidate = candidates?.firstOrNull()?.jsonObject
            val content = firstCandidate?.get("content")?.jsonObject
            val parts = content?.get("parts")?.jsonArray

            val respuesta = parts
                ?.mapNotNull { part ->
                    part.jsonObject["text"]?.jsonPrimitive?.contentOrNull
                }
                ?.joinToString("\n")
                ?.trim()

            if (respuesta.isNullOrBlank()) {
                "Gemini respondió, pero no se encontró texto en la respuesta."
            } else {
                respuesta
            }

        } catch (e: Exception) {
            "Error al leer la respuesta de Gemini: ${e.message}"
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