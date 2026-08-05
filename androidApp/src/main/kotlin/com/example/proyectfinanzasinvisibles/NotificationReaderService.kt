package com.example.proyectfinanzasinvisibles

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
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

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val extras = sbn?.notification?.extras ?: return
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val fullText = "$title $text"
        
        if (text.contains("Cargo", ignoreCase = true) || text.contains("$") || text.contains("compra", ignoreCase = true)) {
            Log.d("FinanzasInvisibles", "Notificación detectada: $fullText")

            serviceScope.launch {
                try {
                    // 1. Clasificación con IA (Gemini Simulado)
                    val nuevoGasto = geminiHelper.clasificarGasto(fullText)
                    
                    // 2. Persistencia en la nube (Firestore)
                    val exitoSync = gastoRepository.sincronizarGasto(nuevoGasto)
                    
                    if (exitoSync) {
                        // 3. Guardado local para UI reactiva
                        GastoDatabase.guardarGastoLocal(nuevoGasto)
                        Log.d("FinanzasInvisibles", "Gasto clasificado y sincronizado: ${nuevoGasto.tipo}")
                    }
                } catch (e: Exception) {
                    Log.e("FinanzasInvisibles", "Error procesando notificación", e)
                }
            }
        }
    }
}
