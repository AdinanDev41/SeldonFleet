package com.example.seldonfleet.data.source

import com.example.seldonfleet.data.mapper.toDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FleetJsonParserTest {

    private val json = """
    {
      "schemaVersion": "1.0",
      "fleetId": "IGNORADO",
      "devices": [
        {
          "id": "DEV-0204", "model": "Galaxy A54", "soc": "Exynos 1380", "tier": "mid",
          "ramMb": 8192, "androidApi": 34, "batteryCapacityMah": 5000, "batteryLevelPct": 61,
          "screenOnMinutes": 186, "energyConsumed": 3773, "energyUnit": "mWh",
          "nominalVoltage": 3.85, "jankFramesPct": 4.1, "crashCount7d": 0,
          "lastSeen": "2026-10-20T02:50:44Z"
        },
        {
          "id": "DEV-X", "model": "Sem capacidade", "soc": "X", "tier": "low",
          "ramMb": 2048, "androidApi": 31, "batteryLevelPct": 10,
          "screenOnMinutes": 50, "energyConsumed": 100, "energyUnit": "mAh",
          "jankFramesPct": 1.0, "crashCount7d": 0, "lastSeen": "2026-10-20T02:50:44Z"
        }
      ]
    }
    """.trimIndent()

    @Test fun leTodosOsRegistrosIgnorandoCamposExtras() {
        assertEquals(2, FleetJsonParser().parse(json).size)
    }

    @Test fun registroEmMwhChegaConvertidoAoDominio() {
        val device = FleetJsonParser().parse(json)[0].toDomain()
        assertEquals(980.0, device.energyMah!!, 1e-6)
    }

    @Test fun capacidadeAusenteChegaComoNula() {
        val device = FleetJsonParser().parse(json)[1].toDomain()
        assertNull(device.batteryCapacityMah)
    }
}