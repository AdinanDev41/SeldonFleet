package com.example.seldonfleet.data.dto

data class FleetDto(
    val schemaVersion: String?,
    val exportedAt: String?,
    val devices: List<DeviceDto>?
)