@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.seldonfleet.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seldonfleet.ui.StatusChip
import com.example.seldonfleet.ui.list.DeviceItem

@Composable
fun DeviceDetailScreen(
    item: DeviceItem?,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.device?.id ?: "Dispositivo") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } },
                actions = {
                    if (item != null) {
                        TextButton(onClick = { onToggleFavorite(item.device.id) }) {
                            Text(if (item.isFavorite) "★ Favorito" else "☆ Favoritar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (item == null) {
            Text(
                "Dispositivo não encontrado.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
        } else {
            val d = item.device
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(d.model, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.width(12.dp))
                    StatusChip(item.classification.status)
                }
                Text(
                    justificationText(item.classification),
                    style = MaterialTheme.typography.bodyLarge
                )
                HorizontalDivider()
                AttributeRow("SoC", d.soc)
                AttributeRow("Memória", "${d.ramMb} MB")
                AttributeRow("Nível de API", d.androidApi.toString())
                AttributeRow(
                    "Capacidade da bateria",
                    d.batteryCapacityMah?.let { "$it mAh" } ?: "Indisponível"
                )
                AttributeRow("Nível da bateria", "${d.batteryLevelPct}%")
                AttributeRow(
                    "Consumo (normalizado)",
                    d.energyMah?.let { "${formatNumber(it, 1)} mAh" } ?: "Indisponível"
                )
                AttributeRow(
                    "Dreno relativo",
                    item.classification.relativeDrainPctPerHour
                        ?.let { "${formatNumber(it, 1)} %/h" } ?: "Indisponível"
                )
                AttributeRow("Tempo de tela", "${d.screenOnMinutes} min")
                AttributeRow("Quadros perdidos", "${formatNumber(d.jankFramesPct, 1)} %")
                AttributeRow("Falhas (7 dias)", d.crashCount7d.toString())
                AttributeRow("Último contato", d.lastSeen)
            }
        }
    }
}

@Composable
private fun AttributeRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}