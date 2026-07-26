package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FacilityDrugRequirementEntity
import com.example.data.entity.InventoryNotificationEntity
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryAlertsScreen(
    notifications: List<InventoryNotificationEntity>,
    drugRequirements: List<FacilityDrugRequirementEntity>,
    thresholdMode: String,
    customThresholdPercentage: Int,
    customThresholdUnits: Int,
    unacknowledgedCount: Int,
    onSetThresholdMode: (String) -> Unit,
    onSetThresholdPercentage: (Int) -> Unit,
    onSetThresholdUnits: (Int) -> Unit,
    onRunAudit: (Context) -> Unit,
    onAcknowledge: (Long, String) -> Unit,
    onDispatchRestock: (InventoryNotificationEntity, Int) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("Unacknowledged") }
    var selectedLgaFilter by remember { mutableStateOf("All LGAs") }
    var showThresholdSettings by remember { mutableStateOf(false) }
    var showSimulateDialog by remember { mutableStateOf(false) }

    val lgas = listOf("All LGAs") + notifications.map { it.lga }.distinct().sorted()

    val filteredNotifications = notifications.filter { item ->
        val matchesFilter = when (selectedFilter) {
            "Unacknowledged" -> !item.isAcknowledged
            "Acknowledged" -> item.isAcknowledged
            "Critical & Out of Stock" -> item.severity == "OUT_OF_STOCK" || item.severity == "CRITICAL_DEFICIT"
            else -> true
        }
        val matchesLga = selectedLgaFilter == "All LGAs" || item.lga == selectedLgaFilter
        matchesFilter && matchesLga
    }

    val outOfStockCount = notifications.count { it.severity == "OUT_OF_STOCK" && !it.isAcknowledged }
    val criticalCount = notifications.count { it.severity == "CRITICAL_DEFICIT" && !it.isAcknowledged }
    val lowStockCount = notifications.count { it.severity == "LOW_STOCK" && !it.isAcknowledged }

    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Critical Drug Alert System",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "Statewide Stockout Prevention & Restock Dispatches",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { showThresholdSettings = !showThresholdSettings }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Threshold Settings",
                                tint = EmeraldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary metric cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBadgeCard(
                            label = "Out of Stock",
                            count = outOfStockCount,
                            containerColor = Color(0xFFFFEBEE),
                            contentColor = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        MetricBadgeCard(
                            label = "Critical Deficit",
                            count = criticalCount,
                            containerColor = Color(0xFFFFF3E0),
                            contentColor = Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                        MetricBadgeCard(
                            label = "Low Stock",
                            count = lowStockCount,
                            containerColor = Color(0xFFFFF8E1),
                            contentColor = Color(0xFFF57F17),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Expandable threshold settings panel
            AnimatedVisibility(visible = showThresholdSettings) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Alert Threshold Configuration",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )

                        Text(
                            text = "Define when the notification system triggers alerts to health officials for critical drugs:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = thresholdMode == "PERCENTAGE",
                                onClick = { onSetThresholdMode("PERCENTAGE") },
                                label = { Text("Quota % Threshold") }
                            )
                            FilterChip(
                                selected = thresholdMode == "BUFFER",
                                onClick = { onSetThresholdMode("BUFFER") },
                                label = { Text("Safety Buffer Level") }
                            )
                            FilterChip(
                                selected = thresholdMode == "FIXED_UNITS",
                                onClick = { onSetThresholdMode("FIXED_UNITS") },
                                label = { Text("Fixed Units") }
                            )
                        }

                        if (thresholdMode == "PERCENTAGE") {
                            Column {
                                Text(
                                    text = "Alert when stock falls below: $customThresholdPercentage% of Monthly Required Quota",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Slider(
                                    value = customThresholdPercentage.toFloat(),
                                    onValueChange = { onSetThresholdPercentage(it.toInt()) },
                                    valueRange = 10f..50f,
                                    steps = 7
                                )
                            }
                        } else if (thresholdMode == "FIXED_UNITS") {
                            Column {
                                Text(
                                    text = "Alert when stock falls below: $customThresholdUnits units",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Slider(
                                    value = customThresholdUnits.toFloat(),
                                    onValueChange = { onSetThresholdUnits(it.toInt()) },
                                    valueRange = 20f..500f,
                                    steps = 23
                                )
                            }
                        }
                    }
                }
            }

            // Primary Audit & Trigger Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onRunAudit(context) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Run Statewide Audit", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onClearAll() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }

            // Filter row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf("Unacknowledged", "All Alerts", "Critical & Out of Stock", "Acknowledged")
                items(filters) { flt ->
                    FilterChip(
                        selected = selectedFilter == flt,
                        onClick = { selectedFilter = flt },
                        label = { Text(flt) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Notification cards list
            if (filteredNotifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "No Active Low Stock Alerts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "All facilities meet minimum drug safety thresholds.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotifications) { item ->
                        InventoryNotificationCard(
                            notification = item,
                            onAcknowledge = { action -> onAcknowledge(item.id, action) },
                            onDispatchRestock = { qty -> onDispatchRestock(item, qty) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBadgeCard(
    label: String,
    count: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = contentColor
            )
        }
    }
}

@Composable
fun InventoryNotificationCard(
    notification: InventoryNotificationEntity,
    onAcknowledge: (String) -> Unit,
    onDispatchRestock: (Int) -> Unit
) {
    val (severityColor, severityBg, severityIcon) = when (notification.severity) {
        "OUT_OF_STOCK" -> Triple(Color(0xFFC62828), Color(0xFFFFEBEE), Icons.Default.ReportProblem)
        "CRITICAL_DEFICIT" -> Triple(Color(0xFFE65100), Color(0xFFFFF3E0), Icons.Default.Warning)
        else -> Triple(Color(0xFFF57F17), Color(0xFFFFF8E1), Icons.Default.ErrorOutline)
    }

    val stockRatio = if (notification.definedThresholdUnits > 0) {
        (notification.currentStockUnits.toFloat() / notification.definedThresholdUnits.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(severityBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = severityIcon,
                            contentDescription = null,
                            tint = severityColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = notification.drugOrSupplyName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${notification.facilityName} · ${notification.lga}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = severityBg
                ) {
                    Text(
                        text = notification.severity.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = severityColor
                    )
                }
            }

            Text(
                text = notification.notificationMessage,
                style = MaterialTheme.typography.bodySmall
            )

            // Stock level visual bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current: ${notification.currentStockUnits} ${notification.unitOfMeasure}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = severityColor
                    )
                    Text(
                        text = "Threshold: ${notification.definedThresholdUnits} ${notification.unitOfMeasure}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LinearProgressIndicator(
                    progress = { stockRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = severityColor,
                    trackColor = severityBg
                )
            }

            Text(
                text = "Alert Timestamp: ${notification.timestamp}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (notification.isAcknowledged) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = notification.actionTaken ?: "Alert Acknowledged",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onDispatchRestock(500) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dispatch Restock (+500)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = { onAcknowledge("Acknowledged by Official") },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Acknowledge", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
