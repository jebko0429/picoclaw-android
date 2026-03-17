package com.picoclaw.app.keepalive

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.picoclaw.app.PicoService
import java.util.concurrent.TimeUnit

class PicoKeepAliveWorker(appContext: Context, params: androidx.work.WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        // Ensure service is running; if killed, start it again.
        ContextCompat.startForegroundService(
            applicationContext,
            Intent(applicationContext, PicoService::class.java)
        )
        return Result.success()
    }

    companion object {
        private const val UNIQUE_NAME = "pico_keep_alive"

        fun schedule(context: Context) {
            val req = PeriodicWorkRequestBuilder<PicoKeepAliveWorker>(
                15, TimeUnit.MINUTES
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }
    }
}
