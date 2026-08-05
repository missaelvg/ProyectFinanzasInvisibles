package com.example.proyectfinanzasinvisibles.backend.ai

import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import kotlin.time.Clock

/**
 * Helper para interactuar con la IA de Gemini para clasificar gastos.
 */
class GeminiHelper {
    
    private val api = GeminiApi()

    /**
     * Clasifica un mensaje de texto (notificación) usando el backend de IA si es posible,
     * de lo contrario usa una clasificación local como fallback.
     */
    suspend fun clasificarGasto(mensajeTexto: String): Gasto {
        val respuestaIA = try {
            api.analizarTexto(mensajeTexto)
        } catch (_: Exception) {
            "ERROR"
        }

        val esHormigaIA = respuestaIA.contains("Hormiga", ignoreCase = true) || 
                         respuestaIA.contains("Antojo", ignoreCase = true)
        
        val textoLimpio = mensajeTexto.lowercase()
        
        // Local Fallback / Supplemental logic
        val esHormigaLocal = textoLimpio.contains("café") || 
                        textoLimpio.contains("starbucks") || 
                        textoLimpio.contains("oxxo") || 
                        textoLimpio.contains("papas") || 
                        textoLimpio.contains("dulces") || 
                        textoLimpio.contains("netflix") || 
                        textoLimpio.contains("spotify") || 
                        textoLimpio.contains("antojo")

        val esHormiga = esHormigaIA || (respuestaIA == "ERROR" && esHormigaLocal)

        val esNormal = textoLimpio.contains("renta") || 
                       textoLimpio.contains("despensa") || 
                       textoLimpio.contains("servicios") || 
                       textoLimpio.contains("luz") || 
                       textoLimpio.contains("agua") || 
                       textoLimpio.contains("walmart")

        val tipoClasificado = when {
            esHormiga -> "Gasto Hormiga"
            esNormal -> "Gasto Normal"
            else -> if (respuestaIA != "ERROR") "Gasto Normal" else "Gasto Normal"
        }

        val montoExtraido = try {
            "\\d+(\\.\\d+)?".toRegex().find(mensajeTexto)?.value?.toDouble() ?: 0.0
        } catch (_: Exception) {
            0.0
        }

        val now = Clock.System.now().toEpochMilliseconds()

        return Gasto(
            id = "ID-$now",
            descripcion = if (respuestaIA != "ERROR" && respuestaIA.length < 100) "$mensajeTexto ($respuestaIA)" else mensajeTexto,
            monto = montoExtraido,
            categoria = if (esHormiga) "Hormiga" else "Fijo/Variable",
            tipo = tipoClasificado,
            estado = "Pendiente",
            fecha = now
        )
    }
}
