package com.example.proyectfinanzasinvisibles.metas.data

import com.example.proyectfinanzasinvisibles.metas.model.MetaAhorro

class MetaRepository {
    fun obtenerMetaActual(): MetaAhorro {
        return MetaAhorro(
            idMeta = 1,
            titulo = "Evitar refrescos y cafés diarios",
            montoObjetivo = 500.00,
            montoAcumulado = 350.00,
            rachaActualDias = 5,
            mejorRachaDias = 12,
            mensajeMotivacional = "¡Vas excelente! Has evitado 5 cafés esta semana. Sigue así."
        )
    }
}