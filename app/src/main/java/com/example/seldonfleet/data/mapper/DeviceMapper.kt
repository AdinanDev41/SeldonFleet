package com.example.seldonfleet.data.mapper

import com.example.seldonfleet.data.dto.DeviceDto
import com.example.seldonfleet.domain.model.Device

fun DeviceDto.toDomain(): Device = Device(
    id = id,
    model = model,
    soc = soc,
    tier = tier,
    ramMb = ramMb,
    androidApi = androidApi,
    batteryCapacityMah = batteryCapacityMah,
    batteryLevelPct = batteryLevelPct,
    screenOnMinutes = screenOnMinutes ?: 0,
    energyMah = energyInMah(),
    jankFramesPct = jankFramesPct,
    crashCount7d = crashCount7d,
    lastSeen = lastSeen
)

private fun DeviceDto.energyInMah(): Double? {
    val energy = energyConsumed ?: return null
    return when (energyUnit) {
        "mAh" -> energy
        "mWh" -> {
            val voltage = nominalVoltage
            if (voltage != null && voltage > 0.0) energy / voltage else null
        }
        else -> null
    }
}