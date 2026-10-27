package com.example.seldonfleet.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seldonfleet.domain.model.Classification
import com.example.seldonfleet.domain.model.Device
import com.example.seldonfleet.domain.model.DeviceStatus
import com.example.seldonfleet.domain.repository.DeviceRepository
import com.example.seldonfleet.domain.repository.FavoritesRepository
import com.example.seldonfleet.domain.usecase.ClassifyDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DeviceItem(
    val device: Device,
    val classification: Classification,
    val isFavorite: Boolean
)

data class FleetUiState(
    val items: List<DeviceItem> = emptyList(),
    val filter: DeviceStatus? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val counts: Map<DeviceStatus, Int>
        get() = DeviceStatus.entries.associateWith { status ->
            items.count { it.classification.status == status }
        }

    val visibleItems: List<DeviceItem>
        get() = filter?.let { f -> items.filter { it.classification.status == f } } ?: items
}

class FleetListViewModel(
    private val repository: DeviceRepository,
    private val classifyDevice: ClassifyDevice,
    private val favorites: FavoritesRepository
) : ViewModel() {

    private val classified = MutableStateFlow<List<Pair<Device, Classification>>?>(null)
    private val loadError = MutableStateFlow<String?>(null)
    private val filter = MutableStateFlow<DeviceStatus?>(null)

    val uiState: StateFlow<FleetUiState> = combine(
        classified, favorites.favoriteIds, filter, loadError
    ) { list, favoriteIds, currentFilter, error ->
        FleetUiState(
            items = list.orEmpty().map { (device, classification) ->
                DeviceItem(device, classification, device.id in favoriteIds)
            },
            filter = currentFilter,
            isLoading = list == null && error == null,
            error = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FleetUiState())

    init {
        viewModelScope.launch {
            try {
                classified.value = withContext(Dispatchers.IO) {
                    repository.getDevices().map { it to classifyDevice(it) }
                }
            } catch (e: Exception) {
                loadError.value = "Não foi possível carregar a frota."
            }
        }
    }

    fun setFilter(status: DeviceStatus?) {
        filter.value = status
    }

    fun toggleFavorite(deviceId: String) {
        viewModelScope.launch { favorites.toggle(deviceId) }
    }
}