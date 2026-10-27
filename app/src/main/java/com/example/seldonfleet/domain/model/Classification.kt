package com.example.seldonfleet.domain.model

enum class ClassificationSignal {
    DRENO_RELATIVO,
    FALHAS_RECENTES,
    QUADROS_PERDIDOS,
    CAPACIDADE_INVALIDA,
    TEMPO_DE_TELA_ZERO,
    ENERGIA_INDISPONIVEL
}

data class Classification(
    val status: DeviceStatus,
    val signal: ClassificationSignal?,
    val observedValue: Double?,
    val threshold: Double?,
    val relativeDrainPctPerHour: Double?
)