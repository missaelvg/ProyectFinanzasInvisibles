package com.example.proyectfinanzasinvisibles

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
