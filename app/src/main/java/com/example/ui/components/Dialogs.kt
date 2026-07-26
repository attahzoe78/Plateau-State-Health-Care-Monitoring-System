package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.*
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFacilityDialog(
    onDismiss: () -> Unit,
    onSave: (FacilityEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lga by remember { mutableStateOf("Jos North") }
    var ward by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var facilityType by remember { mutableStateOf("PHC") }
    var phone by remember { mutableStateOf("+234 ") }
    var totalBeds by remember { mutableStateOf("20") }
    var availableBeds by remember { mutableStateOf("10") }
    var staffCount by remember { mutableStateOf("8") }

    val lgaList = listOf("Jos North", "Jos South", "Jos East", "Pankshin", "Shendam", "Barkin Ladi", "Bokkos", "Mangu", "Wase", "Bassa", "Riyom", "Kanke", "Kanam", "Langtang North", "Langtang South", "Mikang", "Qua'an Pan")
    var lgaExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Health Facility", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Facility Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // LGA Dropdown
                ExposedDropdownMenuBox(
                    expanded = lgaExpanded,
                    onExpandedChange = { lgaExpanded = !lgaExpanded }
                ) {
                    OutlinedTextField(
                        value = lga,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Local Government Area (LGA)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lgaExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = lgaExpanded,
                        onDismissRequest = { lgaExpanded = false }
                    ) {
                        lgaList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    lga = item
                                    lgaExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = ward,
                    onValueChange = { ward = it },
                    label = { Text("Ward Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Street Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalBeds,
                        onValueChange = { totalBeds = it },
                        label = { Text("Total Beds") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = availableBeds,
                        onValueChange = { availableBeds = it },
                        label = { Text("Avail Beds") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            FacilityEntity(
                                name = name,
                                lga = lga,
                                ward = if (ward.isBlank()) "Central Ward" else ward,
                                address = address,
                                facilityType = facilityType,
                                latitude = 9.9,
                                longitude = 8.8,
                                contactPhone = phone,
                                totalBeds = totalBeds.toIntOrNull() ?: 20,
                                availableBeds = availableBeds.toIntOrNull() ?: 10,
                                activeStaffCount = staffCount.toIntOrNull() ?: 8,
                                operationalStatus = "Operational",
                                emergencyAlertLevel = "Normal"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Save Facility")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffDialog(
    facilities: List<FacilityEntity>,
    onDismiss: () -> Unit,
    onSave: (MedicalStaffEntity) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Doctor") }
    var specialization by remember { mutableStateOf("General Practice") }
    var phone by remember { mutableStateOf("+234 ") }
    var email by remember { mutableStateOf("") }
    var selectedFacility by remember { mutableStateOf(facilities.firstOrNull() ?: FacilityEntity(name = "Tudun Wada PHC", lga = "Jos North", ward = "", address = "", facilityType = "PHC", latitude = 0.0, longitude = 0.0, contactPhone = "", totalBeds = 0, availableBeds = 0, activeStaffCount = 0, operationalStatus = "", emergencyAlertLevel = "")) }

    var roleExpanded by remember { mutableStateOf(false) }
    var facExpanded by remember { mutableStateOf(false) }

    val roles = listOf("Doctor", "Nurse", "Midwife", "Community Health Worker", "Pharmacist")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medical Personnel", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name (e.g. Dr. Nyam Gyang)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = !roleExpanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Staff Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    role = r
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }

                // Facility Dropdown
                ExposedDropdownMenuBox(
                    expanded = facExpanded,
                    onExpandedChange = { facExpanded = !facExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFacility.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigned Health Facility") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = facExpanded,
                        onDismissRequest = { facExpanded = false }
                    ) {
                        facilities.forEach { f ->
                            DropdownMenuItem(
                                text = { Text("${f.name} (${f.lga})") },
                                onClick = {
                                    selectedFacility = f
                                    facExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = specialization,
                    onValueChange = { specialization = it },
                    label = { Text("Specialization / Focus Area") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        onSave(
                            MedicalStaffEntity(
                                facilityId = selectedFacility.id,
                                facilityName = selectedFacility.name,
                                lga = selectedFacility.lga,
                                fullName = fullName,
                                role = role,
                                specialization = specialization,
                                phone = phone,
                                email = if (email.isBlank()) "staff@plateauhealth.gov.ng" else email,
                                dutyStatus = "Active",
                                assignedShift = "Morning"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Save Personnel")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDrugDialog(
    facilities: List<FacilityEntity>,
    onDismiss: () -> Unit,
    onSave: (DrugInventoryEntity) -> Unit
) {
    var drugName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Antimalarial") }
    var quantity by remember { mutableStateOf("1000") }
    var unit by remember { mutableStateOf("tablets") }
    var batchNumber by remember { mutableStateOf("BN${(100000..999999).random()}") }
    var selectedFacility by remember { mutableStateOf(facilities.firstOrNull() ?: FacilityEntity(name = "Tudun Wada PHC", lga = "Jos North", ward = "", address = "", facilityType = "PHC", latitude = 0.0, longitude = 0.0, contactPhone = "", totalBeds = 0, availableBeds = 0, activeStaffCount = 0, operationalStatus = "", emergencyAlertLevel = "")) }

    var catExpanded by remember { mutableStateOf(false) }
    var facExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Antimalarial", "Antibiotic", "Analgesic", "Antiviral", "Vaccine", "Antivenom", "IV Fluid")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Drug Inventory Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = drugName,
                    onValueChange = { drugName = it },
                    label = { Text("Drug Name & Dosage") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    category = c
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (vials, tablets)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = facExpanded,
                    onExpandedChange = { facExpanded = !facExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFacility.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Stock Location Facility") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = facExpanded,
                        onDismissRequest = { facExpanded = false }
                    ) {
                        facilities.forEach { f ->
                            DropdownMenuItem(
                                text = { Text("${f.name} (${f.lga})") },
                                onClick = {
                                    selectedFacility = f
                                    facExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (drugName.isNotBlank()) {
                        val q = quantity.toIntOrNull() ?: 500
                        onSave(
                            DrugInventoryEntity(
                                facilityId = selectedFacility.id,
                                facilityName = selectedFacility.name,
                                lga = selectedFacility.lga,
                                drugName = drugName,
                                category = category,
                                stockQuantity = q,
                                unit = unit,
                                reorderLevel = 300,
                                expiryDate = "2027-12-31",
                                batchNumber = batchNumber,
                                status = if (q < 300) "Low Stock" else "In Stock"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Add Stock Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
