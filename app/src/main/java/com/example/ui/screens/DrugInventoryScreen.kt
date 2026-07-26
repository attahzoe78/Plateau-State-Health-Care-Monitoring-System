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
import com.example.data.entity.DrugInventoryEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugInventoryScreen(
    drugList: List<DrugInventoryEntity>,
    onAddDrugClick: () -> Unit,
    onEditDrug: (DrugInventoryEntity) -> Unit,
    onDeleteDrug: (DrugInventoryEntity) -> Unit,
    onQrScanClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var selectedStatus by remember { mutableStateOf("All Statuses") }

    val categories = listOf("All Categories", "Antimalarial", "Antibiotic", "Analgesic", "Antiviral", "Vaccine", "Antivenom", "IV Fluid")
    val statuses = listOf("All Statuses", "In Stock", "Low Stock", "Out of Stock")

    val filteredList = drugList.filter { drug ->
        (selectedCategory == "All Categories" || drug.category.equals(selectedCategory, ignoreCase = true)) &&
                (selectedStatus == "All Statuses" || drug.status.equals(selectedStatus, ignoreCase = true)) &&
                (searchQuery.isEmpty() || drug.drugName.contains(searchQuery, ignoreCase = true) || drug.facilityName.contains(searchQuery, ignoreCase = true) || drug.batchNumber.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDrugClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.AddBox, contentDescription = "Add Drug Stock")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search drug, batch number, facility...") },
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
                            contentDescription = "Scan Batch QR",
                            tint = Color.White
                        )
                    }
                }
            }

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text(
                text = "Inventory Items (${filteredList.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { drug ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = drug.drugName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = "${drug.category} · ${drug.facilityName} (${drug.lga})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(text = drug.status, type = if (drug.status == "In Stock") "success" else "warning")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Stock Quantity", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("%,d ${drug.unit}".format(drug.stockQuantity), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary))
                                }
                                Column {
                                    Text("Reorder Level", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${drug.reorderLevel} ${drug.unit}", style = MaterialTheme.typography.bodyMedium)
                                }
                                Column {
                                    Text("Expiry Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(drug.expiryDate, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Batch #: ${drug.batchNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { onEditDrug(drug) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Stock", tint = EmeraldPrimary)
                                }
                                IconButton(onClick = { onDeleteDrug(drug) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Drug", tint = Color(0xFFD32F2F))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
