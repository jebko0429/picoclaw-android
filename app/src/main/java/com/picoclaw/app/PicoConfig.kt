package com.picoclaw.app

object PicoConfig {
    // Default backend endpoint. Point to your remote PicoClaw service.
    const val backendHost = "your.backend.host"
    const val backendPort = 11434

    // Asset fallback for WebView when backend is unreachable.
    const val assetFallback = "file:///android_asset/index.html"
}
