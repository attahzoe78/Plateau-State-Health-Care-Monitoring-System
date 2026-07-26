package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val selectedLgaFilter: String = "All LGAs",
    val selectedFacilityTypeFilter: String = "All Types",
    val selectedSeasonFilter: String = "All Seasons",
    val selectedRoleFilter: String = "All Roles",
    val defaultTabName: String = "DASHBOARD",
    val thresholdMode: String = "PERCENTAGE",
    val customThresholdUnits: Int = 100,
    val customThresholdPercentage: Int = 25,
    val searchHistory: List<String> = emptyList(),
    val lastSearchQuery: String = "",
    val isCompactView: Boolean = false,
    val isAutoSyncEnabled: Boolean = true,
    val isBiometricProtectionEnabled: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_LGA = stringPreferencesKey("selected_lga_filter")
        val SELECTED_FACILITY_TYPE = stringPreferencesKey("selected_facility_type_filter")
        val SELECTED_SEASON = stringPreferencesKey("selected_season_filter")
        val SELECTED_ROLE = stringPreferencesKey("selected_role_filter")
        val DEFAULT_TAB = stringPreferencesKey("default_tab")
        val THRESHOLD_MODE = stringPreferencesKey("threshold_mode")
        val CUSTOM_THRESHOLD_UNITS = intPreferencesKey("custom_threshold_units")
        val CUSTOM_THRESHOLD_PERCENTAGE = intPreferencesKey("custom_threshold_percentage")
        val SEARCH_HISTORY = stringPreferencesKey("search_history_csv")
        val LAST_SEARCH_QUERY = stringPreferencesKey("last_search_query")
        val IS_COMPACT_VIEW = booleanPreferencesKey("is_compact_view")
        val IS_AUTO_SYNC_ENABLED = booleanPreferencesKey("is_auto_sync_enabled")
        val IS_BIOMETRIC_PROTECTION_ENABLED = booleanPreferencesKey("is_biometric_protection_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.userPreferencesDataStore.data
        .map { preferences ->
            val lga = preferences[PreferencesKeys.SELECTED_LGA] ?: "All LGAs"
            val facType = preferences[PreferencesKeys.SELECTED_FACILITY_TYPE] ?: "All Types"
            val season = preferences[PreferencesKeys.SELECTED_SEASON] ?: "All Seasons"
            val role = preferences[PreferencesKeys.SELECTED_ROLE] ?: "All Roles"
            val tab = preferences[PreferencesKeys.DEFAULT_TAB] ?: "DASHBOARD"
            val mode = preferences[PreferencesKeys.THRESHOLD_MODE] ?: "PERCENTAGE"
            val units = preferences[PreferencesKeys.CUSTOM_THRESHOLD_UNITS] ?: 100
            val pct = preferences[PreferencesKeys.CUSTOM_THRESHOLD_PERCENTAGE] ?: 25
            val historyCsv = preferences[PreferencesKeys.SEARCH_HISTORY] ?: ""
            val lastQuery = preferences[PreferencesKeys.LAST_SEARCH_QUERY] ?: ""
            val compact = preferences[PreferencesKeys.IS_COMPACT_VIEW] ?: false
            val autoSync = preferences[PreferencesKeys.IS_AUTO_SYNC_ENABLED] ?: true
            val biometricEnabled = preferences[PreferencesKeys.IS_BIOMETRIC_PROTECTION_ENABLED] ?: true

            val historyList = if (historyCsv.isBlank()) {
                emptyList()
            } else {
                historyCsv.split("|||").filter { it.isNotBlank() }
            }

            UserPreferences(
                selectedLgaFilter = lga,
                selectedFacilityTypeFilter = facType,
                selectedSeasonFilter = season,
                selectedRoleFilter = role,
                defaultTabName = tab,
                thresholdMode = mode,
                customThresholdUnits = units,
                customThresholdPercentage = pct,
                searchHistory = historyList,
                lastSearchQuery = lastQuery,
                isCompactView = compact,
                isAutoSyncEnabled = autoSync,
                isBiometricProtectionEnabled = biometricEnabled
            )
        }

    suspend fun updateLgaFilter(lga: String) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.SELECTED_LGA] = lga
        }
    }

    suspend fun updateFacilityTypeFilter(type: String) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.SELECTED_FACILITY_TYPE] = type
        }
    }

    suspend fun updateSeasonFilter(season: String) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.SELECTED_SEASON] = season
        }
    }

    suspend fun updateRoleFilter(role: String) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.SELECTED_ROLE] = role
        }
    }

    suspend fun updateDefaultTab(tabName: String) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.DEFAULT_TAB] = tabName
        }
    }

    suspend fun updateThresholdSettings(mode: String, units: Int, percentage: Int) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.THRESHOLD_MODE] = mode
            prefs[PreferencesKeys.CUSTOM_THRESHOLD_UNITS] = units
            prefs[PreferencesKeys.CUSTOM_THRESHOLD_PERCENTAGE] = percentage
        }
    }

    suspend fun addSearchQueryToHistory(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        context.userPreferencesDataStore.edit { prefs ->
            val currentCsv = prefs[PreferencesKeys.SEARCH_HISTORY] ?: ""
            val currentList = if (currentCsv.isBlank()) emptyList() else currentCsv.split("|||").filter { it.isNotBlank() }

            val updatedList = (listOf(trimmed) + currentList.filter { !it.equals(trimmed, ignoreCase = true) }).take(10)
            prefs[PreferencesKeys.SEARCH_HISTORY] = updatedList.joinToString("|||")
            prefs[PreferencesKeys.LAST_SEARCH_QUERY] = trimmed
        }
    }

    suspend fun removeSearchQueryFromHistory(query: String) {
        context.userPreferencesDataStore.edit { prefs ->
            val currentCsv = prefs[PreferencesKeys.SEARCH_HISTORY] ?: ""
            val currentList = if (currentCsv.isBlank()) emptyList() else currentCsv.split("|||").filter { it.isNotBlank() }
            val updatedList = currentList.filter { !it.equals(query.trim(), ignoreCase = true) }
            prefs[PreferencesKeys.SEARCH_HISTORY] = updatedList.joinToString("|||")
        }
    }

    suspend fun clearSearchHistory() {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.SEARCH_HISTORY] = ""
            prefs[PreferencesKeys.LAST_SEARCH_QUERY] = ""
        }
    }

    suspend fun updateLastSearchQuery(query: String) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.LAST_SEARCH_QUERY] = query
        }
    }

    suspend fun toggleCompactView(enabled: Boolean) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_COMPACT_VIEW] = enabled
        }
    }

    suspend fun toggleAutoSync(enabled: Boolean) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_AUTO_SYNC_ENABLED] = enabled
        }
    }

    suspend fun toggleBiometricProtection(enabled: Boolean) {
        context.userPreferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_BIOMETRIC_PROTECTION_ENABLED] = enabled
        }
    }
}
