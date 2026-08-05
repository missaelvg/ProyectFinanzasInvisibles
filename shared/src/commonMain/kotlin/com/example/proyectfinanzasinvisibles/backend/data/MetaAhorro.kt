package com.example.proyectfinanzasinvisibles.backend.data

data class MetaAhorro(
    val idMeta: String,
    val titulo: String,
    val montoObjetivo: Double,
    val montoAcumulado: Double,
    val rachaActualDias: Int,
    val mejorRachaDias: Int,
    val mensajeMotivacional: String
)
