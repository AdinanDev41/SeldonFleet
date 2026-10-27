package com.example.seldonfleet.data.source

import com.example.seldonfleet.data.dto.DeviceDto
import com.example.seldonfleet.data.dto.FleetDto
import com.google.gson.Gson

class FleetJsonParser(private val gson: Gson = Gson()) {
    fun parse(json: String): List<DeviceDto> =
        gson.fromJson(json, FleetDto::class.java)?.devices.orEmpty()
}