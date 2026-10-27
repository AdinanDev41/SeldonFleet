package com.example.seldonfleet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.seldonfleet.domain.model.DeviceStatus

fun statusLabel(status: DeviceStatus): String = when (status) {
    DeviceStatus.OK -> "OK"
    DeviceStatus.ATENCAO -> "Atenção"
    DeviceStatus.CRITICO -> "Crítico"
    DeviceStatus.INDETERMINADO -> "Sem dados"
}

private fun statusColor(status: DeviceStatus): Color = when (status) {
    DeviceStatus.OK -> Color(0xFF2E7D32)
    DeviceStatus.ATENCAO -> Color(0xFFB26A00)
    DeviceStatus.CRITICO -> Color(0xFFC62828)
    DeviceStatus.INDETERMINADO -> Color(0xFF616161)
}

@Composable
fun StatusChip(status: DeviceStatus) {
    Surface(color = statusColor(status), shape = RoundedCornerShape(50)) {
        Text(
            text = statusLabel(status),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}