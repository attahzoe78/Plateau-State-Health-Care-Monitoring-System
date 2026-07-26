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
import com.example.data.entity.FumigationLogEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FumigationScreen(
    fumigationLogs: List<FumigationLogEntity>,
    onAddFumigationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("All Types") }

    val types = listOf("All Types", "Organic", "Chemical")

    val organicCount = fumigationLogs.count { it.fumigationType.equals("Organic", ignoreCase = true) }
    val chemicalCount = fumigationLogs.count { it.fumigationType.equals("Chemical", ignoreCase = true) }

    val filteredList = fumigationLogs.filter { log ->
        (selectedType == "All Types" || log.fumigationType.equals(selectedType, ignoreCase = true)) &&
                (searchQuery.isEmpty() || log.facilityName.contains(searchQuery, ignoreCase = true) || log.agentUsed.contains(searchQuery, ignoreCase = true) || log.targetPests.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFumigationClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.CleaningServices, contentDescription = "Schedule Fumigation")
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
            // Header Stats Banner
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
                        text = "Vector Control & Facility Fumigation Tracker",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Monitoring vector suppression schedules across PHCs to prevent Malaria, Lassa Fever, and Blackfly transmission.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Card(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ORGANIC FUMIGATION", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                                Text("$organicCount sessions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32)))
                                Text("Botanical neem & BTI bio-larvicides", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Color(0xFF33691E))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("CHEMICAL FUMIGATION", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                                Text("$chemicalCount sessions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFFF57C00)))
                                Text("Deltamethrin synthetic spray", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Color(0xFFBF360C))
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search facility, agent, or target pest...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(types) { t ->
                    FilterChip(
                        selected = selectedType == t,
                        onClick = { selectedType = t },
                        label = { Text(t) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text(
                text = "Fumigation Logs (${filteredList.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { log ->
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
                                        text = log.facilityName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = "${log.lga} LGA",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(text = log.fumigationType, type = if (log.fumigationType == "Organic") "organic" else "chemical")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("FUMIGATION AGENT USED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Text(log.agentUsed, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldPrimary)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Target Pests", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(log.targetPests, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                                }
                                Column {
                                    Text("Scheduled Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(log.dateScheduled, style = MaterialTheme.typography.bodySmall)
                                }
                                Column {
                                    Text("Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    StatusBadge(text = log.status, type = if (log.status == "Completed") "success" else "warning")
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Supervisor: ${log.supervisorName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
