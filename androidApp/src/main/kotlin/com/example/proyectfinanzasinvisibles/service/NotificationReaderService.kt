package com.example.proyectfinanzasinvisibles.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.proyectfinanzasinvisibles.network.BackendLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReaderService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val packageName = sbn?.packageName
        val extras = sbn?.notification?.extras
        val title = extras?.getString("android.title")
        val text = extras?.getCharSequence("android.text")

        Log.d("NotificationReader", "Notification from: $packageName")
        Log.d("NotificationReader", "Title: $title")
        Log.d("NotificationReader", "Text: $text")

        // Sincronizar con el servidor local
        serviceScope.launch {
            val gastoInfo = "App: $packageName, Titulo: $title, Texto: $text"
            BackendLocal.postSincronizarGasto(gastoInfo)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d("NotificationReader", "Notification removed")
    }
}
