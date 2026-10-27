package com.example.seldonfleet.domain.usecase

import com.example.seldonfleet.domain.model.ClassificationSignal
import com.example.seldonfleet.domain.model.Device
import com.example.seldonfleet.domain.model.DeviceStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ClassifyDeviceTest {

    private val classify = ClassifyDevice()

    private fun device(
        capacity: Int? = 6000,
        screenOnMinutes: Int = 60,
        energyMah: Double? = 0.0,
        jank: Double = 0.0,
        crashes: Int = 0
    ) = Device(
        id = "DEV-TEST", model = "Test", soc = "Test", tier = "mid",
        ramMb = 4096, androidApi = 34,
        batteryCapacityMah = capacity, batteryLevelPct = 50,
        screenOnMinutes = screenOnMinutes, energyMah = energyMah,
        jankFramesPct = jank, crashCount7d = crashes,
        lastSeen = "2026-10-20T02:58:10Z"
    )


    @Test fun capacidadeZero_eIndeterminado() {
        assertEquals(DeviceStatus.INDETERMINADO, classify(device(capacity = 0)).status)
    }

    @Test fun capacidadeAusente_eIndeterminado() {
        assertEquals(DeviceStatus.INDETERMINADO, classify(device(capacity = null)).status)
    }

    @Test fun telaZero_eIndeterminado() {
        assertEquals(DeviceStatus.INDETERMINADO, classify(device(screenOnMinutes = 0)).status)
    }

    @Test fun energiaIndisponivel_eIndeterminado() {
        assertEquals(DeviceStatus.INDETERMINADO, classify(device(energyMah = null)).status)
    }

    @Test fun indeterminadoVenceSinaisCriticos() {
        val result = classify(device(capacity = 0, crashes = 5, jank = 20.0))
        assertEquals(DeviceStatus.INDETERMINADO, result.status)
    }


    @Test fun drenoLogoAbaixoDeSete_eOk() {
        assertEquals(DeviceStatus.OK, classify(device(energyMah = 419.99)).status)
    }

    @Test fun drenoExatamenteSete_eAtencao() {
        assertEquals(DeviceStatus.ATENCAO, classify(device(energyMah = 420.0)).status)
    }

    @Test fun drenoLogoAbaixoDeDoze_eAtencao() {
        assertEquals(DeviceStatus.ATENCAO, classify(device(energyMah = 719.99)).status)
    }

    @Test fun drenoExatamenteDoze_eCritico() {
        assertEquals(DeviceStatus.CRITICO, classify(device(energyMah = 720.0)).status)
    }


    @Test fun zeroFalhas_eOk() {
        assertEquals(DeviceStatus.OK, classify(device(crashes = 0)).status)
    }

    @Test fun umaFalha_eAtencao() {
        assertEquals(DeviceStatus.ATENCAO, classify(device(crashes = 1)).status)
    }

    @Test fun duasFalhas_aindaAtencao() {
        assertEquals(DeviceStatus.ATENCAO, classify(device(crashes = 2)).status)
    }

    @Test fun tresFalhas_eCritico() {
        assertEquals(DeviceStatus.CRITICO, classify(device(crashes = 3)).status)
    }


    @Test fun jankLogoAbaixoDeOito_eOk() {
        assertEquals(DeviceStatus.OK, classify(device(jank = 7.99)).status)
    }

    @Test fun jankExatamenteOito_eAtencao() {
        assertEquals(DeviceStatus.ATENCAO, classify(device(jank = 8.0)).status)
    }

    @Test fun jankLogoAbaixoDeQuinze_eAtencao() {
        assertEquals(DeviceStatus.ATENCAO, classify(device(jank = 14.99)).status)
    }

    @Test fun jankExatamenteQuinze_eCritico() {
        assertEquals(DeviceStatus.CRITICO, classify(device(jank = 15.0)).status)
    }


    @Test fun sinalCriticoVenceSinalDeAtencao() {

        val result = classify(device(energyMah = 480.0, crashes = 3))
        assertEquals(DeviceStatus.CRITICO, result.status)
        assertEquals(ClassificationSignal.FALHAS_RECENTES, result.signal)
    }


    @Test fun anexoA_dev0137_eAtencaoPorFalhas() {
        val result = classify(
            device(capacity = 4410, screenOnMinutes = 214, energyMah = 980.0, jank = 7.4, crashes = 2)
        )
        assertEquals(DeviceStatus.ATENCAO, result.status)
        assertEquals(ClassificationSignal.FALHAS_RECENTES, result.signal)
    }

    @Test fun anexoA_dev0204_eOkAposConversao() {
        val result = classify(
            device(capacity = 5000, screenOnMinutes = 186, energyMah = 980.0, jank = 4.1, crashes = 0)
        )
        assertEquals(DeviceStatus.OK, result.status)
    }

    @Test fun anexoA_dev0311_capacidadeZero_eIndeterminado() {
        val result = classify(
            device(capacity = 0, screenOnMinutes = 97, energyMah = 612.0, jank = 11.2, crashes = 1)
        )
        assertEquals(DeviceStatus.INDETERMINADO, result.status)
    }
}