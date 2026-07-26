package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.entity.FacilityEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilitiesListScreen(
    facilities: List<FacilityEntity>,
    onAddFacilityClick: () -> Unit,
    onEditFacility: (FacilityEntity) -> Unit,
    onDeleteFacility: (FacilityEntity) -> Unit,
    onQrScanClick: (() -> Unit)? = null,
    onReportIssueClick: ((FacilityEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLga by remember { mutableStateOf("All LGAs") }
    var selectedType by remember { mutableStateOf("All Types") }

    val allLgas = listOf("All LGAs") + facilities.map { it.lga }.distinct().sorted()
    val allTypes = listOf("All Types", "PHC", "Cottage Hospital", "Tertiary Annex")

    val filteredList = facilities.filter { fac ->
        (selectedLga == "All LGAs" || fac.lga.equals(selectedLga, ignoreCase = true)) &&
                (selectedType == "All Types" || fac.facilityType.equals(selectedType, ignoreCase = true)) &&
                (searchQuery.isEmpty() || fac.name.contains(searchQuery, ignoreCase = true) || fac.lga.contains(searchQuery, ignoreCase = true) || fac.address.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFacilityClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Facility")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input & QR Scanner trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name, LGA, address...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (onQrScanClick != null) {
                    IconButton(
                        onClick = onQrScanClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Facility Tag",
                            tint = Color.White
                        )
                    }
                }
            }

            // LGA Filter
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allLgas) { lga ->
                    FilterChip(
                        selected = selectedLga == lga,
                        onClick = { selectedLga = lga },
                        label = { Text(lga) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Facility List
            Text(
                text = "Showing ${filteredList.size} Facilities",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { facility ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = facility.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${facility.lga} LGA · ${facility.ward} Ward",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(
                                    text = facility.facilityType,
                                    type = if (facility.facilityType == "Cottage Hospital") "info" else "organic"
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    StatusBadge(text = facility.operationalStatus, type = if (facility.operationalStatus == "Operational") "success" else "warning")
                                }
                                Column {
                                    Text("Beds", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${facility.availableBeds} / ${facility.totalBeds} avail", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("Staff Assigned", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${facility.activeStaffCount} staff", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Address: ${facility.address} · Tel: ${facility.contactPhone}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (facility.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${facility.notes}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (onReportIssueClick != null) {
                                    OutlinedButton(
                                        onClick = { onReportIssueClick(facility) },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary)
                                    ) {
                                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Report Issue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Row {
                                    IconButton(onClick = { onEditFacility(facility) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EmeraldPrimary)
                                    }
                                    IconButton(onClick = { onDeleteFacility(facility) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
