package com.fernando.appmobile.performance

import android.content.Context
import android.os.Build
import android.os.PerformanceHintManager
import java.util.concurrent.atomic.AtomicBoolean

class PerformanceHintController(private val context: Context) {

    private var session: PerformanceHintManager.Session? = null
    private val active = AtomicBoolean(false)

    fun startGamingSession(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || active.get()) {
            return active.get()
        }

        val manager = context.getSystemService(PerformanceHintManager::class.java) ?: return false
        val threadId = android.os.Process.myTid()
        session = manager.createHintSession(intArrayOf(threadId), 16_666_666L)
        session?.updateTargetWorkDuration(16_666_666L)
        active.set(session != null)
        return active.get()
    }

    fun stopGamingSession() {
        session?.close()
        session = null
        active.set(false)
    }
}
