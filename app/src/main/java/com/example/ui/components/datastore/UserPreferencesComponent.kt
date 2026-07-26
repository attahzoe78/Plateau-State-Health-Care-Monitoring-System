package com.example.ui.components.datastore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datastore.UserPreferences
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SleekPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserPreferencesComponent(
    userPreferences: UserPreferences,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmitted: (String) -> Unit,
    onRemoveSearchHistoryItem: (String) -> Unit,
    onClearSearchHistory: () -> Unit,
    onLgaFilterSelected: (String) -> Unit,
    onFacilityTypeSelected: (String) -> Unit,
    onDefaultTabSelected: (String) -> Unit,
    onThresholdSettingsChanged: (mode: String, units: Int, pct: Int) -> Unit,
    onToggleCompactView: (Boolean) -> Unit,
    onToggleAutoSync: (Boolean) -> Unit,
    onToggleBiometricProtection: (Boolean) -> Unit = {},
    onTestBiometricPrompt: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expandedPreferences by remember { mutableStateOf(false) }

    val lgas = listOf(
        "All LGAs", "Barkin Ladi", "Bassa", "Bokkos", "Jos East", "Jos North", "Jos South",
        "Kanam", "Kanke", "Langtang North", "Langtang South", "Mangu", "Mikang",
        "Pankshin", "Qua'an Pan", "Riyom", "Shendam", "Wase"
    )

    val facilityTypes = listOf("All Types", "PHC", "Cottage Hospital", "Tertiary Annex")

    val tabs = listOf(
        "DASHBOARD" to "Dashboard Overview",
        "FACILITIES_LIST" to "Facilities Directory",
        "DRUG_INVENTORY" to "Drug Inventory",
        "SURVEILLANCE" to "Outbreak Watch",
        "REPORTS" to "Analytics & Reports"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DataStore Preferences & Offline Search",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "Jetpack DataStore",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Persisted instantly offline before Room DB sync completes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { expandedPreferences = !expandedPreferences }) {
                    Icon(
                        imageVector = if (expandedPreferences) Icons.Default.ExpandLess else Icons.Default.Tune,
                        contentDescription = "Preferences",
                        tint = SleekPrimary
                    )
                }
            }

            // Search History Chips Row
            if (userPreferences.searchHistory.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Recent Offline Search History (${userPreferences.searchHistory.size}):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(
                            onClick = onClearSearchHistory,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Clear", fontSize = 11.sp, color = Color(0xFFD32F2F))
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(userPreferences.searchHistory) { query ->
                            InputChip(
                                selected = searchQuery.equals(query, ignoreCase = true),
                                onClick = {
                                    onSearchQueryChange(query)
                                    onSearchSubmitted(query)
                                },
                                label = {
                                    Text(
                                        text = query,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { onRemoveSearchHistoryItem(query) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = InputChipDefaults.inputChipColors(
                                    containerColor = Color(0xFFF0F4F8),
                                    selectedContainerColor = SleekPrimary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }

            // Collapsible Preferences Panel
            if (expandedPreferences) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = "Dashboard Preference Settings (Stored in DataStore)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = SleekPrimary
                )

                // Default LGA Preference Dropdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Default LGA Focus:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("Default LGA filter on startup", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    var showLgaMenu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showLgaMenu = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(userPreferences.selectedLgaFilter, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = showLgaMenu,
                            onDismissRequest = { showLgaMenu = false }
                        ) {
                            lgas.forEach { lga ->
                                DropdownMenuItem(
                                    text = { Text(lga, fontSize = 13.sp) },
                                    onClick = {
                                        onLgaFilterSelected(lga)
                                        showLgaMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Default Facility Type Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Default Facility Type:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("PHC vs Cottage Hospital preference", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    var showTypeMenu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showTypeMenu = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(userPreferences.selectedFacilityTypeFilter, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false }
                        ) {
                            facilityTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, fontSize = 13.sp) },
                                    onClick = {
                                        onFacilityTypeSelected(type)
                                        showTypeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Default Landing Tab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Default Landing Screen:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("Tab to show when app opens", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    var showTabMenu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showTabMenu = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            val currentTabLabel = tabs.find { it.first == userPreferences.defaultTabName }?.second ?: "Dashboard Overview"
                            Text(currentTabLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = showTabMenu,
                            onDismissRequest = { showTabMenu = false }
                        ) {
                            tabs.forEach { (tabKey, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, fontSize = 13.sp) },
                                    onClick = {
                                        onDefaultTabSelected(tabKey)
                                        showTabMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Threshold Mode Preference
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Stock Threshold Mode Preference:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = userPreferences.thresholdMode == "PERCENTAGE",
                            onClick = {
                                onThresholdSettingsChanged(
                                    "PERCENTAGE",
                                    userPreferences.customThresholdUnits,
                                    userPreferences.customThresholdPercentage
                                )
                            },
                            label = { Text("Percentage (Quota)", fontSize = 11.sp) },
                            leadingIcon = {
                                if (userPreferences.thresholdMode == "PERCENTAGE") {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                }
                            }
                        )
                        FilterChip(
                            selected = userPreferences.thresholdMode == "BUFFER",
                            onClick = {
                                onThresholdSettingsChanged(
                                    "BUFFER",
                                    userPreferences.customThresholdUnits,
                                    userPreferences.customThresholdPercentage
                                )
                            },
                            label = { Text("Safety Buffer", fontSize = 11.sp) },
                            leadingIcon = {
                                if (userPreferences.thresholdMode == "BUFFER") {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                }
                            }
                        )
                        FilterChip(
                            selected = userPreferences.thresholdMode == "FIXED_UNITS",
                            onClick = {
                                onThresholdSettingsChanged(
                                    "FIXED_UNITS",
                                    userPreferences.customThresholdUnits,
                                    userPreferences.customThresholdPercentage
                                )
                            },
                            label = { Text("Fixed Units", fontSize = 11.sp) },
                            leadingIcon = {
                                if (userPreferences.thresholdMode == "FIXED_UNITS") {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                                }
                            }
                        )
                    }
                }

                // Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto Background Sync:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        Text("Periodically sync Room DB with remote server", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = userPreferences.isAutoSyncEnabled,
                        onCheckedChange = { onToggleAutoSync(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Biometric Security Lock:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
                                Text("Fingerprint/FaceID", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text("Protect health records & outbreak surveillance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = userPreferences.isBiometricProtectionEnabled,
                        onCheckedChange = { onToggleBiometricProtection(it) }
                    )
                }
            }
        }
    }
}
