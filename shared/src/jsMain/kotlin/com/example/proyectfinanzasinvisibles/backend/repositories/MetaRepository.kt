package com.example.proyectfinanzasinvisibles.backend.repositories

import com.example.proyectfinanzasinvisibles.backend.data.MetaAhorro

actual class MetaRepository {
    private val metas = mutableListOf<MetaAhorro>()
    actual suspend fun obtenerMetas(): List<MetaAhorro> = metas.toList()
    actual suspend fun guardarMeta(meta: MetaAhorro): Boolean = metas.add(meta)
    actual suspend fun actualizarProgresoMeta(docId: String, nuevoAcumulado: Double): Boolean {
        val index = metas.indexOfFirst { it.idMeta == docId }
        if (index < 0) return false
        metas[index] = metas[index].copy(montoAcumulado = nuevoAcumulado)
        return true
    }
    actual suspend fun eliminarMeta(docId: String): Boolean = metas.removeAll { it.idMeta == docId }
    actual suspend fun editarMeta(docId: String, titulo: String, objetivo: Double): Boolean {
        val index = metas.indexOfFirst { it.idMeta == docId }
        if (index < 0) return false
        metas[index] = metas[index].copy(titulo = titulo, montoObjetivo = objetivo)
        return true
    }
}

