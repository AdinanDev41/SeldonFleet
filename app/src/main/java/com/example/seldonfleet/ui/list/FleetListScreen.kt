@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.seldonfleet.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seldonfleet.domain.model.DeviceStatus
import com.example.seldonfleet.ui.StatusChip
import com.example.seldonfleet.ui.statusLabel

@Composable
fun FleetListScreen(
    state: FleetUiState,
    onFilterSelected: (DeviceStatus?) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDeviceClick: (String) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Seldon Fleet") }) }) { padding ->
        val error = state.error
        when {
            state.isLoading -> Text(
                "Carregando...",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
            error != null -> Text(
                error,
                modifier = Modifier.padding(padding).padding(16.dp)
            )
            else -> Column(modifier = Modifier.padding(padding)) {
                FilterRow(state, onFilterSelected)
                LazyColumn {
                    items(state.visibleItems, key = { it.device.id }) { item ->
                        DeviceRow(
                            item = item,
                            onClick = { onDeviceClick(item.device.id) },
                            onToggleFavorite = { onToggleFavorite(item.device.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(state: FleetUiState, onFilterSelected: (DeviceStatus?) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = state.filter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("Todos (${state.items.size})") }
        )
        DeviceStatus.entries.forEach { status ->
            FilterChip(
                selected = state.filter == status,
                onClick = { onFilterSelected(status) },
                label = { Text("${statusLabel(status)} (${state.counts[status] ?: 0})") }
            )
        }
    }
}

@Composable
private fun DeviceRow(item: DeviceItem, onClick: () -> Unit, onToggleFavorite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.device.id, style = MaterialTheme.typography.titleMedium)
            Text(item.device.model, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Bateria: ${item.device.batteryLevelPct}%",
                style = MaterialTheme.typography.bodySmall
            )
        }
        StatusChip(item.classification.status)
        TextButton(onClick = onToggleFavorite) {
            Text(
                if (item.isFavorite) "★" else "☆",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}