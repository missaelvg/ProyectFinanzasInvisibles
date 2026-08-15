package com.example.proyectfinanzasinvisibles

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.backend.ai.GeminiHelper
import com.example.proyectfinanzasinvisibles.backend.ai.ExpenseParser
import com.example.proyectfinanzasinvisibles.backend.repositories.GastoRepository
import com.example.proyectfinanzasinvisibles.sync.backend.WorkManagerScheduler
import com.example.proyectfinanzasinvisibles.sync.backend.PendingExpenseStore
import com.example.proyectfinanzasinvisibles.backend.repositories.AuthRepository
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationReaderService : NotificationListenerService() {

    private val geminiHelper = GeminiHelper()
    private val gastoRepository = GastoRepository()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val processedNotifications = LinkedHashSet<String>()
    
    private val CHANNEL_ID = "FinanzasInvisiblesServiceChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val notification = sbn ?: return
        if (!AuthRepository().isUserLoggedIn()) return
        val packageName = notification.packageName
        // EVITAR BUCLE: Si la notificación es de nuestra propia app, ignorarla
        if (packageName == this.packageName) return

        val extras = notification.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val fullText = "$title $text"
        
        val fingerprint = "${notification.key}:${notification.postTime}"
        synchronized(processedNotifications) {
            if (!processedNotifications.add(fingerprint)) return
            if (processedNotifications.size > 100) {
                processedNotifications.remove(processedNotifications.first())
            }
        }

        if (ExpenseParser.isExpenseNotification(fullText)) {
            
            Log.d("FinanzasInvisibles", "Posible gasto detectado en $packageName")

            serviceScope.launch {
                try {
                    val clasificacion = geminiHelper.clasificarConDetalle(fullText) ?: return@launch
                    val nuevoGasto = clasificacion.gasto.copy(
                        id = "notif-${notification.key.hashCode()}-${notification.postTime}"
                    )

                    // Primero se conserva localmente; la red nunca decide si el gasto se pierde.
                    withContext(Dispatchers.Main) { GastoDatabase.guardarGastoLocal(nuevoGasto) }
                    PendingExpenseStore.save(applicationContext, nuevoGasto)
                    val exitoSync = gastoRepository.sincronizarGasto(nuevoGasto)
                    if (exitoSync) {
                        withContext(Dispatchers.Main) { GastoDatabase.marcarSincronizado(nuevoGasto.id) }
                        PendingExpenseStore.remove(applicationContext, nuevoGasto.id)
                    } else {
                        WorkManagerScheduler.programarSincronizacionInmediata(applicationContext)
                    }
                    Log.d("FinanzasInvisibles", "Gasto guardado. IA=${clasificacion.usedAi}, nube=$exitoSync")
                    showGastoNotification(nuevoGasto.descripcion, nuevoGasto.monto, exitoSync)
                } catch (e: Exception) {
                    Log.e("FinanzasInvisibles", "Error procesando notificación", e)
                }
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Avisos de gastos detectados",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun showGastoNotification(desc: String, monto: Double, synced: Boolean) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Gasto pendiente de revisión")
            .setContentText("$desc: $${"%.2f".format(monto)}${if (synced) "" else " · se sincronizará después"}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
