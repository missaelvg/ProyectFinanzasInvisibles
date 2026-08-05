package com.example.proyectfinanzasinvisibles.backend.repositories

import com.example.proyectfinanzasinvisibles.backend.data.MetaAhorro

expect class MetaRepository() {
    suspend fun obtenerMetas(): List<MetaAhorro>
    suspend fun guardarMeta(meta: MetaAhorro): Boolean
    suspend fun actualizarProgresoMeta(docId: String, nuevoAcumulado: Double): Boolean
    suspend fun eliminarMeta(docId: String): Boolean
    suspend fun editarMeta(docId: String, titulo: String, objetivo: Double): Boolean
}
