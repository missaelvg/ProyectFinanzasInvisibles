package com.example.proyectfinanzasinvisibles.backend.data

data class Gasto(
    val id: String = "",
    val descripcion: String,
    val monto: Double,
    val categoria: String,
    val tipo: String, // "Gasto Hormiga" o "Gasto Normal"
    val estado: String = "Pendiente", // "Pendiente", "Aceptado", "Rechazado"
    val fecha: Long = 0L,
    val sincronizado: Boolean = false
)
