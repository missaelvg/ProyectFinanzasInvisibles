package com.example.proyectfinanzasinvisibles.backend.ai

import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import kotlinx.datetime.Clock

data class ClassificationResult(
    val gasto: Gasto,
    val usedAi: Boolean
)

/** Combina la sugerencia del backend con reglas locales que siempre validan monto y tipo. */
class GeminiHelper(private val api: GeminiApi = GeminiApi()) {

    suspend fun clasificarConDetalle(mensajeTexto: String): ClassificationResult? {
        val localResult = ExpenseParser.parse(mensajeTexto) ?: return null
        val aiResult = api.analizarTexto(mensajeTexto)
        val parsed = ExpenseParser.parse(mensajeTexto, aiResult.getOrNull()) ?: localResult
        val now = Clock.System.now().toEpochMilliseconds()

        return ClassificationResult(
            gasto = Gasto(
                id = "gasto-$now",
                descripcion = mensajeTexto.trim().take(180),
                monto = parsed.amount,
                categoria = parsed.category,
                tipo = parsed.type,
                estado = "Pendiente",
                fecha = now,
                sincronizado = false
            ),
            usedAi = aiResult.isSuccess
        )
    }

    suspend fun clasificarGasto(mensajeTexto: String): Gasto =
        clasificarConDetalle(mensajeTexto)?.gasto
            ?: throw IllegalArgumentException("El texto no contiene un gasto reconocible")
}
