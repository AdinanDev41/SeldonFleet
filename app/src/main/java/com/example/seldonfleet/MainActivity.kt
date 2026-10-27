package com.example.seldonfleet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.seldonfleet.data.repository.DataStoreFavoritesRepository
import com.example.seldonfleet.data.repository.FleetRepositoryImpl
import com.example.seldonfleet.data.source.AssetFleetSource
import com.example.seldonfleet.data.source.FleetJsonParser
import com.example.seldonfleet.domain.usecase.ClassifyDevice
import com.example.seldonfleet.ui.FleetApp
import com.example.seldonfleet.ui.list.FleetListViewModel
import com.example.seldonfleet.ui.theme.SeldonFleetTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FleetListViewModel by viewModels {
        viewModelFactory {
            initializer {
                FleetListViewModel(
                    repository = FleetRepositoryImpl(
                        AssetFleetSource(applicationContext, FleetJsonParser())
                    ),
                    classifyDevice = ClassifyDevice(),
                    favorites = DataStoreFavoritesRepository(applicationContext)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeldonFleetTheme {
                FleetApp(viewModel)
            }
        }
    }
}