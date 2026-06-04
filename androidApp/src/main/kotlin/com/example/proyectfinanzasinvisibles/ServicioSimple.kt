package com.example.proyectfinanzasinvisibles

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

// SERVICIO SIMPLE: Se ejecuta en segundo plano para leer notificaciones bancarias
class NotificationReaderService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val extras = sbn?.notification?.extras ?: return
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val title = extras.getCharSequence("android.title")?.toString() ?: ""

        // Filtramos para leer solo mensajes que hablen de dinero o cargos
        if (text.contains("Cargo", ignoreCase = true) || text.contains("$")) {
            Log.d("FinanzasInvisibles", "Gasto Hormiga Detectado: $text")

            // Aquí mandaríamos el texto interceptado a la API
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)

    }
}
