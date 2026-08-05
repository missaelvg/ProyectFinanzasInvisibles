package com.example.proyectfinanzasinvisibles.backend.ai

import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Helper para interactuar con la IA de Gemini para clasificar gastos.
 */
class GeminiHelper(private val apiKey: String = "") {
    
    /**
     * Clasifica un mensaje de texto (notificación) simulando el comportamiento de Gemini.
     * En una implementación real, se enviaría el prompt al modelo generativo.
     */
    suspend fun clasificarGasto(mensajeTexto: String): Gasto {
        val textoLimpio = mensajeTexto.lowercase()
        
        // Simulación del Prompt de Gemini:
        // "Clasifica la compra: si es un café, papas, suscripciones pequeñas, es 'Gasto Hormiga'; 
        // si es despensa, renta o servicios, es 'Gasto Normal'."
        
        val esHormiga = textoLimpio.contains("café") || 
                        textoLimpio.contains("starbucks") || 
                        textoLimpio.contains("oxxo") || 
                        textoLimpio.contains("papas") || 
                        textoLimpio.contains("dulces") || 
                        textoLimpio.contains("netflix") || 
                        textoLimpio.contains("spotify") || 
                        textoLimpio.contains("antojo")

        val esNormal = textoLimpio.contains("renta") || 
                       textoLimpio.contains("despensa") || 
                       textoLimpio.contains("servicios") || 
                       textoLimpio.contains("luz") || 
                       textoLimpio.contains("agua") || 
                       textoLimpio.contains("walmart")

        val tipoClasificado = when {
            esHormiga -> "Gasto Hormiga"
            esNormal -> "Gasto Normal"
            else -> "Gasto Normal" // Por defecto
        }

        val montoExtraido = try {
            "\\d+(\\.\\d+)?".toRegex().find(mensajeTexto)?.value?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }

        val now = Clock.System.now().toEpochMilliseconds()

        return Gasto(
            id = "ID-$now",
            descripcion = mensajeTexto,
            monto = montoExtraido,
            categoria = if (esHormiga) "Hormiga" else "Fijo/Variable",
            tipo = tipoClasificado,
            estado = "Pendiente", // Siempre inicia como pendiente para aprobación del usuario
            fecha = now
        )
    }
}
