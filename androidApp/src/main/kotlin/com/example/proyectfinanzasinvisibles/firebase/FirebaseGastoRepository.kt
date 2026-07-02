package com.example.proyectfinanzasinvisibles.firebase

import android.util.Log
import com.example.proyectfinanzasinvisibles.data.Gasto
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class FirebaseGastoRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun sincronizarGasto(gasto: Gasto): Boolean {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FIREBASE_SYNC", "Error: No hay usuario autenticado.")
            return false
        }

        return try {
            // TÉCNICA 3: Logging - Registro de inicio para trazabilidad
            Log.d("FIREBASE_SYNC", "Intentando persistir gasto de: ${gasto.monto} para el usuario: ${currentUser.uid}")
            
            // Creamos un mapa para incluir el ID del usuario
            val gastoData = mapOf(
                "descripcion" to gasto.descripcion,
                "monto" to gasto.monto,
                "categoria" to gasto.categoria,
                "userId" to currentUser.uid,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            // TÉCNICA 9: Coroutines - Uso de await() para ejecución asíncrona no bloqueante
            firestore.collection("gastos_hormiga").add(gastoData).await()
            
            Log.d("FIREBASE_SYNC", "ÉXITO: Gasto sincronizado correctamente.")
            true 
            
        } catch (e: FirebaseFirestoreException) {
            // Manejo de Excepciones específicas
            // ---Logging de error de base de datos---
            Log.e("FIREBASE_SYNC", "Error de Firestore (Código: ${e.code}): ${e.message}")
            false
        } catch (e: Exception) {
            // Manejo de Excepción Genérica (Evita el Crash)
            Log.e("FIREBASE_SYNC", "Falla inesperada: ${e.message}")
            e.printStackTrace() 
            false 
        }
    }
}
