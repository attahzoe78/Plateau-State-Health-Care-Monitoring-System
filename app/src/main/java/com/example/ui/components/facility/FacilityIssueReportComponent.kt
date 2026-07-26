package com.example.ui.components.facility

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FacilityEntity
import com.example.data.entity.FacilityIssueReportEntity
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityIssueReportComponent(
    facilities: List<FacilityEntity>,
    issueReports: List<FacilityIssueReportEntity>,
    preselectedFacilityId: Long? = null,
    onSubmitReport: (
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
    ) -> Unit,
    onUpdateReportStatus: (report: FacilityIssueReportEntity, newStatus: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFormVisible by remember { mutableStateOf(preselectedFacilityId != null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    // Form states
    var selectedFacility by remember {
        mutableStateOf(facilities.find { it.id == preselectedFacilityId } ?: facilities.firstOrNull())
    }
    var facilityDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Equipment Failure",
        "Infrastructure & Building",
        "Power / Generator Fault",
        "Cold Chain / Solar Refrigerator",
        "Water & Sanitation Outage",
        "Medical Supply Shortage",
        "Security & Facility Safety"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val urgencyLevels = listOf(
        "URGENT_CRITICAL" to "🔴 Urgent Critical",
        "HIGH" to "🟧 High Priority",
        "MEDIUM" to "🟨 Medium",
        "LOW_ROUTINE" to "🟦 Routine Maintenance"
    )
    var selectedUrgency by remember { mutableStateOf("HIGH") }

    val roles = listOf(
        "Officer in Charge (OIC)",
        "Chief Medical Officer",
        "Senior Nurse",
        "Pharmacy Tech",
        "Lab Technologist",
        "Community Health Extension Worker (CHEW)"
    )
    var selectedRole by remember { mutableStateOf(roles[0]) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    var staffName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var departmentWard by remember { mutableStateOf("Maternity & Primary Care Ward") }
    var issueTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hasSimulatedPhotoAttachment by remember { mutableStateOf(false) }
    var formErrorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    // Dialog for updating issue status
    var reportToUpdate by remember { mutableStateOf<FacilityIssueReportEntity?>(null) }
    var updatedStatusValue by remember { mutableStateOf("Work Order Issued") }
    var resolutionNotesValue by remember { mutableStateOf("") }

    val filteredReports = remember(issueReports, searchQuery, selectedStatusFilter) {
        issueReports.filter { report ->
            val matchesSearch = searchQuery.isBlank() ||
                    report.facilityName.contains(searchQuery, ignoreCase = true) ||
                    report.issueTitle.contains(searchQuery, ignoreCase = true) ||
                    report.category.contains(searchQuery, ignoreCase = true) ||
                    report.lga.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (selectedStatusFilter) {
                "PENDING" -> report.status == "Pending Review"
                "IN_PROGRESS" -> report.status == "Work Order Issued" || report.status == "Under Repair"
                "RESOLVED" -> report.status == "Resolved"
                else -> true
            }

            matchesSearch && matchesStatus
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner & Action Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Staff Facility Issue Reporter",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Direct database link to report equipment failure, solar cold chain faults & maintenance requests for PHCs.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Button(
                        onClick = { isFormVisible = !isFormVisible },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("toggle_report_form_button")
                    ) {
                        Icon(
                            imageVector = if (isFormVisible) Icons.Default.Close else Icons.Default.AddComment,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFormVisible) "Close Form" else "Report Issue",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }
                }
            }
        }

        // Animated Form Card
        AnimatedVisibility(
            visible = isFormVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("facility_issue_form_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛠️ Log Maintenance / Equipment Feedback",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "LINKED TO ROOM DB",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E7D32)
                                )
                            )
                        }
                    }

                    // Facility Selector Dropdown
                    Column {
                        Text(
                            text = "Target Health Facility *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = facilityDropdownExpanded,
                            onExpandedChange = { facilityDropdownExpanded = !facilityDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedFacility?.let { "${it.name} (${it.lga})" } ?: "Select Health Facility",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facilityDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("facility_selector_dropdown"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = facilityDropdownExpanded,
                                onDismissRequest = { facilityDropdownExpanded = false }
                            ) {
                                facilities.forEach { fac ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(fac.name, fontWeight = FontWeight.Bold)
                                                Text("${fac.lga} LGA · ${fac.facilityType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        },
                                        onClick = {
                                            selectedFacility = fac
                                            if (contactPhone.isBlank()) contactPhone = fac.contactPhone
                                            facilityDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Category Selection Chips
                    Column {
                        Text(
                            text = "Issue Category *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    // Urgency Level Selector
                    Column {
                        Text(
                            text = "Urgency / Impact Level *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            urgencyLevels.forEach { (levelKey, levelLabel) ->
                                val isSelected = selectedUrgency == levelKey
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedUrgency = levelKey },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, if (isSelected) EmeraldPrimary else Color.Transparent)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = levelLabel.split(" ").first(), // Icon/Dot
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Reporter Details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = staffName,
                            onValueChange = { staffName = it },
                            label = { Text("Reporter Staff Name *") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("staff_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        ExposedDropdownMenuBox(
                            expanded = roleDropdownExpanded,
                            onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedRole,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Role / Cadre *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = roleDropdownExpanded,
                                onDismissRequest = { roleDropdownExpanded = false }
                            ) {
                                roles.forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role) },
                                        onClick = {
                                            selectedRole = role
                                            roleDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("Contact Phone *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = departmentWard,
                            onValueChange = { departmentWard = it },
                            label = { Text("Department / Ward") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Issue Title & Description
                    OutlinedTextField(
                        value = issueTitle,
                        onValueChange = { issueTitle = it },
                        label = { Text("Issue Title / Summary *") },
                        placeholder = { Text("e.g., Solar Vaccine Refrigerator Inverter Fault") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("issue_title_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description of Fault / Request *") },
                        placeholder = { Text("Describe specific symptoms, affected room number, error codes or parts needed...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("issue_description_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Photo Attachment Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { hasSimulatedPhotoAttachment = !hasSimulatedPhotoAttachment },
                        shape = RoundedCornerShape(10.dp),
                        color = if (hasSimulatedPhotoAttachment) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, if (hasSimulatedPhotoAttachment) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (hasSimulatedPhotoAttachment) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = if (hasSimulatedPhotoAttachment) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasSimulatedPhotoAttachment) "Inspection Photo Attached (IMG_2026_INSPECT.jpg)" else "+ Attach Inspection Photo / Damage Proof",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasSimulatedPhotoAttachment) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    formErrorMessage?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            val fac = selectedFacility
                            if (fac == null) {
                                formErrorMessage = "Please select a health facility."
                                return@Button
                            }
                            if (staffName.isBlank()) {
                                formErrorMessage = "Please enter reporter staff name."
                                return@Button
                            }
                            if (issueTitle.isBlank()) {
                                formErrorMessage = "Please enter an issue title."
                                return@Button
                            }
                            if (description.isBlank()) {
                                formErrorMessage = "Please enter detailed issue description."
                                return@Button
                            }

                            formErrorMessage = null
                            onSubmitReport(
                                fac.id,
                                fac.name,
                                fac.lga,
                                selectedCategory,
                                issueTitle,
                                description,
                                selectedUrgency,
                                staffName,
                                selectedRole,
                                contactPhone.ifBlank { fac.contactPhone },
                                departmentWard
                            )

                            // Clear form
                            issueTitle = ""
                            description = ""
                            hasSimulatedPhotoAttachment = false
                            isFormVisible = false
                            showSuccessSnackbar = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_issue_report_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Submit Facility Issue Report to Room DB & Queue",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Success Feedback Snackbar Banner
        if (showSuccessSnackbar) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFF2E7D32))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Facility issue successfully logged in Room DB and queued for WorkManager sync!",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        )
                    }
                    IconButton(onClick = { showSuccessSnackbar = false }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF2E7D32))
                    }
                }
            }
        }

        // Filter and Search Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by facility, issue, or LGA...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_facility_issues_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf(
                "ALL" to "All Reports (${issueReports.size})",
                "PENDING" to "Pending Review",
                "IN_PROGRESS" to "In Progress / Work Order",
                "RESOLVED" to "Resolved"
            )
            items(filters) { (key, label) ->
                FilterChip(
                    selected = selectedStatusFilter == key,
                    onClick = { selectedStatusFilter = key },
                    label = { Text(label, fontSize = 12.sp) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Text(
            text = "Facility Issue & Maintenance Reports (${filteredReports.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        // List of Facility Issue Reports
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredReports, key = { it.id }) { report ->
                FacilityIssueReportCard(
                    report = report,
                    onOpenStatusUpdate = { reportToUpdate = report }
                )
            }
        }
    }

    // Status Update Dialog
    reportToUpdate?.let { report ->
        AlertDialog(
            onDismissRequest = { reportToUpdate = null },
            title = { Text("Update Work Order Status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "${report.facilityName} · ${report.issueTitle}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Text("Current Status: ${report.status}", fontSize = 12.sp, color = EmeraldPrimary)

                    Column {
                        Text("New Work Order Status", style = MaterialTheme.typography.labelSmall)
                        val statusOptions = listOf("Pending Review", "Work Order Issued", "Under Repair", "Resolved")
                        statusOptions.forEach { st ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { updatedStatusValue = st }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(selected = updatedStatusValue == st, onClick = { updatedStatusValue = st })
                                Text(st, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = resolutionNotesValue,
                        onValueChange = { resolutionNotesValue = it },
                        label = { Text("Maintenance / Tech Resolution Notes") },
                        placeholder = { Text("e.g. Technician replaced solar battery and verified +2°C to +8°C cold chain range.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateReportStatus(report, updatedStatusValue, resolutionNotesValue)
                        reportToUpdate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save Status Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToUpdate = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FacilityIssueReportCard(
    report: FacilityIssueReportEntity,
    onOpenStatusUpdate: () -> Unit
) {
    val statusBg = when (report.status) {
        "Resolved" -> Color(0xFFE8F5E9)
        "Under Repair" -> Color(0xFFE3F2FD)
        "Work Order Issued" -> Color(0xFFFFF3E0)
        else -> Color(0xFFFFEBEE)
    }

    val statusFg = when (report.status) {
        "Resolved" -> Color(0xFF2E7D32)
        "Under Repair" -> Color(0xFF1565C0)
        "Work Order Issued" -> Color(0xFFE65100)
        else -> Color(0xFFC62828)
    }

    val urgencyColor = when (report.urgencyLevel) {
        "URGENT_CRITICAL" -> Color(0xFFD32F2F)
        "HIGH" -> Color(0xFFE65100)
        "MEDIUM" -> Color(0xFFF57C00)
        else -> Color(0xFF1976D2)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("facility_issue_card_${report.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Facility Link Badge & Work Order Ticket #
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${report.facilityName} (${report.lga})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }
                }

                if (report.workOrderTicketNumber.isNotBlank()) {
                    Text(
                        text = report.workOrderTicketNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            // Issue Title & Urgency Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.issueTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "${report.category} · ${report.departmentOrWard}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = urgencyColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = report.urgencyLevel.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = urgencyColor,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Description text
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Resolution Notes if available
            report.resolutionNotes?.let { notes ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("TECH MAINTENANCE NOTES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = EmeraldPrimary)
                        Text(notes, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Footer: Staff details & Status update action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reported by ${report.reportedByStaffName} (${report.reportedByRole})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${report.dateReported} · Contact: ${report.contactPhone}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    modifier = Modifier.clickable { onOpenStatusUpdate() },
                    shape = RoundedCornerShape(10.dp),
                    color = statusBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = report.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusFg
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = statusFg,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}
