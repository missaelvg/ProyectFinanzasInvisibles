package com.example.proyectfinanzasinvisibles.backend.repositories

import com.example.proyectfinanzasinvisibles.backend.data.MetaAhorro

expect class MetaRepository() {
    suspend fun obtenerMetas(): List<MetaAhorro>
    suspend fun guardarMeta(meta: MetaAhorro): Boolean
    suspend fun actualizarProgresoMeta(docId: String, nuevoAcumulado: Double): Boolean
}
