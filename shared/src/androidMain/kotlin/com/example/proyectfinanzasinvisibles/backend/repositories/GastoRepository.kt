package com.example.proyectfinanzasinvisibles.backend.repositories

import android.util.Log
import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date

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
                "timestamp" to com.google.firebase.Timestamp(Date(gasto.fecha))
            )
            firestore.collection("gastos_hormiga")
                .document(gasto.id)
                .set(gastoData, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("GastoRepository", "Error sincronizando: ${e.message}")
            false
        }
    }

    actual suspend fun actualizarEstadoGasto(gastoId: String, nuevoEstado: String): Boolean {
        val currentUser = auth.currentUser ?: return false
        return try {
            val document = firestore.collection("gastos_hormiga").document(gastoId)
            val snapshot = document.get().await()
            if (snapshot.getString("userId") != currentUser.uid) return false
            document.update("estado", nuevoEstado).await()
            true
        } catch (e: Exception) {
            Log.e("GastoRepository", "Error actualizando estado: ${e.message}")
            false
        }
    }

    actual suspend fun actualizarGasto(gasto: Gasto): Boolean {
        val currentUser = auth.currentUser ?: return false
        return try {
            val document = firestore.collection("gastos_hormiga").document(gasto.id)
            val snapshot = document.get().await()
            if (snapshot.getString("userId") != currentUser.uid) return false
            document.update(
                mapOf(
                    "descripcion" to gasto.descripcion,
                    "monto" to gasto.monto,
                    "categoria" to gasto.categoria,
                    "tipo" to gasto.tipo,
                    "estado" to gasto.estado
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e("GastoRepository", "Error editando gasto: ${e.message}")
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
                    fecha = (data["timestamp"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: 0L,
                    sincronizado = true
                )
            }
        } catch (e: Exception) {
            Log.e("GastoRepository", "Error obteniendo gastos: ${e.message}")
            emptyList()
        }
    }
}
