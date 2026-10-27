package com.example.seldonfleet.domain.repository

import com.example.seldonfleet.domain.model.Device

interface DeviceRepository {
    fun getDevices(): List<Device>
}