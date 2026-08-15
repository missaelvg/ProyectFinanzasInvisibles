package com.example.proyectfinanzasinvisibles.backend.repositories

expect class AuthRepository() {
    fun isUserLoggedIn(): Boolean
    suspend fun getUserProfile(): Result<Map<String, Any>>
    suspend fun signUp(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        fechaNacimiento: String,
        ciudad: String
    ): Result<Unit>
    suspend fun updateProfile(nombre: String, apellido: String, email: String): Result<Unit>
    suspend fun reauthenticate(currentPassword: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun actualizarRacha(): Int
    fun logout()
}
