package com.example.proyectfinanzasinvisibles.backend.repositories

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

actual class AuthRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun getCurrentUser() = auth.currentUser

    actual fun isUserLoggedIn(): Boolean = auth.currentUser != null

    actual suspend fun signUp(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        fechaNacimiento: String,
        ciudad: String
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Error al crear usuario"))
            
            val userData = mapOf(
                "nombre" to nombre,
                "apellido" to apellido,
                "fechaNacimiento" to fechaNacimiento,
                "ciudad" to ciudad,
                "email" to email,
                "racha" to 1,
                "ultimaConexion" to com.google.firebase.Timestamp.now(),
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("users").document(user.uid).set(userData).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun actualizarRacha(): Int {
        return try {
            val uid = auth.currentUser?.uid ?: return 0
            val docRef = firestore.collection("users").document(uid)
            val doc = docRef.get().await()
            
            if (!doc.exists()) return 0

            val ultimaConexion = doc.getTimestamp("ultimaConexion")?.toDate()?.time ?: 0L
            val rachaActual = (doc.getLong("racha") ?: 0L).toInt()
            
            val hoy = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            val unDiaMs = 24 * 60 * 60 * 1000L
            
            val diff = hoy - ultimaConexion
            
            val nuevaRacha = when {
                diff < unDiaMs -> rachaActual // Ya se conectó hoy o hace menos de 24h
                diff < 2 * unDiaMs -> rachaActual + 1 // Se conectó ayer, racha continúa
                else -> 1 // Se perdió la racha, reinicia a 1
            }

            docRef.update(
                mapOf(
                    "racha" to nuevaRacha,
                    "ultimaConexion" to com.google.firebase.Timestamp.now()
                )
            ).await()
            
            nuevaRacha
        } catch (e: Exception) {
            0
        }
    }

    actual suspend fun getUserProfile(): Result<Map<String, Any>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No user logged in"))
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                Result.success(document.data ?: emptyMap())
            } else {
                Result.failure(Exception("User data not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun updateProfile(
        nombre: String,
        apellido: String,
        email: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            
            // Si el email cambió, solicitar verificación para el nuevo email (Reemplaza al deprecado updateEmail)
            if (email != user.email) {
                user.verifyBeforeUpdateEmail(email).await()
            }

            val updates = mapOf(
                "nombre" to nombre,
                "apellido" to apellido,
                "email" to email
            )
            firestore.collection("users").document(user.uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual fun logout() {
        auth.signOut()
    }
}
