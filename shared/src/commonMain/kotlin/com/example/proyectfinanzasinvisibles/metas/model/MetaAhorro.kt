package com.example.proyectfinanzasinvisibles.metas.model

data class MetaAhorro(
    val idMeta: Int,
    val titulo: String,
    val montoObjetivo: Double,
    val montoAcumulado: Double,
    val rachaActualDias: Int,
    val mejorRachaDias: Int,
    val mensajeMotivacional: String
)