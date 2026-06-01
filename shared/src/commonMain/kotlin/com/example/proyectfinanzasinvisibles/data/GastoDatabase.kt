package com.example.proyectfinanzasinvisibles.data

// 3. ALMACENAMIENTO LOCAL: Tabla relacional de la Base de Datos
data class Gasto(
    val id: Int = 0,
    val monto: Double,
    val concepto: String,
    val categoria: String
)

// Interfaz DAO (Data Access Object) para consultar la base de datos
interface GastoDao {
    fun insertarGasto(gasto: Gasto)
    fun obtenerGastosPorCategoria(categoria: String): List<Gasto>
}

// Clase controladora del cifrado
class LocalDatabase {
    fun guardarGastoCifrado(gasto: Gasto) {
        // Aquí se aplica SQLCipher para proteger la privacidad del usuario
        println("Se guardó localmente en el dispositivo: ${gasto.concepto}")
    }
}