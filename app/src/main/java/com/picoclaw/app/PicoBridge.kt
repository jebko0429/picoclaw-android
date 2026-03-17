package com.picoclaw.app

import android.content.Context

/**
 * Reflection-based shim to the gomobile-generated class (expected: io.picoclaw.Picoclaw).
 * This lets the app compile even when the AAR isn't present yet.
 */
object PicoBridge {
    private const val CLASS_NAME = "io.picoclaw.Picoclaw"
    private var cachedClazz: Class<*>? = null

    fun isAvailable(): Boolean = loadClass() != null

    fun start(context: Context): Boolean {
        val cls = loadClass() ?: return false
        return try {
            val m = cls.getMethod("start", Context::class.java, String::class.java)
            // Pass app context and an internal data directory for the backend.
            m.invoke(null, context.applicationContext, context.filesDir.absolutePath)
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    fun stop(): Boolean {
        val cls = loadClass() ?: return false
        return try {
            val m = cls.getMethod("stop")
            m.invoke(null)
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }
    }

    private fun loadClass(): Class<*>? {
        if (cachedClazz != null) return cachedClazz
        return try {
            Class.forName(CLASS_NAME).also { cachedClazz = it }
        } catch (_: Throwable) {
            null
        }
    }
}
