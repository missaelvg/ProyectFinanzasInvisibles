package com.example.proyectfinanzasinvisibles.backend.repositories

import com.example.proyectfinanzasinvisibles.backend.data.MetaAhorro

actual class MetaRepository {
    actual suspend fun obtenerMetas(): List<MetaAhorro> = emptyList()
    actual suspend fun guardarMeta(meta: MetaAhorro): Boolean = false
    actual suspend fun actualizarProgresoMeta(docId: String, nuevoAcumulado: Double): Boolean = false
}
