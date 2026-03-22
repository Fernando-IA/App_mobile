package com.fernando.appmobile.performance

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import java.io.RandomAccessFile

class RealtimeMonitor(private val context: Context) {

    private var previousTotal: Long = 0L
    private var previousIdle: Long = 0L

    fun readSnapshot(): RealtimeMetrics {
        val cpuPercent = readCpuUsagePercent()
        val (usedRamMb, freeRamMb) = readRamStatsMb()
        val batteryTemp = readBatteryTemperatureC()
        return RealtimeMetrics(
            systemCpuPercent = cpuPercent,
            usedRamMb = usedRamMb,
            freeRamMb = freeRamMb,
            batteryTempC = batteryTemp
        )
    }

    private fun readRamStatsMb(): Pair<Int, Int> {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(memoryInfo)
        val totalMb = (memoryInfo.totalMem / (1024 * 1024)).toInt()
        val freeMb = (memoryInfo.availMem / (1024 * 1024)).toInt()
        val usedMb = (totalMb - freeMb).coerceAtLeast(0)
        return usedMb to freeMb
    }

    private fun readBatteryTemperatureC(): Float? {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return null
        val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (rawTemp == Int.MIN_VALUE) {
            return null
        }
        return rawTemp / 10f
    }

    private fun readCpuUsagePercent(): Int {
        val stat = runCatching {
            RandomAccessFile("/proc/stat", "r").use { file -> file.readLine() }
        }.getOrNull() ?: return 0

        val chunks = stat.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (chunks.size < 8) {
            return 0
        }

        val user = chunks.getOrNull(1)?.toLongOrNull() ?: 0L
        val nice = chunks.getOrNull(2)?.toLongOrNull() ?: 0L
        val system = chunks.getOrNull(3)?.toLongOrNull() ?: 0L
        val idle = chunks.getOrNull(4)?.toLongOrNull() ?: 0L
        val iowait = chunks.getOrNull(5)?.toLongOrNull() ?: 0L
        val irq = chunks.getOrNull(6)?.toLongOrNull() ?: 0L
        val softirq = chunks.getOrNull(7)?.toLongOrNull() ?: 0L

        val total = user + nice + system + idle + iowait + irq + softirq
        if (previousTotal == 0L || previousIdle == 0L) {
            previousTotal = total
            previousIdle = idle
            return 0
        }

        val totalDiff = total - previousTotal
        val idleDiff = idle - previousIdle
        previousTotal = total
        previousIdle = idle
        if (totalDiff <= 0L) {
            return 0
        }

        val usage = ((totalDiff - idleDiff) * 100f / totalDiff).coerceIn(0f, 100f)
        return usage.toInt()
    }
}
