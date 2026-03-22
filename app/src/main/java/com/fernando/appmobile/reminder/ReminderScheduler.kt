package com.fernando.appmobile.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    fun schedulePreGameReminder(minutes: Long) {
        val request = OneTimeWorkRequestBuilder<PreGameReminderWorker>()
            .setInitialDelay(minutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "pre_game_reminder",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
