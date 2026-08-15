package com.example.proyectfinanzasinvisibles

import com.example.proyectfinanzasinvisibles.backend.ai.ExpenseParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedCommonTest {
    @Test
    fun extraeMontoConMilesYDecimales() {
        assertEquals(1250.50, ExpenseParser.extractAmount("Compra aprobada por \$1,250.50 MXN"))
    }

    @Test
    fun noConfundeTerminacionDeTarjetaConMonto() {
        assertEquals(120.0, ExpenseParser.extractAmount("Compra con tarjeta 1234 por \$120"))
    }

    @Test
    fun prefiereMontoDeCompraSobreSaldoDisponible() {
        assertEquals(120.0, ExpenseParser.extractAmount("Saldo disponible \$5,000. Compra con tarjeta por \$120"))
    }

    @Test
    fun ignoraIngresosYReembolsos() {
        assertFalse(ExpenseParser.isExpenseNotification("Transferencia recibida por \$850"))
        assertNull(ExpenseParser.parse("Reembolso de \$99.00"))
    }

    @Test
    fun clasificaGastoHormigaSinInternet() {
        val expense = ExpenseParser.parse("Compra en OXXO por \$75")
        assertNotNull(expense)
        assertEquals("Hormiga", expense.category)
        assertTrue(expense.amount > 0.0)
    }
}
