package com.example.seldonfleet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seldonfleet.ui.detail.DeviceDetailScreen
import com.example.seldonfleet.ui.list.FleetListScreen
import com.example.seldonfleet.ui.list.FleetListViewModel

@Composable
fun FleetApp(viewModel: FleetListViewModel) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            FleetListScreen(
                state = state,
                onFilterSelected = viewModel::setFilter,
                onToggleFavorite = viewModel::toggleFavorite,
                onDeviceClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable("detail/{deviceId}") { entry ->
            val id = entry.arguments?.getString("deviceId")
            DeviceDetailScreen(
                item = state.items.firstOrNull { it.device.id == id },
                onBack = { navController.popBackStack() },
                onToggleFavorite = viewModel::toggleFavorite
            )
        }
    }
}