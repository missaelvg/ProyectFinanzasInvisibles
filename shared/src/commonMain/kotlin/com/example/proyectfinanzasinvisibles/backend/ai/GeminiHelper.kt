package com.example.proyectfinanzasinvisibles.backend.ai

import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import kotlinx.datetime.Clock

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

        val textoLimpio = mensajeTexto.lowercase()
        val respuestaLimpia = respuestaIA.lowercase()

        // 1. DETECCIÓN DE GASTOS FIJOS (Prioridad Máxima)
        val esFijo = textoLimpio.contains("renta") || 
                     textoLimpio.contains("luz") || 
                     textoLimpio.contains("agua") || 
                     textoLimpio.contains("internet") ||
                     textoLimpio.contains("netflix") || 
                     textoLimpio.contains("spotify") ||
                     textoLimpio.contains("seguro") ||
                     textoLimpio.contains("colegiatura")

        // 2. DETECCIÓN DE GASTOS HORMIGA (Cosas pequeñas e innecesarias)
        val esHormiga = (respuestaLimpia.contains("hormiga") && !respuestaLimpia.contains("no es")) || 
                        respuestaLimpia.contains("antojo") || 
                        textoLimpio.contains("café") || 
                        textoLimpio.contains("starbucks") || 
                        textoLimpio.contains("oxxo") || 
                        textoLimpio.contains("papas") || 
                        textoLimpio.contains("dulces") || 
                        textoLimpio.contains("coca") ||
                        textoLimpio.contains("sabritas") ||
                        textoLimpio.contains("tiendita")

        // 3. DETECCIÓN DE GASTOS NORMALES/VARIABLES (Supervivencia/Necesidad)
        val esVariable = textoLimpio.contains("walmart") || 
                         textoLimpio.contains("soriana") || 
                         textoLimpio.contains("gasolina") || 
                         textoLimpio.contains("farmacia") || 
                         textoLimpio.contains("despensa") || 
                         textoLimpio.contains("comida") ||
                         textoLimpio.contains("uber") ||
                         textoLimpio.contains("didi")

        // Asignación de Categoría Final
        val categoriaFinal = when {
            esFijo -> "Fijo"
            esHormiga -> "Hormiga"
            esVariable -> "Variable"
            else -> "Variable" // Por defecto si no estamos seguros
        }

        val tipoClasificado = if (categoriaFinal == "Hormiga") "Gasto Hormiga" else "Gasto Normal"

        // Extraer monto
        val montoExtraido = try {
            val match = "\\$?(\\d+([.,]\\d+)?)".toRegex().find(mensajeTexto)
            match?.groupValues?.get(1)?.replace(",", ".")?.toDouble() ?: 0.0
        } catch (_: Exception) {
            0.0
        }

        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()

        return Gasto(
            id = "ID-$now",
            descripcion = if (respuestaIA != "ERROR" && respuestaIA.length < 100) "$mensajeTexto ($respuestaIA)" else mensajeTexto,
            monto = montoExtraido,
            categoria = categoriaFinal,
            tipo = tipoClasificado,
            estado = "Pendiente",
            fecha = now
        )
    }
}
