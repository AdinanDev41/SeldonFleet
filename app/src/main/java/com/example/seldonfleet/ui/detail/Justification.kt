package com.example.seldonfleet.ui.detail

import com.example.seldonfleet.domain.model.Classification
import com.example.seldonfleet.domain.model.ClassificationSignal
import com.example.seldonfleet.domain.model.DeviceStatus
import com.example.seldonfleet.ui.statusLabel
import java.util.Locale

private val ptBr: Locale = Locale.forLanguageTag("pt-BR")

fun formatNumber(value: Double, decimals: Int): String =
    String.format(ptBr, "%.${decimals}f", value)

fun justificationText(c: Classification): String {
    val label = statusLabel(c.status)

    when (c.status) {
        DeviceStatus.INDETERMINADO -> return when (c.signal) {
            ClassificationSignal.CAPACIDADE_INVALIDA ->
                "$label: capacidade da bateria ausente ou igual a zero; o dreno relativo não pode ser calculado."
            ClassificationSignal.TEMPO_DE_TELA_ZERO ->
                "$label: tempo de tela igual a zero; o dreno não pode ser calculado."
            ClassificationSignal.ENERGIA_INDISPONIVEL ->
                "$label: consumo de energia indisponível (unidade desconhecida ou mWh sem tensão nominal)."
            else -> "$label: dados insuficientes para classificar."
        }
        DeviceStatus.OK -> {
            val drain = c.relativeDrainPctPerHour
            return if (drain != null) {
                "$label: nenhum sinal atingiu os limiares (dreno de ${formatNumber(drain, 1)} %/h)."
            } else {
                "$label: nenhum sinal atingiu os limiares."
            }
        }
        else -> Unit
    }

    val observed = c.observedValue ?: return "$label."
    val threshold = c.threshold ?: return "$label."

    return when (c.signal) {
        ClassificationSignal.DRENO_RELATIVO ->
            "$label: dreno de ${formatNumber(observed, 1)} %/h, igual ou acima do limiar de ${formatNumber(threshold, 0)} %/h."
        ClassificationSignal.FALHAS_RECENTES ->
            "$label: ${observed.toInt()} falha(s) nos últimos 7 dias, igual ou acima do limiar de ${threshold.toInt()}."
        ClassificationSignal.QUADROS_PERDIDOS ->
            "$label: ${formatNumber(observed, 1)} % de quadros perdidos, igual ou acima do limiar de ${formatNumber(threshold, 0)} %."
        else -> "$label."
    }
}