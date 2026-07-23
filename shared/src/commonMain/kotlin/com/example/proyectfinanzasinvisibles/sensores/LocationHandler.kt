package com.example.proyectfinanzasinvisibles.sensores

/**
 * Interface para manejar la lógica de geolocalización de forma multiplataforma.
 */
interface LocationHandler {
    fun requestLocationPermission()
    fun getCurrentLocation(onSuccess: (latitude: Double, longitude: Double) -> Unit)
}
