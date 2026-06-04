package com.example.proyectfinanzasinvisibles

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.proyectfinanzasinvisibles.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.network.GastoRepository

// SERVICIO SIMPLE: Se ejecuta en segundo plano para leer notificaciones bancarias
// Implementa la unión real entre el Front (UI) y el Almacenamiento/Backend (Repository)
class NotificationReaderService : NotificationListenerService() {

    private val repository = GastoRepository(GastoDatabase)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val extras = sbn?.notification?.extras ?: return
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        
        // Filtramos para leer solo mensajes que hablen de dinero o cargos
        if (text.contains("Cargo", ignoreCase = true) || text.contains("$")) {
            Log.d("FinanzasInvisibles", "Gasto Hormiga Detectado: $text")

            // REQUERIMIENTO: Unión de Front con Backend
            // Aquí simulamos la extracción del monto y lo guardamos automáticamente
            // En una app real, usaríamos Regex para extraer el número exacto.
            repository.agregarNuevoGasto("Notificación: Cargo Detectado", 15.0)
            
            Log.d("FinanzasInvisibles", "Gasto registrado automáticamente desde el servicio.")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
