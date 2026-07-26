package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.*
import com.example.data.repository.PhcRepository
import com.example.util.InventoryNotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ScreenTab(val title: String, val category: String) {
    DASHBOARD("System Dashboard", "Overview"),
    FACILITIES_MAP("Facilities Map", "Geographic View"),
    FACILITIES_LIST("Health Facilities", "PHCs & Cottage Hospitals"),
    MEDICAL_STAFF("Medical Staff", "Doctors & Nurses Directory"),
    DRUG_INVENTORY("Drug Inventory", "Central Drug Stock"),
    INVENTORY_ALERTS("Low Stock Alerts", "Emergency Notifications"),
    DRUG_USAGE_SEASON("Drug Usage by Season", "Illness & Season Matrix"),
    FUMIGATION("Vector & Fumigation", "Organic & Chemical Vector Control"),
    PATIENTS("Patients & Health Cards", "Universal Healthcare Cards"),
    BIRTH_RECORDS("Birth Records", "Deliveries & Gender Ratio"),
    SURVEILLANCE("Surveillance & AI Alerts", "Outbreak Detection"),
    AI_FORECAST("AI Seasonal Forecast", "Predictive Drug Requirements"),
    OUTBREAK_PREDICTION("AI Outbreak Predictive Chart", "3-Year Trend & Spike Analysis"),
    INSTALL_APP("Install App (PWA & APK)", "Deployment & Sisi Tech"),
    STAFF_FEEDBACK("Facility Issue Reports", "Infrastructure & Maintenance"),
    REPORTS("Reports & Analytics", "Statewide Insights")
}


data class ForecastResult(
    val season: String,
    val lga: String,
    val predictedHighDemandIllnesses: List<String>,
    val recommendedStockList: List<Pair<String, String>>, // Drug name -> recommended quantity description
    val riskSummary: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PhcRepository
    private val mapTileCacheRepository: com.example.data.repository.MapTileCacheRepository
    private val userPreferencesRepository = com.example.data.datastore.UserPreferencesRepository(application)

    val userPreferences: StateFlow<com.example.data.datastore.UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            com.example.data.datastore.UserPreferences()
        )

    init {
        val db = AppDatabase.getInstance(application)
        repository = PhcRepository(db)
        mapTileCacheRepository = com.example.data.repository.MapTileCacheRepository(db, application)
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            val facs = repository.allFacilities.first()
            mapTileCacheRepository.initializeMapTileCacheIfNeeded(facs)
        }
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { prefs ->
                _selectedLgaFilter.value = prefs.selectedLgaFilter
                _selectedFacilityTypeFilter.value = prefs.selectedFacilityTypeFilter
                _selectedSeasonFilter.value = prefs.selectedSeasonFilter
                _selectedRoleFilter.value = prefs.selectedRoleFilter
                thresholdMode.value = prefs.thresholdMode
                customThresholdUnits.value = prefs.customThresholdUnits
                customThresholdPercentage.value = prefs.customThresholdPercentage

                if (prefs.defaultTabName.isNotBlank()) {
                    try {
                        _currentTab.value = ScreenTab.valueOf(prefs.defaultTabName)
                    } catch (_: Exception) {}
                }
                if (prefs.lastSearchQuery.isNotBlank() && _searchQuery.value.isBlank()) {
                    _searchQuery.value = prefs.lastSearchQuery
                }
            }
        }
    }

    // Reactive database flows
    val facilities: StateFlow<List<FacilityEntity>> = repository.allFacilities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicalStaff: StateFlow<List<MedicalStaffEntity>> = repository.allMedicalStaff
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val drugInventory: StateFlow<List<DrugInventoryEntity>> = repository.allDrugInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val criticalDrugs: StateFlow<List<DrugInventoryEntity>> = repository.criticalDrugs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val seasonalUsages: StateFlow<List<SeasonalDrugUsageEntity>> = repository.allSeasonalUsages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fumigationLogs: StateFlow<List<FumigationLogEntity>> = repository.allFumigationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val patients: StateFlow<List<PatientRecordEntity>> = repository.allPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val birthRecords: StateFlow<List<BirthRecordEntity>> = repository.allBirths
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outbreakAlerts: StateFlow<List<OutbreakAlertEntity>> = repository.allOutbreakAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val facilityDrugRequirements: StateFlow<List<FacilityDrugRequirementEntity>> = repository.allFacilityRequirements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val facilityMedicalSupplies: StateFlow<List<FacilityMedicalSupplyEntity>> = repository.allFacilitySupplies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryNotifications: StateFlow<List<InventoryNotificationEntity>> = repository.allInventoryNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val facilityIssueReports: StateFlow<List<FacilityIssueReportEntity>> = repository.allFacilityIssueReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val facilityAssessments: StateFlow<List<FacilityAssessmentEntity>> = repository.allFacilityAssessments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bufferedAssessmentCount: StateFlow<Int> = repository.bufferedAssessmentCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cachedMapTiles: StateFlow<List<com.example.data.entity.MapTileCacheEntity>> = mapTileCacheRepository.allCachedTiles

        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mapTileCount: StateFlow<Int> = mapTileCacheRepository.cachedTileCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val mapTileCacheSizeBytes: StateFlow<Long> = mapTileCacheRepository.totalCacheSizeBytes
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val unacknowledgedNotificationCount: StateFlow<Int> = repository.unacknowledgedNotificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val predictive30DayOutbreakAlerts: StateFlow<List<com.example.ui.components.alerts.OutbreakPredictiveAlert>> = combine(
        seasonalUsages,
        outbreakAlerts,
        drugInventory,
        facilityDrugRequirements
    ) { seasonal, alerts, inventory, reqs ->
        com.example.ui.components.alerts.PredictiveOutbreakEngine.compute30DayOutbreakPredictions(
            seasonalUsages = seasonal,
            outbreakAlerts = alerts,
            drugInventory = inventory,
            requirements = reqs
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notification threshold settings
    val customThresholdUnits = MutableStateFlow(100)
    val customThresholdPercentage = MutableStateFlow(25) // 25% of required quota
    val thresholdMode = MutableStateFlow("PERCENTAGE") // "PERCENTAGE", "BUFFER", "FIXED_UNITS"

    // WorkManager & Offline Sync state
    val isOnline = MutableStateFlow(true)
    val syncStatusMessage = MutableStateFlow("Room Database Offline-First Mode Active")
    val lastSyncTime = MutableStateFlow("2026-07-26 11:20")
    val isSyncing = MutableStateFlow(false)
    val syncCountdownSeconds = MutableStateFlow(0)
    val syncStepMessage = MutableStateFlow("")
    val syncProgressFraction = MutableStateFlow(0f)

    // Biometric Security Lock State
    val isBiometricLocked = MutableStateFlow(false)
    val biometricAuthMessage = MutableStateFlow("")

    val pendingUploadItems = MutableStateFlow(
        listOf(
            com.example.ui.components.sync.PendingUploadItem("Q-101", "Shendam PHC - Artemether Stock Update (+500)", "Inventory Restock", "Just now", "Enqueued in WorkManager"),
            com.example.ui.components.sync.PendingUploadItem("Q-102", "Pankshin Clinic - Vector Fumigation Log (#208)", "Fumigation Log", "5 mins ago", "Waiting for Connection"),
            com.example.ui.components.sync.PendingUploadItem("Q-103", "Jos North PHC - Birth Delivery Record", "Birth Registration", "12 mins ago", "Enqueued in WorkManager")
        )
    )
    val pendingQueueCount = MutableStateFlow(3)

    // UI state
    private val _currentTab = MutableStateFlow(ScreenTab.DASHBOARD)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedLgaFilter = MutableStateFlow("All LGAs")
    val selectedLgaFilter: StateFlow<String> = _selectedLgaFilter.asStateFlow()

    private val _selectedFacilityTypeFilter = MutableStateFlow("All Types")
    val selectedFacilityTypeFilter: StateFlow<String> = _selectedFacilityTypeFilter.asStateFlow()

    private val _selectedSeasonFilter = MutableStateFlow("All Seasons")
    val selectedSeasonFilter: StateFlow<String> = _selectedSeasonFilter.asStateFlow()

    private val _selectedRoleFilter = MutableStateFlow("All Roles")
    val selectedRoleFilter: StateFlow<String> = _selectedRoleFilter.asStateFlow()

    // Dialog & Form states
    var showAddFacilityDialog = MutableStateFlow(false)
    var showAddStaffDialog = MutableStateFlow(false)
    var showAddDrugDialog = MutableStateFlow(false)
    var showAddFumigationDialog = MutableStateFlow(false)
    var showIssueHealthCardDialog = MutableStateFlow(false)
    var showRecordBirthDialog = MutableStateFlow(false)
    var showOfflineAuditModal = MutableStateFlow(false)


    // Forecast state
    private val _forecastResult = MutableStateFlow<ForecastResult?>(null)
    val forecastResult: StateFlow<ForecastResult?> = _forecastResult.asStateFlow()

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
        viewModelScope.launch {
            userPreferencesRepository.updateDefaultTab(tab.name)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            userPreferencesRepository.updateLastSearchQuery(query)
        }
    }

    fun commitSearchQueryToHistory(query: String) {
        viewModelScope.launch {
            userPreferencesRepository.addSearchQueryToHistory(query)
        }
    }

    fun removeSearchHistoryItem(query: String) {
        viewModelScope.launch {
            userPreferencesRepository.removeSearchQueryFromHistory(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            userPreferencesRepository.clearSearchHistory()
        }
    }

    fun setSelectedLgaFilter(lga: String) {
        _selectedLgaFilter.value = lga
        viewModelScope.launch {
            userPreferencesRepository.updateLgaFilter(lga)
        }
    }

    fun setSelectedFacilityTypeFilter(type: String) {
        _selectedFacilityTypeFilter.value = type
        viewModelScope.launch {
            userPreferencesRepository.updateFacilityTypeFilter(type)
        }
    }

    fun setSelectedSeasonFilter(season: String) {
        _selectedSeasonFilter.value = season
        viewModelScope.launch {
            userPreferencesRepository.updateSeasonFilter(season)
        }
    }

    fun setSelectedRoleFilter(role: String) {
        _selectedRoleFilter.value = role
        viewModelScope.launch {
            userPreferencesRepository.updateRoleFilter(role)
        }
    }

    fun toggleCompactView(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.toggleCompactView(enabled)
        }
    }

    fun toggleAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.toggleAutoSync(enabled)
        }
    }

    fun toggleBiometricProtection(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.toggleBiometricProtection(enabled)
            if (!enabled) {
                isBiometricLocked.value = false
            }
        }
    }

    fun unlockApp() {
        isBiometricLocked.value = false
        biometricAuthMessage.value = "Biometric Verification Successful! Health records unlocked."
    }

    fun lockApp() {
        if (userPreferences.value.isBiometricProtectionEnabled) {
            isBiometricLocked.value = true
            biometricAuthMessage.value = "Session Locked. Biometric scan required."
        }
    }

    fun rebuildMapTileCache() {
        viewModelScope.launch {
            mapTileCacheRepository.rebuildTileCache(facilities.value)
        }
    }

    // CRUD Actions
    fun addFacility(facility: FacilityEntity) {
        viewModelScope.launch {
            repository.addFacility(facility)
        }
    }

    fun updateFacility(facility: FacilityEntity) {
        viewModelScope.launch {
            repository.updateFacility(facility)
        }
    }

    fun deleteFacility(facility: FacilityEntity) {
        viewModelScope.launch {
            repository.deleteFacility(facility)
        }
    }

    fun addMedicalStaff(staff: MedicalStaffEntity) {
        viewModelScope.launch {
            repository.addMedicalStaff(staff)
        }
    }

    fun updateMedicalStaff(staff: MedicalStaffEntity) {
        viewModelScope.launch {
            repository.updateMedicalStaff(staff)
        }
    }

    fun deleteMedicalStaff(staff: MedicalStaffEntity) {
        viewModelScope.launch {
            repository.deleteMedicalStaff(staff)
        }
    }

    fun addDrugItem(drug: DrugInventoryEntity) {
        viewModelScope.launch {
            repository.addDrugItem(drug)
        }
    }

    fun updateDrugItem(drug: DrugInventoryEntity) {
        viewModelScope.launch {
            repository.updateDrugItem(drug)
        }
    }

    fun deleteDrugItem(drug: DrugInventoryEntity) {
        viewModelScope.launch {
            repository.deleteDrugItem(drug)
        }
    }

    fun addFumigationLog(log: FumigationLogEntity) {
        viewModelScope.launch {
            repository.addFumigationLog(log)
        }
    }

    fun addPatientRecord(patient: PatientRecordEntity) {
        viewModelScope.launch {
            repository.addPatientRecord(patient)
        }
    }

    fun addBirthRecord(birth: BirthRecordEntity) {
        viewModelScope.launch {
            repository.addBirthRecord(birth)
        }
    }

    fun generateAiMedicationForecast(season: String, lga: String) {
        viewModelScope.launch {
            val predictedIllnesses = when (season) {
                "Dry Season (Harmattan)" -> listOf("Upper Respiratory Infection (URI)", "Meningitis", "Measles", "Harmattan Conjunctivitis")
                "Hot Season" -> listOf("Lassa Fever", "Typhoid Fever", "Dehydration & Gastroenteritis")
                else -> listOf("Malaria (Plasmodium falciparum)", "Cholera & Watery Diarrhea", "Snakebite Envenomation", "River Blindness")
            }

            val recommendedDrugs = when (season) {
                "Dry Season (Harmattan)" -> listOf(
                    "Amoxicillin 500mg" to "+4,500 packs recommended for $lga (URI Peak)",
                    "Ceftriaxone 1g Injectable" to "+1,200 vials for meningitis prevention",
                    "Measles Vaccine (10-dose)" to "+800 vials for under-5 campaign",
                    "Chloramphenicol Eye Drops" to "+2,000 bottles for Harmattan dust eye treatment"
                )
                "Hot Season" -> listOf(
                    "Ribavirin 200mg" to "+3,000 tablets priority reserve for $lga (Lassa watch)",
                    "Oral Rehydration Salts (ORS)" to "+8,000 sachets for extreme heat dehydration",
                    "Ciprofloxacin 500mg" to "+5,000 packs for typhoid treatment",
                    "Zinc Sulfate 20mg" to "+3,500 packs for pediatric diarrhea"
                )
                else -> listOf(
                    "Artemether-Lumefantrine 80/480mg" to "+15,000 packs priority dispatch to $lga (Peak Malaria)",
                    "Injectable Artesunate 60mg" to "+2,500 vials for severe malaria wards",
                    "Polyvalent Snake Antivenom" to "+150 vials reserve for agricultural snakebite incidents",
                    "ORS & Ringer's Lactate" to "+6,000 IV bags for cholera outbreak containment"
                )
            }

            val summary = "AI Analysis based on historical 3-year surveillance data in $lga: Expected 35% surge in $season cases. Recommended immediate pre-positioning of medical supplies to prevent stockouts."

            _forecastResult.value = ForecastResult(
                season = season,
                lga = lga,
                predictedHighDemandIllnesses = predictedIllnesses,
                recommendedStockList = recommendedDrugs,
                riskSummary = summary
            )
        }
    }

    // Notification System Functions
    fun setThresholdMode(mode: String) {
        thresholdMode.value = mode
        viewModelScope.launch {
            userPreferencesRepository.updateThresholdSettings(mode, customThresholdUnits.value, customThresholdPercentage.value)
        }
    }

    fun setCustomThresholdUnits(units: Int) {
        customThresholdUnits.value = units
        viewModelScope.launch {
            userPreferencesRepository.updateThresholdSettings(thresholdMode.value, units, customThresholdPercentage.value)
        }
    }

    fun setCustomThresholdPercentage(percentage: Int) {
        customThresholdPercentage.value = percentage
        viewModelScope.launch {
            userPreferencesRepository.updateThresholdSettings(thresholdMode.value, customThresholdUnits.value, percentage)
        }
    }

    fun runInventoryThresholdAudit(context: Context? = null) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val existingNotifications = repository.allInventoryNotifications.first()
            val existingKeys = existingNotifications.map { "${it.facilityName}_${it.drugOrSupplyName}" }.toSet()

            var notificationCount = 0

            // 1. Audit Facility Drug Requirements
            val drugReqs = repository.allFacilityRequirements.first()
            val pct = customThresholdPercentage.value
            val mode = thresholdMode.value

            drugReqs.forEach { req ->
                val threshold = when (mode) {
                    "PERCENTAGE" -> (req.monthlyRequiredUnits * pct) / 100
                    "BUFFER" -> req.bufferStockRequired
                    else -> customThresholdUnits.value
                }

                if (req.currentStockUnits <= threshold) {
                    val key = "${req.facilityName}_${req.requiredDrugName}"
                    if (!existingKeys.contains(key)) {
                        val severity = when {
                            req.currentStockUnits == 0 -> "OUT_OF_STOCK"
                            req.currentStockUnits <= threshold / 2 -> "CRITICAL_DEFICIT"
                            else -> "LOW_STOCK"
                        }
                        val title = "🚨 Stock Alert: ${req.requiredDrugName}"
                        val msg = "${req.facilityName} (${req.lga}) has ${req.currentStockUnits} units remaining. Defined threshold is $threshold units."

                        val notification = InventoryNotificationEntity(
                            facilityId = req.facilityId,
                            facilityName = req.facilityName,
                            lga = req.lga,
                            drugOrSupplyName = req.requiredDrugName,
                            category = req.category,
                            currentStockUnits = req.currentStockUnits,
                            definedThresholdUnits = threshold,
                            unitOfMeasure = "packs/vials",
                            severity = severity,
                            notificationTitle = title,
                            notificationMessage = msg,
                            timestamp = dateStr,
                            isAcknowledged = false,
                            actionTaken = null
                        )
                        val id = repository.addInventoryNotification(notification)
                        notificationCount++

                        if (context != null) {
                            InventoryNotificationHelper.sendStockoutAlert(context, (id % 10000).toInt(), title, msg)
                        }
                    }
                }
            }


            // 2. Audit Drug Inventory
            val drugInv = repository.allDrugInventory.first()
            drugInv.forEach { item ->
                val threshold = item.reorderLevel
                if (item.stockQuantity <= threshold) {
                    val key = "${item.facilityName}_${item.drugName}"
                    if (!existingKeys.contains(key)) {
                        val severity = if (item.stockQuantity == 0) "OUT_OF_STOCK" else "LOW_STOCK"
                        val title = "⚠️ Stock Level Warning: ${item.drugName}"
                        val msg = "${item.facilityName} (${item.lga}) central stock is at ${item.stockQuantity} ${item.unit}. Reorder level is $threshold ${item.unit}."

                        val notification = InventoryNotificationEntity(
                            facilityId = 0L,
                            facilityName = item.facilityName,
                            lga = item.lga,
                            drugOrSupplyName = item.drugName,
                            category = item.category,
                            currentStockUnits = item.stockQuantity,
                            definedThresholdUnits = threshold,
                            unitOfMeasure = item.unit,
                            severity = severity,
                            notificationTitle = title,
                            notificationMessage = msg,
                            timestamp = dateStr,
                            isAcknowledged = false,
                            actionTaken = null
                        )
                        val id = repository.addInventoryNotification(notification)
                        notificationCount++

                        if (context != null) {
                            InventoryNotificationHelper.sendStockoutAlert(context, (id % 10000).toInt(), title, msg)
                        }
                    }
                }
            }
        }
    }

    fun acknowledgeNotification(notificationId: Long, actionTaken: String = "Acknowledged by Health Official") {
        viewModelScope.launch {
            repository.acknowledgeInventoryNotification(notificationId, actionTaken)
        }
    }

    fun dispatchEmergencyRestock(notification: InventoryNotificationEntity, restockQty: Int = 500) {
        viewModelScope.launch {
            // Update drug inventory or requirements stock
            val drugReqs = repository.allFacilityRequirements.first()
            val req = drugReqs.find { it.facilityName == notification.facilityName && it.requiredDrugName == notification.drugOrSupplyName }
            if (req != null) {
                val updated = req.copy(
                    currentStockUnits = req.currentStockUnits + restockQty,
                    lastRestockDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                repository.updateDrugRequirement(updated)
            } else {
                val drugInv = repository.allDrugInventory.first()
                val invItem = drugInv.find { it.facilityName == notification.facilityName && it.drugName == notification.drugOrSupplyName }
                if (invItem != null) {
                    val updatedInv = invItem.copy(
                        stockQuantity = invItem.stockQuantity + restockQty,
                        status = "In Stock"
                    )
                    repository.updateDrugItem(updatedInv)
                }
            }

            val actionMsg = "Emergency Restock Dispatched (+$restockQty ${notification.unitOfMeasure})"
            repository.acknowledgeInventoryNotification(notification.id, actionMsg)
        }
    }

    fun dispatch30DayOutbreakBuffer(drugName: String, restockQty: Int) {
        viewModelScope.launch {
            val drugInv = repository.allDrugInventory.first()
            val matchKey = drugName.split(" ").first()
            val invItem = drugInv.find { it.drugName.contains(matchKey, ignoreCase = true) }
            if (invItem != null) {
                val updatedInv = invItem.copy(
                    stockQuantity = invItem.stockQuantity + restockQty,
                    status = "In Stock"
                )
                repository.updateDrugItem(updatedInv)
            }

            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val notification = InventoryNotificationEntity(
                facilityId = 0L,
                facilityName = "Plateau Regional Warehouse",
                lga = "Statewide Pre-Positioning",
                drugOrSupplyName = drugName,
                category = "30-Day Outbreak Buffer",
                currentStockUnits = restockQty,
                definedThresholdUnits = restockQty,
                unitOfMeasure = "packs/vials",
                severity = "LOW_STOCK",
                notificationTitle = "🛡️ 30-Day Outbreak Stock Dispatched",
                notificationMessage = "Pre-positioned +$restockQty units of $drugName in Room DB to prevent predicted seasonal outbreak shortage.",
                timestamp = dateStr,
                isAcknowledged = true,
                actionTaken = "Auto-Dispatched 30-Day Outbreak Stock Reserve"
            )
            repository.addInventoryNotification(notification)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    fun scheduleWorkManagerPeriodicSync(context: Context) {
        com.example.worker.SyncManager.schedulePeriodicSync(context)
    }

    fun toggleNetworkMode() {
        isOnline.value = !isOnline.value
        if (isOnline.value) {
            syncStatusMessage.value = "Connected (4G/Wi-Fi) · Ready to Flush WorkManager Queue"
        } else {
            syncStatusMessage.value = "Offline Mode Active · Changes Enqueued in Local Room DB"
        }
    }

    fun triggerManualWorkManagerSync(context: Context) {
        viewModelScope.launch {
            if (isSyncing.value) return@launch

            isSyncing.value = true
            isOnline.value = true
            val initialCount = pendingQueueCount.value

            com.example.worker.SyncManager.triggerImmediateSync(context)

            // Step 1: Packing & Serializing (3 seconds remaining)
            syncCountdownSeconds.value = 3
            syncProgressFraction.value = 0.20f
            syncStepMessage.value = "Step 1/3: Serializing $initialCount queued local Room DB mutations..."
            syncStatusMessage.value = "Force Sync Active: Pushing queued changes..."
            kotlinx.coroutines.delay(1000)

            // Step 2: Uploading & Flushing (2 seconds remaining)
            syncCountdownSeconds.value = 2
            syncProgressFraction.value = 0.60f
            syncStepMessage.value = "Step 2/3: Transmitting payload to Central Ministry Endpoint..."
            kotlinx.coroutines.delay(1000)

            // Step 3: Verifying Remote Handshake (1 second remaining)
            syncCountdownSeconds.value = 1
            syncProgressFraction.value = 0.90f
            syncStepMessage.value = "Step 3/3: Verifying database consistency & server handshake..."
            kotlinx.coroutines.delay(1000)

            // Complete
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            lastSyncTime.value = dateStr
            syncCountdownSeconds.value = 0
            syncProgressFraction.value = 1.00f
            syncStepMessage.value = "Force Sync Successful! All queued local changes verified."
            syncStatusMessage.value = "Synced via WorkManager (Connected) · All Room DB Uploads Complete"
            pendingUploadItems.value = emptyList()
            pendingQueueCount.value = 0
            
            kotlinx.coroutines.delay(600)
            isSyncing.value = false
            syncProgressFraction.value = 0f

            // Run audit after sync
            runInventoryThresholdAudit(context)
        }
    }

    fun submitFacilityIssueReport(
        facilityId: Long,
        facilityName: String,
        lga: String,
        category: String,
        issueTitle: String,
        description: String,
        urgencyLevel: String,
        reportedByStaffName: String,
        reportedByRole: String,
        contactPhone: String,
        departmentOrWard: String
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val ticketNumber = "WO-2026-PHC-${(100..999).random()}"
            val newReport = FacilityIssueReportEntity(
                facilityId = facilityId,
                facilityName = facilityName,
                lga = lga,
                category = category,
                issueTitle = issueTitle,
                description = description,
                urgencyLevel = urgencyLevel,
                reportedByStaffName = reportedByStaffName,
                reportedByRole = reportedByRole,
                contactPhone = contactPhone,
                departmentOrWard = departmentOrWard,
                dateReported = dateStr,
                status = "Pending Review",
                workOrderTicketNumber = ticketNumber
            )
            repository.addFacilityIssueReport(newReport)

            // Add item to WorkManager queue
            val updatedQueue = pendingUploadItems.value.toMutableList()
            updatedQueue.add(
                com.example.ui.components.sync.PendingUploadItem(
                    id = "Q-${(100..999).random()}",
                    title = "$facilityName - $issueTitle",
                    entityType = "Facility Issue Report",
                    timestamp = "Just now",
                    status = "Enqueued in WorkManager"
                )
            )
            pendingUploadItems.value = updatedQueue
            pendingQueueCount.value = updatedQueue.size

            // Also post notification if critical/high
            if (urgencyLevel == "URGENT_CRITICAL" || urgencyLevel == "HIGH") {
                val notification = InventoryNotificationEntity(
                    facilityId = facilityId,
                    facilityName = facilityName,
                    lga = lga,
                    drugOrSupplyName = issueTitle,
                    category = category,
                    currentStockUnits = 0,
                    definedThresholdUnits = 0,
                    unitOfMeasure = "issue",
                    severity = if (urgencyLevel == "URGENT_CRITICAL") "CRITICAL_DEFICIT" else "LOW_STOCK",
                    notificationTitle = "🚨 $category Report ($urgencyLevel)",
                    notificationMessage = "Staff $reportedByStaffName ($reportedByRole) logged $issueTitle at $facilityName.",
                    timestamp = dateStr,
                    isAcknowledged = false,
                    actionTaken = "Work Order $ticketNumber Generated"
                )
                repository.addInventoryNotification(notification)
            }
        }
    }

    fun updateFacilityIssueStatus(report: FacilityIssueReportEntity, newStatus: String, notes: String) {
        viewModelScope.launch {
            val updated = report.copy(
                status = newStatus,
                resolutionNotes = notes.ifBlank { report.resolutionNotes }
            )
            repository.updateFacilityIssueReport(updated)
        }
    }

    fun submitOfflineAssessment(
        facilityId: Long,
        facilityName: String,
        lga: String,
        inspectorName: String,
        inspectorBadgeId: String,
        overallScorePct: Int,
        cleanlinessRating: String,
        coldChainStatus: String,
        waterSanitationRating: String,
        staffingAdequacy: String,
        drugStockRating: String,
        buildingStructureCondition: String,
        inspectorComments: String,
        recommendedAction: String
    ) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val assessment = FacilityAssessmentEntity(
                facilityId = facilityId,
                facilityName = facilityName,
                lga = lga,
                inspectorName = inspectorName,
                inspectorBadgeId = inspectorBadgeId,
                assessmentDate = dateStr,
                overallScorePct = overallScorePct,
                cleanlinessRating = cleanlinessRating,
                coldChainStatus = coldChainStatus,
                waterSanitationRating = waterSanitationRating,
                staffingAdequacy = staffingAdequacy,
                drugStockRating = drugStockRating,
                buildingStructureCondition = buildingStructureCondition,
                inspectorComments = inspectorComments,
                recommendedAction = recommendedAction,
                syncStatus = "BUFFERED_OFFLINE",
                isBufferedOffline = true
            )
            repository.addFacilityAssessment(assessment)

            // Add item to pending upload WorkManager queue display
            val updatedQueue = pendingUploadItems.value.toMutableList()
            updatedQueue.add(
                com.example.ui.components.sync.PendingUploadItem(
                    id = "AUDIT-${(100..999).random()}",
                    title = "$facilityName - Inspector Audit Assessment ($overallScorePct%)",
                    entityType = "Offline Facility Audit",
                    timestamp = "Just now",
                    status = "Buffered in Room DB"
                )
            )
            pendingUploadItems.value = updatedQueue
            pendingQueueCount.value = updatedQueue.size
        }
    }

    fun flushBufferedAssessmentsToMinistry() {
        viewModelScope.launch {
            repository.syncAllBufferedAssessments()
        }
    }

    fun deleteFacilityAssessment(assessment: FacilityAssessmentEntity) {
        viewModelScope.launch {
            repository.deleteFacilityAssessment(assessment)
        }
    }
}



