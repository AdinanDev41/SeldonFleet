package com.example.seldonfleet.domain.usecase

import com.example.seldonfleet.domain.model.Classification
import com.example.seldonfleet.domain.model.ClassificationSignal
import com.example.seldonfleet.domain.model.Device
import com.example.seldonfleet.domain.model.DeviceStatus

class ClassifyDevice {

    operator fun invoke(device: Device): Classification {
        val capacity = device.batteryCapacityMah
        val energy = device.energyMah

        if (capacity == null || capacity <= 0) {
            return insufficient(ClassificationSignal.CAPACIDADE_INVALIDA)
        }
        if (device.screenOnMinutes <= 0) {
            return insufficient(ClassificationSignal.TEMPO_DE_TELA_ZERO)
        }
        if (energy == null) {
            return insufficient(ClassificationSignal.ENERGIA_INDISPONIVEL)
        }

        val absoluteDrain = energy / (device.screenOnMinutes / 60.0)
        val relativeDrain = absoluteDrain * 100.0 / capacity


        if (relativeDrain >= CRITICAL_DRAIN) {
            return classified(DeviceStatus.CRITICO, ClassificationSignal.DRENO_RELATIVO, relativeDrain, CRITICAL_DRAIN, relativeDrain)
        }
        if (device.crashCount7d >= CRITICAL_CRASHES) {
            return classified(DeviceStatus.CRITICO, ClassificationSignal.FALHAS_RECENTES, device.crashCount7d.toDouble(), CRITICAL_CRASHES.toDouble(), relativeDrain)
        }
        if (device.jankFramesPct >= CRITICAL_JANK) {
            return classified(DeviceStatus.CRITICO, ClassificationSignal.QUADROS_PERDIDOS, device.jankFramesPct, CRITICAL_JANK, relativeDrain)
        }


        if (relativeDrain >= WARNING_DRAIN) {
            return classified(DeviceStatus.ATENCAO, ClassificationSignal.DRENO_RELATIVO, relativeDrain, WARNING_DRAIN, relativeDrain)
        }
        if (device.crashCount7d >= WARNING_CRASHES) {
            return classified(DeviceStatus.ATENCAO, ClassificationSignal.FALHAS_RECENTES, device.crashCount7d.toDouble(), WARNING_CRASHES.toDouble(), relativeDrain)
        }
        if (device.jankFramesPct >= WARNING_JANK) {
            return classified(DeviceStatus.ATENCAO, ClassificationSignal.QUADROS_PERDIDOS, device.jankFramesPct, WARNING_JANK, relativeDrain)
        }


        return classified(DeviceStatus.OK, null, null, null, relativeDrain)
    }

    private fun insufficient(signal: ClassificationSignal) =
        Classification(DeviceStatus.INDETERMINADO, signal, null, null, null)

    private fun classified(
        status: DeviceStatus,
        signal: ClassificationSignal?,
        observed: Double?,
        threshold: Double?,
        relativeDrain: Double
    ) = Classification(status, signal, observed, threshold, relativeDrain)

    private companion object {
        const val CRITICAL_DRAIN = 12.0
        const val CRITICAL_CRASHES = 3
        const val CRITICAL_JANK = 15.0
        const val WARNING_DRAIN = 7.0
        const val WARNING_CRASHES = 1
        const val WARNING_JANK = 8.0
    }
}