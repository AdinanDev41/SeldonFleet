package com.example.seldonfleet.ui.detail

import com.example.seldonfleet.domain.model.Classification
import com.example.seldonfleet.domain.model.ClassificationSignal
import com.example.seldonfleet.domain.model.DeviceStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JustificationTest {

    @Test fun criticoPorDreno_citaValorELimiar() {
        val c = Classification(DeviceStatus.CRITICO, ClassificationSignal.DRENO_RELATIVO, 13.4, 12.0, 13.4)
        assertEquals(
            "Crítico: dreno de 13,4 %/h, igual ou acima do limiar de 12 %/h.",
            justificationText(c)
        )
    }

    @Test fun semDados_explicaOMotivo() {
        val c = Classification(DeviceStatus.INDETERMINADO, ClassificationSignal.CAPACIDADE_INVALIDA, null, null, null)
        assertTrue(justificationText(c).contains("capacidade"))
    }
}