package com.example.proyectfinanzasinvisibles.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationReaderService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val packageName = sbn?.packageName
        val tickerText = sbn?.notification?.tickerText
        val extras = sbn?.notification?.extras
        val title = extras?.getString("android.title")
        val text = extras?.getCharSequence("android.text")

        Log.d("NotificationReader", "Notification from: $packageName")
        Log.d("NotificationReader", "Ticker: $tickerText")
        Log.d("NotificationReader", "Title: $title")
        Log.d("NotificationReader", "Text: $text")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d("NotificationReader", "Notification removed")
    }
}
