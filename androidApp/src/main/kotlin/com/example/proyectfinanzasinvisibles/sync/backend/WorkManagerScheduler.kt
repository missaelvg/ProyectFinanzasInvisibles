package com.example.proyectfinanzasinvisibles.sync.backend

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {
    private const val SYNC_WORK_NAME = "SyncGastosWork"
    private const val IMMEDIATE_SYNC_WORK_NAME = "SyncGastosImmediate"

    fun programarSincronizacionInmediata(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncGastosWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun programarSincronizacionPeriodica(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncGastosWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
