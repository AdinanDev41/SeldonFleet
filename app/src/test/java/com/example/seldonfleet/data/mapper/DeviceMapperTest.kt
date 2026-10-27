package com.example.seldonfleet.data.mapper

import com.example.seldonfleet.data.dto.DeviceDto
import com.example.seldonfleet.domain.model.DeviceStatus
import com.example.seldonfleet.domain.usecase.ClassifyDevice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeviceMapperTest {

    private fun dto(
        energy: Double? = 980.0,
        unit: String? = "mAh",
        voltage: Double? = 3.85
    ) = DeviceDto(
        id = "DEV-TEST", model = "Test", soc = "Test", tier = "mid",
        ramMb = 4096, androidApi = 34,
        batteryCapacityMah = 5000, batteryLevelPct = 50,
        screenOnMinutes = 186, energyConsumed = energy,
        energyUnit = unit, nominalVoltage = voltage,
        jankFramesPct = 4.1, crashCount7d = 0,
        lastSeen = "2026-10-20T02:58:10Z"
    )

    @Test fun mwhEConvertidoParaMah() {
        val device = dto(energy = 3773.0, unit = "mWh").toDomain()
        assertEquals(980.0, device.energyMah!!, 1e-6)
    }

    @Test fun mesmoConsumoFisico_geraMesmoDrenoEMesmoStatus() {
        val classify = ClassifyDevice()
        val emMah = classify(dto(energy = 980.0, unit = "mAh").toDomain())
        val emMwh = classify(dto(energy = 3773.0, unit = "mWh").toDomain())

        assertEquals(emMah.relativeDrainPctPerHour!!, emMwh.relativeDrainPctPerHour!!, 1e-6)
        assertEquals(emMah.status, emMwh.status)
        assertEquals(DeviceStatus.OK, emMwh.status)
    }

    @Test fun mwhSemTensao_viraEnergiaNula() {
        assertNull(dto(energy = 3773.0, unit = "mWh", voltage = null).toDomain().energyMah)
    }

    @Test fun mwhComTensaoZero_viraEnergiaNula() {
        assertNull(dto(energy = 3773.0, unit = "mWh", voltage = 0.0).toDomain().energyMah)
    }

    @Test fun unidadeDesconhecida_viraEnergiaNula() {
        assertNull(dto(unit = "joules").toDomain().energyMah)
    }

    @Test fun mwhSemTensao_resultaEmIndeterminado() {
        val result = ClassifyDevice()(dto(energy = 3773.0, unit = "mWh", voltage = null).toDomain())
        assertEquals(DeviceStatus.INDETERMINADO, result.status)
    }
}