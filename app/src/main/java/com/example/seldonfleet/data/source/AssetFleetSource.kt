package com.example.seldonfleet.data.source

import android.content.Context
import com.example.seldonfleet.data.dto.DeviceDto

class AssetFleetSource(
    private val context: Context,
    private val parser: FleetJsonParser
) {
    fun load(): List<DeviceDto> {
        val json = context.assets.open("frota.json").bufferedReader().use { it.readText() }
        return parser.parse(json)
    }
}