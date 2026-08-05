package com.example.proyectfinanzasinvisibles.backend.repositories

import com.example.proyectfinanzasinvisibles.backend.data.MetaAhorro
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import android.util.Log

actual class MetaRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    actual suspend fun obtenerMetas(): List<MetaAhorro> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val result = firestore.collection("metas")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            result.documents.mapNotNull { doc ->
                MetaAhorro(
                    idMeta = doc.id,
                    titulo = doc.getString("titulo") ?: "",
                    montoObjetivo = doc.getDouble("montoObjetivo") ?: 0.0,
                    montoAcumulado = doc.getDouble("montoAcumulado") ?: 0.0,
                    rachaActualDias = doc.getLong("rachaActualDias")?.toInt() ?: 0,
                    mejorRachaDias = doc.getLong("mejorRachaDias")?.toInt() ?: 0,
                    mensajeMotivacional = doc.getString("mensajeMotivacional") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("MetaRepository", "Error obteniendo metas: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun guardarMeta(meta: MetaAhorro): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            val metaData = mapOf(
                "userId" to userId,
                "titulo" to meta.titulo,
                "montoObjetivo" to meta.montoObjetivo,
                "montoAcumulado" to meta.montoAcumulado,
                "rachaActualDias" to meta.rachaActualDias,
                "mejorRachaDias" to meta.mejorRachaDias,
                "mensajeMotivacional" to meta.mensajeMotivacional
            )
            firestore.collection("metas").add(metaData).await()
            true
        } catch (e: Exception) {
            Log.e("MetaRepository", "Error guardando meta: ${e.message}")
            false
        }
    }

    actual suspend fun actualizarProgresoMeta(docId: String, nuevoAcumulado: Double): Boolean {
        return try {
            firestore.collection("metas").document(docId)
                .update("montoAcumulado", nuevoAcumulado)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    actual suspend fun eliminarMeta(docId: String): Boolean {
        return try {
            firestore.collection("metas").document(docId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("MetaRepository", "Error eliminando meta: ${e.message}")
            false
        }
    }

    actual suspend fun editarMeta(docId: String, titulo: String, objetivo: Double): Boolean {
        return try {
            val updates = mapOf(
                "titulo" to titulo,
                "montoObjetivo" to objetivo
            )
            firestore.collection("metas").document(docId).update(updates).await()
            true
        } catch (e: Exception) {
            Log.e("MetaRepository", "Error editando meta: ${e.message}")
            false
        }
    }
}
