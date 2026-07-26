package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.entity.*
import com.example.ui.components.CompanyBrandingFooter
import com.example.ui.components.GoogleMapViewComponent
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SleekPrimary
import com.example.ui.viewmodel.ScreenTab

import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.example.ui.theme.EmeraldPrimary

import com.example.data.datastore.UserPreferences
import com.example.ui.components.datastore.UserPreferencesComponent
import com.example.ui.components.sync.PendingUploadItem
import com.example.ui.components.sync.SyncStatusIndicatorComponent

@Composable
fun DashboardScreen(
    facilities: List<FacilityEntity>,
    staffList: List<MedicalStaffEntity>,
    drugList: List<DrugInventoryEntity>,
    seasonalUsages: List<SeasonalDrugUsageEntity>,
    fumigations: List<FumigationLogEntity>,
    outbreakAlerts: List<OutbreakAlertEntity>,
    isOnline: Boolean = true,
    pendingQueueCount: Int = 3,
    pendingUploadItems: List<PendingUploadItem> = emptyList(),
    syncStatusMessage: String = "Room DB Offline Cache Active",
    lastSyncTime: String = "2026-07-26 11:20",
    isSyncing: Boolean = false,
    syncCountdownSeconds: Int = 0,
    syncStepMessage: String = "",
    syncProgressFraction: Float = 0f,
    userPreferences: UserPreferences = UserPreferences(),
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSearchSubmitted: (String) -> Unit = {},
    onRemoveSearchHistoryItem: (String) -> Unit = {},
    onClearSearchHistory: () -> Unit = {},
    onLgaFilterSelected: (String) -> Unit = {},
    onFacilityTypeSelected: (String) -> Unit = {},
    onDefaultTabSelected: (String) -> Unit = {},
    onThresholdSettingsChanged: (mode: String, units: Int, pct: Int) -> Unit = { _, _, _ -> },
    onToggleCompactView: (Boolean) -> Unit = {},
    onToggleAutoSync: (Boolean) -> Unit = {},
    onToggleBiometricProtection: (Boolean) -> Unit = {},
    onTestBiometricPrompt: () -> Unit = {},
    mapTileCount: Int = 153,
    mapTileCacheSizeBytes: Long = 9830400L,
    onRecacheTiles: () -> Unit = {},
    onTriggerSync: (Context) -> Unit = {},
    onToggleNetworkMode: () -> Unit = {},
    onNavigateTab: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalBeds = facilities.sumOf { it.totalBeds }
    val availableBeds = facilities.sumOf { it.availableBeds }
    val doctors = staffList.count { it.role.contains("Doctor", ignoreCase = true) }
    val nurses = staffList.count { it.role.contains("Nurse", ignoreCase = true) || it.role.contains("Midwife", ignoreCase = true) }
    val totalStockUnits = drugList.sumOf { it.stockQuantity }
    val organicFumigations = fumigations.count { it.fumigationType.equals("Organic", ignoreCase = true) }
    val chemicalFumigations = fumigations.count { it.fumigationType.equals("Chemical", ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Welcome Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SleekPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Statewide Health Intelligence",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Plateau State Health Care Monitoring System",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Real-time Google Maps GIS oversight of all primary healthcare facilities and cottage hospitals across the 17 Local Government Areas of Plateau State — tracking medical staff, bed capacity, drug inventory, and fumigation logs.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigateTab(ScreenTab.FACILITIES_MAP) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White))
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Full GIS Map", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = { onNavigateTab(ScreenTab.DRUG_USAGE_SEASON) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White))
                    ) {
                        Icon(imageVector = Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Drug Trends", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = { onNavigateTab(ScreenTab.FUMIGATION) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White))
                    ) {
                        Icon(imageVector = Icons.Default.PestControl, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fumigation", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // WorkManager & Connectivity Sync Indicator Component
        SyncStatusIndicatorComponent(
            isOnline = isOnline,
            pendingQueueCount = pendingQueueCount,
            pendingItems = pendingUploadItems,
            syncStatusMessage = syncStatusMessage,
            lastSyncTime = lastSyncTime,
            isSyncing = isSyncing,
            syncCountdownSeconds = syncCountdownSeconds,
            syncStepMessage = syncStepMessage,
            syncProgressFraction = syncProgressFraction,
            onTriggerSync = onTriggerSync,
            onToggleNetworkMode = onToggleNetworkMode
        )

        // DataStore Preferences & Offline Search History Component
        UserPreferencesComponent(
            userPreferences = userPreferences,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onSearchSubmitted = onSearchSubmitted,
            onRemoveSearchHistoryItem = onRemoveSearchHistoryItem,
            onClearSearchHistory = onClearSearchHistory,
            onLgaFilterSelected = onLgaFilterSelected,
            onFacilityTypeSelected = onFacilityTypeSelected,
            onDefaultTabSelected = onDefaultTabSelected,
            onThresholdSettingsChanged = onThresholdSettingsChanged,
            onToggleCompactView = onToggleCompactView,
            onToggleAutoSync = onToggleAutoSync,
            onToggleBiometricProtection = onToggleBiometricProtection,
            onTestBiometricPrompt = onTestBiometricPrompt
        )

        // Central Map View displaying all Primary Healthcare Facilities and Cottage Hospitals
        GoogleMapViewComponent(
            facilities = facilities,
            mapTileCount = mapTileCount,
            mapTileCacheSizeBytes = mapTileCacheSizeBytes,
            onRecacheTiles = onRecacheTiles,
            onFacilitySelected = { fac ->
                // Selection action
            }
        )

        // Metrics Grid (2 columns on mobile, 4 on wider screens)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Health Facilities",
                    value = "${facilities.size}",
                    subtitle = "${facilities.count { it.facilityType == "PHC" }} PHC · ${facilities.count { it.facilityType == "Cottage Hospital" }} Cottage · ${facilities.count { it.facilityType == "Tertiary Annex" }} Tertiary",
                    icon = Icons.Default.Domain,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Medical Staff",
                    value = "${staffList.size}",
                    subtitle = "$doctors doctors · $nurses nurses",
                    icon = Icons.Default.MedicalServices,
                    iconTint = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Drug Stock Items",
                    value = "${drugList.size}",
                    subtitle = "%,d units total".format(totalStockUnits),
                    icon = Icons.Default.Medication,
                    iconTint = Color(0xFF7B1FA2),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Patient Visits (Yr)",
                    value = "311,671",
                    subtitle = "Across 20 tracked illnesses",
                    icon = Icons.Default.ShowChart,
                    iconTint = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Bed Capacity",
                    value = "%,d".format(totalBeds),
                    subtitle = "%,d available beds statewide".format(availableBeds),
                    icon = Icons.Default.Hotel,
                    iconTint = Color(0xFF00796B),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Personnel",
                    value = "${staffList.count { it.dutyStatus == "Active" || it.dutyStatus == "On Duty" }}",
                    subtitle = "${staffList.count { it.specialization.contains("Maternal", true) }} midwives",
                    icon = Icons.Default.Group,
                    iconTint = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Operational Sites",
                    value = "${facilities.count { it.operationalStatus == "Operational" }}",
                    subtitle = "${facilities.count { it.operationalStatus == "Under Repair" }} under repair",
                    icon = Icons.Default.VerifiedUser,
                    iconTint = Color(0xFF00897B),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Fumigation Sessions",
                    value = "${fumigations.size}",
                    subtitle = "$organicFumigations organic · $chemicalFumigations chemical",
                    icon = Icons.Default.CleaningServices,
                    iconTint = Color(0xFFF57C00),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Active Epidemic Surveillance Watch
        if (outbreakAlerts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Epidemic Surveillance & Outbreak Watch",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(onClick = { onNavigateTab(ScreenTab.SURVEILLANCE) }) {
                            Text("Full Report", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    outbreakAlerts.take(3).forEach { alert ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = alert.diseaseName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFFB71C1C)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        StatusBadge(text = alert.lga, type = "info")
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = alert.recommendedResponse,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFF5D4037)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadge(text = alert.severityLevel, type = "danger")
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${alert.reportedCases} cases",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFB71C1C)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Facilities by LGA Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Facilities by Local Government Area",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Distribution of PHC centres & cottage hospitals across Plateau State",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                val lgaCounts = facilities.groupBy { it.lga }.mapValues { it.value.size }
                val maxCount = lgaCounts.values.maxOrNull() ?: 1

                lgaCounts.entries.take(8).forEach { (lgaName, count) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lgaName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "$count facilities",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = SleekPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { count.toFloat() / maxCount.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SleekPrimary,
                            trackColor = SleekPrimary.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }

        CompanyBrandingFooter()
    }
}

