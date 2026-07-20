package com.example.proyectfinanzasinvisibles.auth.backend

expect class AuthRepository() {
    fun isUserLoggedIn(): Boolean
    suspend fun getUserProfile(): Result<Map<String, Any>>
    suspend fun signUp(email: String, password: String, nombre: String, apellido: String, fechaNacimiento: String): Result<Unit>
    suspend fun updateProfile(nombre: String, apellido: String, email: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    fun logout()
}
