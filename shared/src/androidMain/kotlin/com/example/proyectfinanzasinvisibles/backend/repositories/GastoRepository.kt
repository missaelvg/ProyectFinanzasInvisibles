package com.example.proyectfinanzasinvisibles.backend.repositories

import android.util.Log
import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

actual class GastoRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    actual suspend fun sincronizarGasto(gasto: Gasto): Boolean {
        val currentUser = auth.currentUser ?: return false
        return try {
            val gastoData = mapOf(
                "descripcion" to gasto.descripcion,
                "monto" to gasto.monto,
                "categoria" to gasto.categoria,
                "tipo" to gasto.tipo,
                "estado" to gasto.estado,
                "userId" to currentUser.uid,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            val docRef = firestore.collection("gastos_hormiga").add(gastoData).await()
            // Podríamos actualizar el objeto gasto local con el ID real de Firestore si fuera necesario
            true 
        } catch (e: Exception) {
            Log.e("GastoRepository", "Error sincronizando: ${e.message}")
            false
        }
    }

    actual suspend fun actualizarEstadoGasto(gastoId: String, nuevoEstado: String): Boolean {
        return try {
            // Buscamos por el ID del documento
            firestore.collection("gastos_hormiga")
                .document(gastoId)
                .update("estado", nuevoEstado)
                .await()
            true
        } catch (e: Exception) {
            Log.e("GastoRepository", "Error actualizando estado: ${e.message}")
            false
        }
    }

    actual suspend fun obtenerGastos(): List<Gasto> {
        val currentUser = auth.currentUser ?: return emptyList()
        return try {
            val result = firestore.collection("gastos_hormiga")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()
            
            result.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                Gasto(
                    id = doc.id,
                    descripcion = data["descripcion"] as? String ?: "",
                    monto = (data["monto"] as? Number)?.toDouble() ?: 0.0,
                    categoria = data["categoria"] as? String ?: "General",
                    tipo = data["tipo"] as? String ?: "Gasto Normal",
                    estado = data["estado"] as? String ?: "Pendiente",
                    fecha = (data["timestamp"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: 0L
                )
            }
        } catch (e: Exception) {
            Log.e("GastoRepository", "Error obteniendo gastos: ${e.message}")
            emptyList()
        }
    }
}
