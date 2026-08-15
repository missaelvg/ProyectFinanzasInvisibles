package com.example.proyectfinanzasinvisibles.backend.ai

data class ParsedExpense(
    val amount: Double,
    val category: String,
    val type: String
)

/** Reglas locales deterministas para filtrar y estructurar notificaciones bancarias. */
object ExpenseParser {
    private val incomeWords = listOf(
        "deposito", "depósito", "recibiste", "recibido", "abono", "reembolso",
        "devolucion", "devolución", "saldo a favor", "transferencia recibida"
    )
    private val expenseWords = listOf(
        "cargo", "compra", "pago", "retiro", "consumo", "transferencia enviada",
        "aprobada", "aprobado", "realizaste", "gastaste"
    )

    fun isExpenseNotification(text: String): Boolean {
        val normalized = text.lowercase()
        if (incomeWords.any(normalized::contains)) return false
        return expenseWords.any(normalized::contains) && extractAmount(text) != null
    }

    fun parse(text: String, aiHint: String? = null): ParsedExpense? {
        if (!isExpenseNotification(text)) return null
        val amount = extractAmount(text)?.takeIf { it > 0.0 } ?: return null
        val normalized = text.lowercase()
        val hint = aiHint.orEmpty().lowercase()

        val fixedWords = listOf("renta", "luz", "agua", "internet", "netflix", "spotify", "seguro", "colegiatura")
        val antWords = listOf("café", "cafe", "starbucks", "oxxo", "papas", "dulces", "refresco", "sabritas", "tiendita", "antojo")
        val category = when {
            fixedWords.any(normalized::contains) -> "Fijo"
            antWords.any(normalized::contains) || (hint.contains("hormiga") && !hint.contains("no es")) -> "Hormiga"
            else -> "Variable"
        }

        return ParsedExpense(
            amount = amount,
            category = category,
            type = if (category == "Hormiga") "Gasto Hormiga" else "Gasto Normal"
        )
    }

    fun extractAmount(text: String): Double? {
        val patterns = listOf(
            Regex("(?:cargo|compra|pago|retiro|consumo)[^\\n]{0,60}?(?:por|de|monto|importe|total)\\s*:?\\s*\\$?\\s*([0-9][0-9.,]*)", RegexOption.IGNORE_CASE),
            Regex("(?:aprobada|aprobado|gastaste)\\s*:?\\s*\\$?\\s*([0-9][0-9.,]*)", RegexOption.IGNORE_CASE),
            Regex("(?:MXN\\s*)?\\$\\s*([0-9][0-9.,]*)", RegexOption.IGNORE_CASE),
            Regex("([0-9][0-9.,]*)\\s*(?:MXN|pesos?)", RegexOption.IGNORE_CASE),
            Regex("(?:por|monto|importe|total)\\s*[:$]?\\s*([0-9][0-9.,]*)", RegexOption.IGNORE_CASE)
        )
        return patterns.firstNotNullOfOrNull { pattern ->
            pattern.find(text)?.groupValues?.getOrNull(1)?.let(::normalizeAmount)
        }
    }

    private fun normalizeAmount(raw: String): Double? {
        val value = raw.filter { it.isDigit() || it == ',' || it == '.' }
        if (value.isBlank()) return null

        val lastComma = value.lastIndexOf(',')
        val lastDot = value.lastIndexOf('.')
        val decimalSeparator = when {
            lastComma >= 0 && lastDot >= 0 -> if (lastComma > lastDot) ',' else '.'
            lastComma >= 0 && value.length - lastComma - 1 in 1..2 -> ','
            lastDot >= 0 && value.length - lastDot - 1 in 1..2 -> '.'
            else -> null
        }

        val normalized = buildString {
            value.forEachIndexed { index, char ->
                when {
                    char.isDigit() -> append(char)
                    decimalSeparator != null && char == decimalSeparator && index == value.lastIndexOf(decimalSeparator) -> append('.')
                }
            }
        }
        return normalized.toDoubleOrNull()
    }
}
