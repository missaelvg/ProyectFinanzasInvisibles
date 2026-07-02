package com.example.proyectfinanzasinvisibles.metas.viewmodel

import com.example.proyectfinanzasinvisibles.metas.data.MetaRepository
import com.example.proyectfinanzasinvisibles.metas.model.MetaAhorro

class MetasViewModel {
    private val repository = MetaRepository()
    val metaActual: MetaAhorro = repository.obtenerMetaActual()

    fun calcularPorcentajeProgreso(): Float {
        if (metaActual.montoObjetivo == 0.0) return 0f
        return (metaActual.montoAcumulado / metaActual.montoObjetivo).toFloat()
    }
}