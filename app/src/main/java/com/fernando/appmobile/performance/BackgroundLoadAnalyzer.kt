package com.fernando.appmobile.performance

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import java.util.concurrent.TimeUnit

class BackgroundLoadAnalyzer(private val context: Context) {

    fun findHeavyRecentApps(limit: Int = 8): List<String> {
        val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return emptyList()

        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.MINUTES.toMillis(30)

        val stats = usageManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ).orEmpty()

        if (stats.isEmpty()) {
            return emptyList()
        }

        val packageManager = context.packageManager
        return stats
            .filter { it.packageName != context.packageName }
            .sortedByDescending { it.totalTimeInForeground }
            .take(limit)
            .map { usage ->
                runCatching {
                    val appInfo = packageManager.getApplicationInfo(usage.packageName, 0)
                    packageManager.getApplicationLabel(appInfo).toString()
                }.getOrElse {
                    usage.packageName
                }
            }
    }
}
