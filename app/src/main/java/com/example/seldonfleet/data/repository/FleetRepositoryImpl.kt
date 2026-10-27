package com.example.seldonfleet.data.repository

import com.example.seldonfleet.data.mapper.toDomain
import com.example.seldonfleet.data.source.AssetFleetSource
import com.example.seldonfleet.domain.model.Device
import com.example.seldonfleet.domain.repository.DeviceRepository

class FleetRepositoryImpl(
    private val source: AssetFleetSource
) : DeviceRepository {
    override fun getDevices(): List<Device> =
        source.load().map { it.toDomain() }
}