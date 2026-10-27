package com.example.seldonfleet.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.seldonfleet.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore(name = "favorites")

class DataStoreFavoritesRepository(private val context: Context) : FavoritesRepository {

    private val key = stringSetPreferencesKey("favorite_ids")

    override val favoriteIds: Flow<Set<String>> =
        context.favoritesDataStore.data.map { it[key] ?: emptySet() }

    override suspend fun toggle(deviceId: String) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = if (deviceId in current) current - deviceId else current + deviceId
        }
    }
}