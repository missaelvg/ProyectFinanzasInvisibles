package com.example.proyectfinanzasinvisibles.auth.backend

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
        fechaNacimiento: String
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val userData = mapOf(
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "fechaNacimiento" to fechaNacimiento,
                    "email" to email,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                firestore.collection("users").document(user.uid).set(userData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
            
            // Si el email cambió, actualizarlo en Firebase Auth
            if (email != user.email) {
                user.updateEmail(email).await()
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
