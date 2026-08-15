package com.example.proyectfinanzasinvisibles.backend.repositories

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldValue
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
            try {
                firestore.collection("users").document(user.uid).set(userData).await()
            } catch (e: Exception) {
                runCatching { user.delete().await() }
                return Result.failure(e)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun actualizarRacha(): Int {
        return try {
            val uid = auth.currentUser?.uid ?: return 0
            val docRef = firestore.collection("users").document(uid)
            firestore.runTransaction { transaction ->
                val doc = transaction.get(docRef)
                if (!doc.exists()) return@runTransaction 0
                val lastMillis = doc.getTimestamp("ultimaConexion")?.toDate()?.time ?: 0L
                val currentStreak = (doc.getLong("racha") ?: 0L).toInt()
                val today = dayNumber(System.currentTimeMillis())
                val lastDay = dayNumber(lastMillis)
                val newStreak = when (today - lastDay) {
                    0L -> currentStreak.coerceAtLeast(1)
                    1L -> currentStreak.coerceAtLeast(0) + 1
                    else -> 1
                }
                transaction.update(docRef, mapOf(
                    "racha" to newStreak,
                    "ultimaConexion" to com.google.firebase.Timestamp.now()
                ))
                newStreak
            }.await()
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

            val updates = mutableMapOf<String, Any>(
                "nombre" to nombre,
                "apellido" to apellido
            )
            if (email != user.email) updates["emailPendiente"] = email
            else updates["email"] = email
            firestore.collection("users").document(user.uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun reauthenticate(currentPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No hay una sesión activa"))
            val email = user.email ?: return Result.failure(Exception("La cuenta no usa correo electrónico"))
            user.reauthenticate(EmailAuthProvider.getCredential(email, currentPassword)).await()
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
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            if (user != null) {
                runCatching {
                    firestore.collection("users").document(user.uid).update(
                        mapOf("email" to (user.email ?: email), "emailPendiente" to FieldValue.delete())
                    ).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual fun logout() {
        auth.signOut()
    }

    private fun dayNumber(timestamp: Long): Long {
        val local = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        val normalized = java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                local.get(java.util.Calendar.YEAR),
                local.get(java.util.Calendar.MONTH),
                local.get(java.util.Calendar.DAY_OF_MONTH)
            )
        }
        return normalized.timeInMillis / 86_400_000L
    }
}
