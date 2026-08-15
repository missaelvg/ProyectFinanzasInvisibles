package com.example.proyectfinanzasinvisibles.backend.repositories

actual class AuthRepository {
    actual fun isUserLoggedIn(): Boolean = false
    actual suspend fun getUserProfile(): Result<Map<String, Any>> = Result.success(emptyMap())
    actual suspend fun signUp(email: String, password: String, nombre: String, apellido: String, fechaNacimiento: String, ciudad: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("Firebase Auth está disponible en la versión Android"))
    actual suspend fun updateProfile(nombre: String, apellido: String, email: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("Edición de perfil disponible en Android"))
    actual suspend fun reauthenticate(currentPassword: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("Reautenticación disponible en Android"))
    actual suspend fun updatePassword(newPassword: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("Cambio de contraseña disponible en Android"))
    actual suspend fun login(email: String, password: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("Inicio de sesión disponible en Android"))
    actual suspend fun actualizarRacha(): Int = 0
    actual fun logout() = Unit
}

