package com.picoclaw.app

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PicoService : Service() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        scope.launch {
            // TODO: Initialize and start the gomobile-bound PicoClaw backend here.
            // Example (after gomobile bind):
            // PicoBridge.start(getApplicationContext().filesDir.absolutePath)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.launch {
            // TODO: Stop backend gracefully.
            // PicoBridge.stop()
        }
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PicoClaw running")
            .setContentText("Background AI service is active.")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(openAppIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "pico_service"
        private const val NOTIFICATION_ID = 42
    }
}
