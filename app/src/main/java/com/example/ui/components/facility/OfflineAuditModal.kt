package com.example.ui.components.facility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.FacilityAssessmentEntity
import com.example.data.entity.FacilityEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineAuditModal(
    onDismiss: () -> Unit,
    facilities: List<FacilityEntity>,
    bufferedAssessments: List<FacilityAssessmentEntity>,
    onSaveAssessment: (
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
    ) -> Unit,
    onSyncAllBuffered: () -> Unit,
    onDeleteAssessment: (FacilityAssessmentEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = New Audit Form, 1 = Local Buffer List

    // Form fields
    var selectedFacility by remember { mutableStateOf(facilities.firstOrNull()) }
    var facilityDropdownExpanded by remember { mutableStateOf(false) }

    var inspectorName by remember { mutableStateOf("Insp. Solomon Choji") }
    var inspectorBadgeId by remember { mutableStateOf("INS-PL-2026-042") }
    var overallScorePct by remember { mutableStateOf(80f) }

    var cleanlinessRating by remember { mutableStateOf("Good") }
    var coldChainStatus by remember { mutableStateOf("Functional Solar Refrigerator") }
    var waterSanitationRating by remember { mutableStateOf("Borehole Operational") }
    var staffingAdequacy by remember { mutableStateOf("Fully Staffed") }
    var drugStockRating by remember { mutableStateOf("Sufficient Antimalarials & Antivenom") }
    var buildingStructureCondition by remember { mutableStateOf("Intact Roof & Walls") }
    var inspectorComments by remember { mutableStateOf("") }
    var recommendedAction by remember { mutableStateOf("Routine Quarterly Restock") }

    var formSubmittedSuccess by remember { mutableStateOf(false) }

    val cleanlinessOptions = listOf("Excellent", "Good", "Fair", "Poor")
    val coldChainOptions = listOf("Functional Solar Refrigerator", "Battery Backup Low", "Non-Functional", "Grid Power Only")
    val waterOptions = listOf("Borehole Operational", "Water Shortage", "Contaminated Supply", "No Running Water")
    val staffingOptions = listOf("Fully Staffed", "Moderate Deficit", "Severe Doctor Shortage", "Critical Shortage")
    val drugOptions = listOf("Sufficient Antimalarials & Antivenom", "Low Stocks", "Critical Stockout")
    val buildingOptions = listOf("Intact Roof & Walls", "Minor Repairs Needed", "Severe Leaks/Damage")
    val actionOptions = listOf("Compliance Approved", "Routine Quarterly Restock", "Immediate Emergency Support", "Infrastructure Maintenance Required")

    val bufferedCount = bufferedAssessments.count { it.isBufferedOffline }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Offline Audit Mode",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Offline Facility Audit Mode",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32))
                                )
                                Text(
                                    text = "100% Offline Compatible · Saved to Room DB",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: New Audit Form vs Offline Buffer List
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Inspector Assessment Form", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Offline Buffer ($bufferedCount)", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // TAB 0: NEW AUDIT FORM
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Offline assurance banner
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WifiOff,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = "Complete facility inspections deep in rural wards without cellular signal. Submissions buffer locally in Room SQLite database.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                            }
                        }

                        item {
                            // Success message alert
                            AnimatedVisibility(visible = formSubmittedSuccess) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1E7DD)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0F5132))
                                        Text(
                                            text = "Assessment saved to local Room buffer! Inspector record queued for background sync.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF0F5132),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "1. Facility & Inspector Identification",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        item {
                            // Facility Dropdown
                            ExposedDropdownMenuBox(
                                expanded = facilityDropdownExpanded,
                                onExpandedChange = { facilityDropdownExpanded = !facilityDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedFacility?.let { "${it.name} (${it.lga})" } ?: "Select Facility",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Health Facility Under Inspection") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facilityDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                )

                                ExposedDropdownMenu(
                                    expanded = facilityDropdownExpanded,
                                    onDismissRequest = { facilityDropdownExpanded = false }
                                ) {
                                    facilities.forEach { fac ->
                                        DropdownMenuItem(
                                            text = { Text("${fac.name} (${fac.lga})") },
                                            onClick = {
                                                selectedFacility = fac
                                                facilityDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = inspectorName,
                                    onValueChange = { inspectorName = it },
                                    label = { Text("Inspector Name") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = inspectorBadgeId,
                                    onValueChange = { inspectorBadgeId = it },
                                    label = { Text("Badge / License ID") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            Divider()
                        }

                        item {
                            Text(
                                text = "2. Overall Operational & Hygiene Score",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Compliance Score", fontWeight = FontWeight.Medium)
                                    Badge(
                                        containerColor = when {
                                            overallScorePct.toInt() >= 80 -> Color(0xFF2E7D32)
                                            overallScorePct.toInt() >= 60 -> Color(0xFFF57C00)
                                            else -> Color(0xFFC62828)
                                        }
                                    ) {
                                        Text(
                                            text = "${overallScorePct.toInt()}%",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Slider(
                                    value = overallScorePct,
                                    onValueChange = { overallScorePct = it },
                                    valueRange = 0f..100f,
                                    steps = 19
                                )
                            }
                        }

                        item {
                            Divider()
                        }

                        item {
                            Text(
                                text = "3. Facility Assessment Checklist",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Criteria 1: Cleanliness
                        item {
                            AuditCriterionSelector(
                                title = "Cleanliness & Ward Hygiene",
                                options = cleanlinessOptions,
                                selected = cleanlinessRating,
                                onSelected = { cleanlinessRating = it }
                            )
                        }

                        // Criteria 2: Cold Chain
                        item {
                            AuditCriterionSelector(
                                title = "Vaccine Cold Chain Refrigerator",
                                options = coldChainOptions,
                                selected = coldChainStatus,
                                onSelected = { coldChainStatus = it }
                            )
                        }

                        // Criteria 3: Water & Sanitation
                        item {
                            AuditCriterionSelector(
                                title = "Water Supply & WASH Infrastructure",
                                options = waterOptions,
                                selected = waterSanitationRating,
                                onSelected = { waterSanitationRating = it }
                            )
                        }

                        // Criteria 4: Staffing
                        item {
                            AuditCriterionSelector(
                                title = "Medical Staff Availability",
                                options = staffingOptions,
                                selected = staffingAdequacy,
                                onSelected = { staffingAdequacy = it }
                            )
                        }

                        // Criteria 5: Drug Stock
                        item {
                            AuditCriterionSelector(
                                title = "Essential Antimalarials & Antivenom Stocks",
                                options = drugOptions,
                                selected = drugStockRating,
                                onSelected = { drugStockRating = it }
                            )
                        }

                        // Criteria 6: Building Structure
                        item {
                            AuditCriterionSelector(
                                title = "Roofing & Facility Physical Structure",
                                options = buildingOptions,
                                selected = buildingStructureCondition,
                                onSelected = { buildingStructureCondition = it }
                            )
                        }

                        item {
                            Divider()
                        }

                        item {
                            Text(
                                text = "4. Inspector Comments & Recommended Action",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        item {
                            AuditCriterionSelector(
                                title = "Ministry Priority Action",
                                options = actionOptions,
                                selected = recommendedAction,
                                onSelected = { recommendedAction = it }
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = inspectorComments,
                                onValueChange = { inspectorComments = it },
                                label = { Text("Inspector Observation Comments & Detailed Notes") },
                                placeholder = { Text("Record specific findings e.g. solar battery performance, ward cleanliness, expired batch items...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                maxLines = 4
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val fac = selectedFacility
                                    if (fac != null && inspectorName.isNotBlank()) {
                                        onSaveAssessment(
                                            fac.id,
                                            fac.name,
                                            fac.lga,
                                            inspectorName,
                                            inspectorBadgeId,
                                            overallScorePct.toInt(),
                                            cleanlinessRating,
                                            coldChainStatus,
                                            waterSanitationRating,
                                            staffingAdequacy,
                                            drugStockRating,
                                            buildingStructureCondition,
                                            inspectorComments,
                                            recommendedAction
                                        )
                                        formSubmittedSuccess = true
                                        // Reset comments
                                        inspectorComments = ""
                                    }
                                },
                                enabled = selectedFacility != null && inspectorName.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Assessment to Local Offline Buffer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                } else {
                    // TAB 1: LOCAL BUFFERED AUDIT LOG (SEARCHABLE & BULK ACTIONS)
                    var logSearchQuery by remember { mutableStateOf("") }
                    var filterSyncStatus by remember { mutableStateOf("ALL") } // "ALL", "BUFFERED", "SYNCED"
                    var priorityCategoryFilter by remember { mutableStateOf("ALL") } // "ALL", "CRITICAL", "MODERATE", "APPROVED"
                    var selectedAssessmentIds by remember { mutableStateOf(setOf<Long>()) }

                    var deletingAssessment by remember { mutableStateOf<FacilityAssessmentEntity?>(null) }
                    var reviewingAssessment by remember { mutableStateOf<FacilityAssessmentEntity?>(null) }

                    fun getPriorityCategory(assessment: FacilityAssessmentEntity): String {
                        return when {
                            assessment.recommendedAction.contains("Emergency", ignoreCase = true) || assessment.overallScorePct < 65 -> "CRITICAL"
                            assessment.recommendedAction.contains("Restock", ignoreCase = true) || assessment.recommendedAction.contains("Maintenance", ignoreCase = true) || assessment.overallScorePct in 65..79 -> "MODERATE"
                            else -> "APPROVED"
                        }
                    }

                    val criticalCount = remember(bufferedAssessments) {
                        bufferedAssessments.count { it.isBufferedOffline && getPriorityCategory(it) == "CRITICAL" }
                    }
                    val moderateCount = remember(bufferedAssessments) {
                        bufferedAssessments.count { it.isBufferedOffline && getPriorityCategory(it) == "MODERATE" }
                    }
                    val approvedCount = remember(bufferedAssessments) {
                        bufferedAssessments.count { it.isBufferedOffline && getPriorityCategory(it) == "APPROVED" }
                    }

                    val filteredAssessments = remember(bufferedAssessments, logSearchQuery, filterSyncStatus, priorityCategoryFilter) {
                        bufferedAssessments.filter { assessment ->
                            val matchesQuery = logSearchQuery.isBlank() ||
                                    assessment.facilityName.contains(logSearchQuery, ignoreCase = true) ||
                                    assessment.inspectorName.contains(logSearchQuery, ignoreCase = true) ||
                                    assessment.lga.contains(logSearchQuery, ignoreCase = true) ||
                                    assessment.recommendedAction.contains(logSearchQuery, ignoreCase = true) ||
                                    assessment.inspectorComments.contains(logSearchQuery, ignoreCase = true)

                            val matchesSync = when (filterSyncStatus) {
                                "BUFFERED" -> assessment.isBufferedOffline
                                "SYNCED" -> !assessment.isBufferedOffline
                                else -> true
                            }

                            val matchesPriority = when (priorityCategoryFilter) {
                                "CRITICAL" -> getPriorityCategory(assessment) == "CRITICAL"
                                "MODERATE" -> getPriorityCategory(assessment) == "MODERATE"
                                "APPROVED" -> getPriorityCategory(assessment) == "APPROVED"
                                else -> true
                            }

                            matchesQuery && matchesSync && matchesPriority
                        }
                    }

                    val visibleIds = filteredAssessments.map { it.id }.toSet()
                    val allVisibleSelected = visibleIds.isNotEmpty() && selectedAssessmentIds.containsAll(visibleIds)

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Local Buffer Assessment Log ($bufferedCount Pending Sync)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Offline submissions stored securely in local Room SQLite buffer.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    onSyncAllBuffered()
                                    selectedAssessmentIds = emptySet()
                                },
                                enabled = bufferedCount > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync All ($bufferedCount)")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // SUMMARY & PRIORITY BREAKDOWN CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                        Text(
                                            text = "Offline Audit Summary & Priority Categorization",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Surface(
                                        color = Color(0xFFFFF3E0),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "$bufferedCount Total Entries",
                                            color = Color(0xFFE65100),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PriorityMetricBox(
                                        title = "Emergency Support",
                                        count = criticalCount,
                                        badgeColor = Color(0xFFC62828),
                                        bgColor = Color(0xFFFFEBEE),
                                        isSelected = priorityCategoryFilter == "CRITICAL",
                                        onClick = { priorityCategoryFilter = if (priorityCategoryFilter == "CRITICAL") "ALL" else "CRITICAL" },
                                        modifier = Modifier.weight(1f)
                                    )

                                    PriorityMetricBox(
                                        title = "Moderate Restock",
                                        count = moderateCount,
                                        badgeColor = Color(0xFFE65100),
                                        bgColor = Color(0xFFFFF3E0),
                                        isSelected = priorityCategoryFilter == "MODERATE",
                                        onClick = { priorityCategoryFilter = if (priorityCategoryFilter == "MODERATE") "ALL" else "MODERATE" },
                                        modifier = Modifier.weight(1f)
                                    )

                                    PriorityMetricBox(
                                        title = "Compliance Pass",
                                        count = approvedCount,
                                        badgeColor = Color(0xFF2E7D32),
                                        bgColor = Color(0xFFE8F5E9),
                                        isSelected = priorityCategoryFilter == "APPROVED",
                                        onClick = { priorityCategoryFilter = if (priorityCategoryFilter == "APPROVED") "ALL" else "APPROVED" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = logSearchQuery,
                            onValueChange = { logSearchQuery = it },
                            placeholder = { Text("Search by facility, inspector, LGA, or comment...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Log") },
                            trailingIcon = {
                                if (logSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { logSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sync Status Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = filterSyncStatus == "ALL",
                                onClick = { filterSyncStatus = "ALL" },
                                label = { Text("All (${bufferedAssessments.size})", fontSize = 12.sp) }
                            )
                            FilterChip(
                                selected = filterSyncStatus == "BUFFERED",
                                onClick = { filterSyncStatus = "BUFFERED" },
                                label = { Text("Buffered ($bufferedCount)", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFF3E0),
                                    selectedLabelColor = Color(0xFFE65100)
                                )
                            )
                            FilterChip(
                                selected = filterSyncStatus == "SYNCED",
                                onClick = { filterSyncStatus = "SYNCED" },
                                label = { Text("Synced (${bufferedAssessments.size - bufferedCount})", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE8F5E9),
                                    selectedLabelColor = Color(0xFF2E7D32)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // SELECT ALL & BULK SYNCHRONIZATION BAR
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        selectedAssessmentIds = if (allVisibleSelected) {
                                            selectedAssessmentIds - visibleIds
                                        } else {
                                            selectedAssessmentIds + visibleIds
                                        }
                                    }
                                ) {
                                    Checkbox(
                                        checked = allVisibleSelected,
                                        onCheckedChange = { checked ->
                                            selectedAssessmentIds = if (checked) {
                                                selectedAssessmentIds + visibleIds
                                            } else {
                                                selectedAssessmentIds - visibleIds
                                            }
                                        }
                                    )
                                    Text(
                                        text = "Select All (${filteredAssessments.size})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (selectedAssessmentIds.isNotEmpty()) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                onSyncAllBuffered()
                                                selectedAssessmentIds = emptySet()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Bulk Sync (${selectedAssessmentIds.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val toDelete = bufferedAssessments.filter { it.id in selectedAssessmentIds }
                                                toDelete.forEach { onDeleteAssessment(it) }
                                                selectedAssessmentIds = emptySet()
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delete (${selectedAssessmentIds.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (filteredAssessments.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (logSearchQuery.isNotBlank()) "No audit logs match \"$logSearchQuery\"" else "No buffered assessments found.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredAssessments, key = { it.id }) { item ->
                                    val isSelected = item.id in selectedAssessmentIds
                                    BufferedAssessmentCard(
                                        assessment = item,
                                        isSelected = isSelected,
                                        onToggleSelect = { checked ->
                                            selectedAssessmentIds = if (checked) {
                                                selectedAssessmentIds + item.id
                                            } else {
                                                selectedAssessmentIds - item.id
                                            }
                                        },
                                        onReview = { reviewingAssessment = item },
                                        onDelete = { deletingAssessment = item }
                                    )
                                }
                            }
                        }
                    }

                    // Delete Confirmation Dialog
                    deletingAssessment?.let { assessment ->
                        AlertDialog(
                            onDismissRequest = { deletingAssessment = null },
                            title = { Text("Delete Offline Audit Log Entry?") },
                            text = {
                                Text("Are you sure you want to delete the offline audit record for \"${assessment.facilityName}\"? This entry will be removed from local Room storage and will NOT be uploaded during sync.")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteAssessment(assessment)
                                        deletingAssessment = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Delete Entry")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { deletingAssessment = null }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    // Detailed Inspection Review Modal
                    reviewingAssessment?.let { item ->
                        AlertDialog(
                            onDismissRequest = { reviewingAssessment = null },
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(item.facilityName, style = MaterialTheme.typography.titleLarge)
                                }
                            },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Inspector: ${item.inspectorName} (${item.inspectorBadgeId})", fontWeight = FontWeight.Bold)
                                    Text("LGA & Date: ${item.lga} · ${item.assessmentDate}", style = MaterialTheme.typography.bodySmall)
                                    Text("Overall Score: ${item.overallScorePct}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                    Divider()

                                    Text("• Cleanliness: ${item.cleanlinessRating}", style = MaterialTheme.typography.bodyMedium)
                                    Text("• Vaccine Cold Chain: ${item.coldChainStatus}", style = MaterialTheme.typography.bodyMedium)
                                    Text("• WASH / Water Supply: ${item.waterSanitationRating}", style = MaterialTheme.typography.bodyMedium)
                                    Text("• Staff Availability: ${item.staffingAdequacy}", style = MaterialTheme.typography.bodyMedium)
                                    Text("• Essential Drugs: ${item.drugStockRating}", style = MaterialTheme.typography.bodyMedium)
                                    Text("• Building Structure: ${item.buildingStructureCondition}", style = MaterialTheme.typography.bodyMedium)

                                    Divider()

                                    Text("Recommended Action:", fontWeight = FontWeight.Bold)
                                    Text(item.recommendedAction, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)

                                    if (item.inspectorComments.isNotBlank()) {
                                        Text("Comments:", fontWeight = FontWeight.Bold)
                                        Text("\"${item.inspectorComments}\"", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = { reviewingAssessment = null }) {
                                    Text("Close Review")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditCriterionSelector(
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(option) },
                    label = { Text(option, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun PriorityMetricBox(
    title: String,
    count: Int,
    badgeColor: Color,
    bgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (isSelected) badgeColor.copy(alpha = 0.18f) else bgColor,
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, badgeColor) else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = badgeColor
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = badgeColor
            )
        }
    }
}

@Composable
fun BufferedAssessmentCard(
    assessment: FacilityAssessmentEntity,
    isSelected: Boolean = false,
    onToggleSelect: ((Boolean) -> Unit)? = null,
    onReview: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityTag = when {
        assessment.recommendedAction.contains("Emergency", ignoreCase = true) || assessment.overallScorePct < 65 -> "EMERGENCY"
        assessment.recommendedAction.contains("Restock", ignoreCase = true) || assessment.recommendedAction.contains("Maintenance", ignoreCase = true) || assessment.overallScorePct in 65..79 -> "RESTOCK"
        else -> "COMPLIANT"
    }

    val priorityColor = when (priorityTag) {
        "EMERGENCY" -> Color(0xFFC62828)
        "RESTOCK" -> Color(0xFFE65100)
        else -> Color(0xFF2E7D32)
    }

    val priorityBg = when (priorityTag) {
        "EMERGENCY" -> Color(0xFFFFEBEE)
        "RESTOCK" -> Color(0xFFFFF3E0)
        else -> Color(0xFFE8F5E9)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReview() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (onToggleSelect != null) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = onToggleSelect,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column {
                        Text(
                            text = assessment.facilityName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "LGA: ${assessment.lga} · Date: ${assessment.assessmentDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = priorityBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = priorityTag,
                            color = priorityColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Surface(
                        color = if (assessment.isBufferedOffline) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (assessment.isBufferedOffline) "OFFLINE" else "SYNCED",
                            color = if (assessment.isBufferedOffline) Color(0xFFE65100) else Color(0xFF2E7D32),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete entry", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inspector: ${assessment.inspectorName} (${assessment.inspectorBadgeId})",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Score: ${assessment.overallScorePct}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        assessment.overallScorePct >= 80 -> Color(0xFF2E7D32)
                        assessment.overallScorePct >= 60 -> Color(0xFFF57C00)
                        else -> Color(0xFFC62828)
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "• Cold Chain: ${assessment.coldChainStatus}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Action: ${assessment.recommendedAction}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (assessment.inspectorComments.isNotBlank()) {
                    Text(
                        text = "• Comments: \"${assessment.inspectorComments}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onReview,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Review Full Assessment Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
