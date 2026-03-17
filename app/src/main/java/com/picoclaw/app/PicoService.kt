package com.picoclaw.app

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
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
            val ok = PicoBridge.start(applicationContext)
            if (!ok) {
                Log.w(TAG, "PicoBridge start failed; ensure picoclaw.aar is present and methods exported.")
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.launch {
            val ok = PicoBridge.stop()
            if (!ok) {
                Log.w(TAG, "PicoBridge stop failed; backend may not have been running.")
            }
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
        private const val TAG = "PicoService"
    }
}
