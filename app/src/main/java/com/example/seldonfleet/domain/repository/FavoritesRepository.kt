package com.example.seldonfleet.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favoriteIds: Flow<Set<String>>
    suspend fun toggle(deviceId: String)
}