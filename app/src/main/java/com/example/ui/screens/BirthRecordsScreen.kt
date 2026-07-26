package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.entity.BirthRecordEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.charts.BirthRateStatisticsChart
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthRecordsScreen(
    birthRecords: List<BirthRecordEntity>,
    onRecordBirthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val boyCount = birthRecords.count { it.babyGender.equals("Boy", ignoreCase = true) }
    val girlCount = birthRecords.count { it.babyGender.equals("Girl", ignoreCase = true) }
    val totalBirths = birthRecords.size
    val boyPercentage = if (totalBirths > 0) (boyCount * 100) / totalBirths else 50
    val girlPercentage = 100 - boyPercentage
    val avgWeight = if (totalBirths > 0) birthRecords.map { it.birthWeightKg }.average() else 3.2

    val filteredList = birthRecords.filter { birth ->
        searchQuery.isEmpty() ||
                birth.motherName.contains(searchQuery, ignoreCase = true) ||
                birth.facilityName.contains(searchQuery, ignoreCase = true) ||
                birth.lga.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRecordBirthClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.ChildCare, contentDescription = "Record New Birth")
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
            // Demographics & Boy:Girl Ratio Header Card
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
                        text = "Maternal Deliveries & Birth Demographics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tracking newborn deliveries across primary health facilities to monitor infant health and gender distribution.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Card(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("BOY BIRTHS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                                Text("$boyCount ($boyPercentage%)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D47A1)))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("GIRL BIRTHS", style = MaterialTheme.typography.labelSmall, color = Color(0xFFC2185B), fontWeight = FontWeight.Bold)
                                Text("$girlCount ($girlPercentage%)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF880E4F)))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Births Logged: $totalBirths", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Avg Birth Weight: %.1f kg".format(avgWeight), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary))
                    }
                }
            }

            // Birth Rate Statistics Chart
            BirthRateStatisticsChart(birthRecords = birthRecords)

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search mother name, facility, LGA...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Text(
                text = "Recent Delivery Logs (${filteredList.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { birth ->
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
                                        text = "Mother: ${birth.motherName}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${birth.facilityName} (${birth.lga})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(text = "Baby ${birth.babyGender}", type = if (birth.babyGender == "Boy") "info" else "warning")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Delivery Type", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(birth.deliveryType, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Column {
                                    Text("Weight", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${birth.birthWeightKg} kg", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary))
                                }
                                Column {
                                    Text("Delivery Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(birth.deliveryDate, style = MaterialTheme.typography.bodyMedium)
                                }
                                Column {
                                    Text("Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    StatusBadge(text = birth.birthStatus, type = if (birth.birthStatus == "Healthy") "success" else "danger")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
