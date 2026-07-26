package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.PlateauHealthTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenTab
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            PlateauHealthTheme(darkTheme = isDarkTheme) {
                MainAppContent(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val facilities by viewModel.facilities.collectAsStateWithLifecycle()
    val staffList by viewModel.medicalStaff.collectAsStateWithLifecycle()
    val drugList by viewModel.drugInventory.collectAsStateWithLifecycle()
    val seasonalUsages by viewModel.seasonalUsages.collectAsStateWithLifecycle()
    val fumigations by viewModel.fumigationLogs.collectAsStateWithLifecycle()
    val patients by viewModel.patients.collectAsStateWithLifecycle()
    val birthRecords by viewModel.birthRecords.collectAsStateWithLifecycle()
    val outbreakAlerts by viewModel.outbreakAlerts.collectAsStateWithLifecycle()
    val predictive30DayAlerts by viewModel.predictive30DayOutbreakAlerts.collectAsStateWithLifecycle()
    val facilityIssueReports by viewModel.facilityIssueReports.collectAsStateWithLifecycle()
    val forecastResult by viewModel.forecastResult.collectAsStateWithLifecycle()

    val inventoryNotifications by viewModel.inventoryNotifications.collectAsStateWithLifecycle()
    val unacknowledgedNotificationCount by viewModel.unacknowledgedNotificationCount.collectAsStateWithLifecycle()
    val facilityDrugReqs by viewModel.facilityDrugRequirements.collectAsStateWithLifecycle()
    val thresholdMode by viewModel.thresholdMode.collectAsStateWithLifecycle()
    val customThresholdPercentage by viewModel.customThresholdPercentage.collectAsStateWithLifecycle()
    val customThresholdUnits by viewModel.customThresholdUnits.collectAsStateWithLifecycle()

    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val pendingQueueCount by viewModel.pendingQueueCount.collectAsStateWithLifecycle()
    val pendingUploadItems by viewModel.pendingUploadItems.collectAsStateWithLifecycle()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncCountdownSeconds by viewModel.syncCountdownSeconds.collectAsStateWithLifecycle()
    val syncStepMessage by viewModel.syncStepMessage.collectAsStateWithLifecycle()
    val syncProgressFraction by viewModel.syncProgressFraction.collectAsStateWithLifecycle()

    val mapTileCount by viewModel.mapTileCount.collectAsStateWithLifecycle()
    val mapTileCacheSizeBytes by viewModel.mapTileCacheSizeBytes.collectAsStateWithLifecycle()

    val isBiometricLocked by viewModel.isBiometricLocked.collectAsStateWithLifecycle()

    val facilityAssessments by viewModel.facilityAssessments.collectAsStateWithLifecycle()
    val bufferedAssessmentCount by viewModel.bufferedAssessmentCount.collectAsStateWithLifecycle()
    val showOfflineAuditModal by viewModel.showOfflineAuditModal.collectAsStateWithLifecycle()

    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedLgaFilter by viewModel.selectedLgaFilter.collectAsStateWithLifecycle()

    val showAddFacilityDialog by viewModel.showAddFacilityDialog.collectAsStateWithLifecycle()
    val showAddStaffDialog by viewModel.showAddStaffDialog.collectAsStateWithLifecycle()
    val showAddDrugDialog by viewModel.showAddDrugDialog.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showQrScannerModal by remember { mutableStateOf(false) }
    var showNotificationModal by remember { mutableStateOf(false) }

    val allLgaNames = listOf("All LGAs") + facilities.map { it.lga }.distinct().sorted()

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometricAuthManager = remember(context) { com.example.auth.BiometricAuthManager(context) }
    val biometricStatus = remember(context) { biometricAuthManager.checkBiometricAvailability() }

    val launchBiometricPrompt = remember(activity, biometricAuthManager) {
        {
            if (activity != null) {
                biometricAuthManager.authenticate(
                    activity = activity,
                    title = "Health Worker Authentication",
                    subtitle = "Verify Fingerprint or FaceID",
                    description = "Verify biometrics to unlock sensitive medical records & outbreak surveillance.",
                    onSuccess = { viewModel.unlockApp() },
                    onError = { _ -> }
                )
            }
        }
    }

    // Auto-audit and schedule WorkManager background sync on app launch
    LaunchedEffect(Unit) {
        viewModel.runInventoryThresholdAudit()
        viewModel.scheduleWorkManagerPeriodicSync(context)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerContent(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        viewModel.selectTab(tab)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBarHeader(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onInstallAppClick = { viewModel.selectTab(ScreenTab.INSTALL_APP) },
                    onQrScanClick = { showQrScannerModal = true },
                    onNotificationBellClick = { showNotificationModal = true },
                    unacknowledgedNotificationCount = unacknowledgedNotificationCount,
                    onBiometricLockClick = { viewModel.lockApp() },
                    onOfflineAuditClick = { viewModel.showOfflineAuditModal.value = true },
                    bufferedAuditCount = bufferedAssessmentCount,
                    onToggleDarkTheme = onToggleDarkTheme,
                    isDarkTheme = isDarkTheme,
                    activeTabTitle = currentTab.title,
                    activeTabCategory = currentTab.category
                )
            }

        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ScreenTab.DASHBOARD -> DashboardScreen(
                        facilities = facilities,
                        staffList = staffList,
                        drugList = drugList,
                        seasonalUsages = seasonalUsages,
                        fumigations = fumigations,
                        outbreakAlerts = outbreakAlerts,
                        isOnline = isOnline,
                        pendingQueueCount = pendingQueueCount,
                        pendingUploadItems = pendingUploadItems,
                        syncStatusMessage = syncStatusMessage,
                        lastSyncTime = lastSyncTime,
                        isSyncing = isSyncing,
                        syncCountdownSeconds = syncCountdownSeconds,
                        syncStepMessage = syncStepMessage,
                        syncProgressFraction = syncProgressFraction,
                        userPreferences = userPreferences,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                        onSearchSubmitted = { q -> viewModel.commitSearchQueryToHistory(q) },
                        onRemoveSearchHistoryItem = { q -> viewModel.removeSearchHistoryItem(q) },
                        onClearSearchHistory = { viewModel.clearSearchHistory() },
                        onLgaFilterSelected = { lga -> viewModel.setSelectedLgaFilter(lga) },
                        onFacilityTypeSelected = { type -> viewModel.setSelectedFacilityTypeFilter(type) },
                        onDefaultTabSelected = { tabName ->
                            try {
                                viewModel.selectTab(ScreenTab.valueOf(tabName))
                            } catch (_: Exception) {}
                        },
                        onThresholdSettingsChanged = { mode, units, pct ->
                            viewModel.setThresholdMode(mode)
                            viewModel.setCustomThresholdUnits(units)
                            viewModel.setCustomThresholdPercentage(pct)
                        },
                        onToggleCompactView = { enabled -> viewModel.toggleCompactView(enabled) },
                        onToggleAutoSync = { enabled -> viewModel.toggleAutoSync(enabled) },
                        onToggleBiometricProtection = { enabled -> viewModel.toggleBiometricProtection(enabled) },
                        onTestBiometricPrompt = { launchBiometricPrompt() },
                        mapTileCount = mapTileCount,
                        mapTileCacheSizeBytes = mapTileCacheSizeBytes,
                        onRecacheTiles = { viewModel.rebuildMapTileCache() },
                        onTriggerSync = { ctx -> viewModel.triggerManualWorkManagerSync(ctx) },
                        onToggleNetworkMode = { viewModel.toggleNetworkMode() },
                        onNavigateTab = { tab -> viewModel.selectTab(tab) }
                    )

                    ScreenTab.FACILITIES_MAP -> FacilitiesMapScreen(
                        facilities = facilities,
                        selectedLga = selectedLgaFilter,
                        onSelectLga = { lga -> viewModel.setSelectedLgaFilter(lga) }
                    )

                    ScreenTab.FACILITIES_LIST -> FacilitiesListScreen(
                        facilities = facilities,
                        onAddFacilityClick = { viewModel.showAddFacilityDialog.value = true },
                        onEditFacility = { fac -> viewModel.updateFacility(fac) },
                        onDeleteFacility = { fac -> viewModel.deleteFacility(fac) },
                        onQrScanClick = { showQrScannerModal = true },
                        onReportIssueClick = { fac -> viewModel.selectTab(ScreenTab.STAFF_FEEDBACK) }
                    )

                    ScreenTab.MEDICAL_STAFF -> MedicalStaffScreen(
                        staffList = staffList,
                        onAddStaffClick = { viewModel.showAddStaffDialog.value = true },
                        onEditStaff = { st -> viewModel.updateMedicalStaff(st) },
                        onDeleteStaff = { st -> viewModel.deleteMedicalStaff(st) }
                    )

                    ScreenTab.DRUG_INVENTORY -> DrugInventoryScreen(
                        drugList = drugList,
                        onAddDrugClick = { viewModel.showAddDrugDialog.value = true },
                        onEditDrug = { drug -> viewModel.updateDrugItem(drug) },
                        onDeleteDrug = { drug -> viewModel.deleteDrugItem(drug) },
                        onQrScanClick = { showQrScannerModal = true }
                    )

                    ScreenTab.INVENTORY_ALERTS -> InventoryAlertsScreen(
                        notifications = inventoryNotifications,
                        drugRequirements = facilityDrugReqs,
                        thresholdMode = thresholdMode,
                        customThresholdPercentage = customThresholdPercentage,
                        customThresholdUnits = customThresholdUnits,
                        unacknowledgedCount = unacknowledgedNotificationCount,
                        onSetThresholdMode = { m -> viewModel.setThresholdMode(m) },
                        onSetThresholdPercentage = { p -> viewModel.setCustomThresholdPercentage(p) },
                        onSetThresholdUnits = { u -> viewModel.setCustomThresholdUnits(u) },
                        onRunAudit = { ctx -> viewModel.runInventoryThresholdAudit(ctx) },
                        onAcknowledge = { id, act -> viewModel.acknowledgeNotification(id, act) },
                        onDispatchRestock = { notif, qty -> viewModel.dispatchEmergencyRestock(notif, qty) },
                        onClearAll = { viewModel.clearAllNotifications() }
                    )

                    ScreenTab.DRUG_USAGE_SEASON -> SeasonalDrugUsageScreen(
                        seasonalUsages = seasonalUsages
                    )


                    ScreenTab.FUMIGATION -> FumigationScreen(
                        fumigationLogs = fumigations,
                        onAddFumigationClick = { viewModel.showAddFumigationDialog.value = true }
                    )

                    ScreenTab.PATIENTS -> PatientsScreen(
                        patientRecords = patients,
                        onIssueHealthCardClick = { viewModel.showIssueHealthCardDialog.value = true }
                    )

                    ScreenTab.BIRTH_RECORDS -> BirthRecordsScreen(
                        birthRecords = birthRecords,
                        onRecordBirthClick = { viewModel.showRecordBirthDialog.value = true }
                    )

                    ScreenTab.SURVEILLANCE -> SurveillanceScreen(
                        outbreakAlerts = outbreakAlerts,
                        predictive30DayAlerts = predictive30DayAlerts,
                        onDispatchBuffer = { drug, qty -> viewModel.dispatch30DayOutbreakBuffer(drug, qty) }
                    )

                    ScreenTab.AI_FORECAST -> AiForecastScreen(
                        lgas = allLgaNames,
                        forecastResult = forecastResult,
                        onGenerateForecast = { season, lga -> viewModel.generateAiMedicationForecast(season, lga) }
                    )

                    ScreenTab.OUTBREAK_PREDICTION -> com.example.ui.screens.OutbreakPredictiveChartScreen(
                        lgas = allLgaNames,
                        onDispatchBuffer = { drug, qty -> viewModel.dispatch30DayOutbreakBuffer(drug, qty) }
                    )

                    ScreenTab.INSTALL_APP -> InstallAppScreen()

                    ScreenTab.STAFF_FEEDBACK -> com.example.ui.components.facility.FacilityIssueReportComponent(
                        facilities = facilities,
                        issueReports = facilityIssueReports,
                        onSubmitReport = { facId, facName, lga, cat, title, desc, urgency, staff, role, phone, dept ->
                            viewModel.submitFacilityIssueReport(
                                facilityId = facId,
                                facilityName = facName,
                                lga = lga,
                                category = cat,
                                issueTitle = title,
                                description = desc,
                                urgencyLevel = urgency,
                                reportedByStaffName = staff,
                                reportedByRole = role,
                                contactPhone = phone,
                                departmentOrWard = dept
                            )
                        },
                        onUpdateReportStatus = { rep, st, notes ->
                            viewModel.updateFacilityIssueStatus(rep, st, notes)
                        }
                    )

                    ScreenTab.REPORTS -> ReportsScreen(
                        facilities = facilities,
                        staffList = staffList,
                        drugList = drugList,
                        seasonalUsages = seasonalUsages,
                        fumigationLogs = fumigations,
                        birthRecords = birthRecords,
                        patientRecords = patients
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddFacilityDialog) {
        AddFacilityDialog(
            onDismiss = { viewModel.showAddFacilityDialog.value = false },
            onSave = { fac ->
                viewModel.addFacility(fac)
                viewModel.showAddFacilityDialog.value = false
            }
        )
    }

    if (showAddStaffDialog) {
        AddStaffDialog(
            facilities = facilities,
            onDismiss = { viewModel.showAddStaffDialog.value = false },
            onSave = { staff ->
                viewModel.addMedicalStaff(staff)
                viewModel.showAddStaffDialog.value = false
            }
        )
    }

    if (showAddDrugDialog) {
        AddDrugDialog(
            facilities = facilities,
            onDismiss = { viewModel.showAddDrugDialog.value = false },
            onSave = { drug ->
                viewModel.addDrugItem(drug)
                viewModel.showAddDrugDialog.value = false
            }
        )
    }

    if (showQrScannerModal) {
        QrCodeScannerModal(
            facilities = facilities,
            drugs = drugList,
            onDismissRequest = { showQrScannerModal = false },
            onFacilityInspected = { fac ->
                viewModel.setSelectedLgaFilter(fac.lga)
                viewModel.selectTab(ScreenTab.FACILITIES_MAP)
            },
            onBatchInspected = { drug ->
                viewModel.selectTab(ScreenTab.DRUG_INVENTORY)
            }
        )
    }

    if (showNotificationModal) {
        InventoryNotificationModal(
            notifications = inventoryNotifications,
            unacknowledgedCount = unacknowledgedNotificationCount,
            onDismissRequest = { showNotificationModal = false },
            onRunAudit = { ctx -> viewModel.runInventoryThresholdAudit(ctx) },
            onAcknowledge = { id, act -> viewModel.acknowledgeNotification(id, act) },
            onDispatchRestock = { notif, qty -> viewModel.dispatchEmergencyRestock(notif, qty) },
            onViewAllAlertsClick = { viewModel.selectTab(ScreenTab.INVENTORY_ALERTS) }
        )
    }

    if (showOfflineAuditModal) {
        com.example.ui.components.facility.OfflineAuditModal(
            onDismiss = { viewModel.showOfflineAuditModal.value = false },
            facilities = facilities,
            bufferedAssessments = facilityAssessments,
            onSaveAssessment = { facId, facName, lga, inspName, badgeId, score, clean, cold, water, staff, drug, bldg, comments, action ->
                viewModel.submitOfflineAssessment(
                    facilityId = facId,
                    facilityName = facName,
                    lga = lga,
                    inspectorName = inspName,
                    inspectorBadgeId = badgeId,
                    overallScorePct = score,
                    cleanlinessRating = clean,
                    coldChainStatus = cold,
                    waterSanitationRating = water,
                    staffingAdequacy = staff,
                    drugStockRating = drug,
                    buildingStructureCondition = bldg,
                    inspectorComments = comments,
                    recommendedAction = action
                )
            },
            onSyncAllBuffered = { viewModel.flushBufferedAssessmentsToMinistry() },
            onDeleteAssessment = { assessment -> viewModel.deleteFacilityAssessment(assessment) }
        )
    }


    // Biometric Security Lock Overlay
    com.example.ui.components.auth.BiometricLockOverlay(
        isLocked = isBiometricLocked,
        onUnlockSuccess = { viewModel.unlockApp() },
        onTriggerBiometricPrompt = { launchBiometricPrompt() },
        biometricStatus = biometricStatus
    )
}

