package com.fernando.appmobile.performance

data class RealtimeMetrics(
    val systemCpuPercent: Int,
    val usedRamMb: Int,
    val freeRamMb: Int,
    val batteryTempC: Float?
)
