package com.picoclaw.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.picoclaw.app.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private val requestNotif =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // Optional: show status to user.
            binding.status.text = if (granted) "Notifications allowed" else "Notifications denied"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.status.text = "Backend URL: http://${PicoConfig.backendHost}:${PicoConfig.backendPort}"

        binding.startServiceBtn.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                Intent(this, PicoService::class.java)
            )
            maybeRequestNotifications()
            maybeRequestBatteryWhitelist()
        }

        binding.stopServiceBtn.setOnClickListener {
            stopService(Intent(this, PicoService::class.java))
        }

        binding.openUiBtn.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }
    }

    private fun maybeRequestNotifications() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun maybeRequestBatteryWhitelist() {
        val pm = getSystemService(PowerManager::class.java)
        val pkg = packageName
        if (!pm.isIgnoringBatteryOptimizations(pkg)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$pkg")
            }
            startActivity(intent)
        }
    }
}
