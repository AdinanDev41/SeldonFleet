package com.example.seldonfleet.data.dto

data class DeviceDto(
    val id: String,
    val model: String,
    val soc: String,
    val tier: String,
    val ramMb: Int,
    val androidApi: Int,
    val batteryCapacityMah: Int?,
    val batteryLevelPct: Int,
    val screenOnMinutes: Int?,
    val energyConsumed: Double?,
    val energyUnit: String?,
    val nominalVoltage: Double?,
    val jankFramesPct: Double,
    val crashCount7d: Int,
    val lastSeen: String
)