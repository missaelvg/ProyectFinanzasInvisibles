package com.example.proyectfinanzasinvisibles

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.backend.ai.GeminiHelper
import com.example.proyectfinanzasinvisibles.backend.repositories.GastoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReaderService : NotificationListenerService() {

    private val geminiHelper = GeminiHelper()
    private val gastoRepository = GastoRepository()
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    private val CHANNEL_ID = "FinanzasInvisiblesServiceChannel"
    private val NOTIFICATION_ID = 1001

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // Mostrar notificación persistente de que el servicio está activo
        startForeground(NOTIFICATION_ID, createPersistentNotification())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName ?: ""
        // EVITAR BUCLE: Si la notificación es de nuestra propia app, ignorarla
        if (packageName == "com.example.proyectfinanzasinvisibles") return

        val extras = sbn?.notification?.extras ?: return
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val fullText = "$title $text"
        
        // Filtro básico para detectar notificaciones de gastos
        if (text.contains("Cargo", ignoreCase = true) || 
            text.contains("$") || 
            text.contains("compra", ignoreCase = true) ||
            text.contains("pago", ignoreCase = true)) {
            
            Log.d("FinanzasInvisibles", "Notificación detectada: $fullText")

            serviceScope.launch {
                try {
                    // 1. Clasificación con IA
                    val nuevoGasto = geminiHelper.clasificarGasto(fullText)
                    
                    // 2. Persistencia en la nube (Firestore)
                    val exitoSync = gastoRepository.sincronizarGasto(nuevoGasto)
                    
                    if (exitoSync) {
                        // 3. Guardado local para UI reactiva
                        GastoDatabase.guardarGastoLocal(nuevoGasto)
                        Log.d("FinanzasInvisibles", "Gasto clasificado y sincronizado: ${nuevoGasto.tipo}")
                        
                        // Opcional: Mostrar una notificación de que se registró el gasto
                        showGastoNotification(nuevoGasto.descripcion, nuevoGasto.monto)
                    }
                } catch (e: Exception) {
                    Log.e("FinanzasInvisibles", "Error procesando notificación", e)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Servicio de Lector de Gastos IA",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createPersistentNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Finanzas Invisibles")
            .setContentText("Lector de gastos activo en segundo plano")
            .setSmallIcon(android.R.drawable.ic_menu_compass) // Usar un icono genérico
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun showGastoNotification(desc: String, monto: Double) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Gasto Detectado por IA")
            .setContentText("$desc: $monto")
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
