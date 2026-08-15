package com.example.proyectfinanzasinvisibles.sync.backend

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectfinanzasinvisibles.backend.data.GastoDatabase
import com.example.proyectfinanzasinvisibles.backend.repositories.GastoRepository
import com.example.proyectfinanzasinvisibles.backend.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncGastosWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (!AuthRepository().isUserLoggedIn()) return Result.success()
        Log.d("SyncGastosWorker", "Sincronizando gastos con el backend...")
        
        val gastos = (PendingExpenseStore.getAll(applicationContext) + GastoDatabase.obtenerPendientesDeSincronizar())
            .associateBy { it.id }
            .values
        Log.d("SyncGastosWorker", "Se encontraron ${gastos.size} gastos para sincronizar.")
        if (gastos.isEmpty()) return Result.success()

        val repository = GastoRepository()
        var failed = false
        gastos.forEach { gasto ->
            if (repository.sincronizarGasto(gasto)) {
                withContext(Dispatchers.Main) { GastoDatabase.marcarSincronizado(gasto.id) }
                PendingExpenseStore.remove(applicationContext, gasto.id)
            } else {
                failed = true
            }
        }

        return if (failed) Result.retry() else Result.success()
    }
}
