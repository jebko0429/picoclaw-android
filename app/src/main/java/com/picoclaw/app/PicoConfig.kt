package com.picoclaw.app

object PicoConfig {
    // Default loopback backend endpoint. Update if your Go service binds a different port.
    const val backendHost = "127.0.0.1"
    const val backendPort = 11434

    // Asset fallback for WebView when backend is unreachable.
    const val assetFallback = "file:///android_asset/index.html"
}
