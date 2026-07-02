package com.example.proyectfinanzasinvisibles.sync.backend

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectfinanzasinvisibles.data.GastoDatabase

class SyncGastosWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SyncGastosWorker", "Sincronizando gastos con el backend...")
        
        // Simulación de sincronización
        val gastos = GastoDatabase.obtenerGastosLocales()
        Log.d("SyncGastosWorker", "Se encontraron ${gastos.size} gastos para sincronizar.")
        
        // Aquí iría la lógica de llamada a API
        
        return Result.success()
    }
}
