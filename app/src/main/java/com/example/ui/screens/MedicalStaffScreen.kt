package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.MedicalStaffEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalStaffScreen(
    staffList: List<MedicalStaffEntity>,
    onAddStaffClick: () -> Unit,
    onEditStaff: (MedicalStaffEntity) -> Unit,
    onDeleteStaff: (MedicalStaffEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("All Roles") }
    var selectedLga by remember { mutableStateOf("All LGAs") }

    val roles = listOf("All Roles", "Doctor", "Nurse", "Midwife", "Community Health Worker", "Pharmacist")
    val lgas = listOf("All LGAs") + staffList.map { it.lga }.distinct().sorted()

    val filteredList = staffList.filter { st ->
        (selectedRole == "All Roles" || st.role.contains(selectedRole, ignoreCase = true)) &&
                (selectedLga == "All LGAs" || st.lga.equals(selectedLga, ignoreCase = true)) &&
                (searchQuery.isEmpty() || st.fullName.contains(searchQuery, ignoreCase = true) || st.facilityName.contains(searchQuery, ignoreCase = true) || st.specialization.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStaffClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Medical Personnel")
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search staff by name, facility, specialization...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Role filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(roles) { r ->
                    FilterChip(
                        selected = selectedRole == r,
                        onClick = { selectedRole = r },
                        label = { Text(r) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text(
                text = "Assigned Personnel (${filteredList.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { staff ->
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (staff.role == "Doctor") Icons.Default.Badge else Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = staff.fullName,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        )
                                        Text(
                                            text = staff.specialization,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                StatusBadge(text = staff.role, type = if (staff.role == "Doctor") "info" else "success")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Assigned Facility", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(staff.facilityName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("Shift", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(staff.assignedShift, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    StatusBadge(text = staff.dutyStatus, type = if (staff.dutyStatus == "Active") "success" else "warning")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tel: ${staff.phone} · Email: ${staff.email}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { onEditStaff(staff) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EmeraldPrimary)
                                }
                                IconButton(onClick = { onDeleteStaff(staff) }) {
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
