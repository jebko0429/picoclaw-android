package com.picoclaw.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class PicoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PicoService.CHANNEL_ID,
                "PicoClaw Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps PicoClaw running in the background."
            }
            val mgr = getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }
}
