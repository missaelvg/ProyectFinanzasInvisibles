package com.example.proyectfinanzasinvisibles.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseGastoRepository {
    private val firestore = Firebase.firestore

    suspend fun sincronizarGasto(gasto: Gasto): Boolean {
        return try {
            // Registro de inicio de la operación
            Log.d("FIREBASE_SYNC", "Iniciando sincronización del gasto: ${gasto.monto}")
            
            // Petición al EndPoint de Firestore
            firestore.collection("gastos_hormiga").add(gasto).await()
            
            // Registro de éxito
            Log.d("FIREBASE_SYNC", "Gasto guardado exitosamente en la nube.")
            true 
            
        } catch (e: Exception) {
            // Manejo de la Excepción y Registro del Error
            Log.e("FIREBASE_SYNC", "Error al sincronizar con Firebase: ${e.message}")
            e.printStackTrace() // Aquí se puede colocar un Breakpoint (Técnica 1) para inspeccionar 'e'
            false 
        }
    }
}
