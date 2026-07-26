package com.example.ui.components.sync

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary

data class PendingUploadItem(
    val id: String,
    val title: String,
    val entityType: String, // "Inventory Restock", "Fumigation Log", "Birth Registration", "Outbreak Report"
    val timestamp: String,
    val status: String = "Enqueued in WorkManager" // "Enqueued", "Uploading...", "Failed (Retry)"
)

@Composable
fun SyncStatusIndicatorComponent(
    isOnline: Boolean,
    networkTypeMessage: String = if (isOnline) "4G / Wi-Fi Connected" else "Offline Mode (Local Room DB)",
    pendingQueueCount: Int,
    pendingItems: List<PendingUploadItem> = emptyList(),
    syncStatusMessage: String,
    lastSyncTime: String,
    isSyncing: Boolean,
    syncCountdownSeconds: Int = 0,
    syncStepMessage: String = "",
    syncProgressFraction: Float = 0f,
    onTriggerSync: (Context) -> Unit,
    onToggleNetworkMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpandedQueue by remember { mutableStateOf(false) }

    // Pulsing alpha for online dot indicator
    val infiniteTransition = rememberInfiniteTransition(label = "PulseSyncDot")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val statusColor = when {
        isSyncing -> Color(0xFF1E88E5) // Steel Blue Syncing
        !isOnline -> Color(0xFFE65100) // Warm Amber / Orange Offline
        pendingQueueCount > 0 -> Color(0xFFF57C00) // Pending Items in Queue
        else -> Color(0xFF2E7D32) // Emerald Green Synced
    }

    val statusContainerBg = when {
        isSyncing -> Color(0xFFE3F2FD)
        !isOnline -> Color(0xFFFFF3E0)
        pendingQueueCount > 0 -> Color(0xFFFFF8E1)
        else -> Color(0xFFE8F5E9)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sync_status_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Network Connectivity & Pulsing Status Dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing Live Network Indicator Dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = if (isOnline) pulseAlpha else 0.8f))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOnline) "ONLINE CONNECTED" else "OFFLINE CACHE ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            color = statusColor
                        )
                    )
                }

                // Interactive Toggle Network Simulator Pill for Health Admin
                Surface(
                    modifier = Modifier
                        .clickable { onToggleNetworkMode() }
                        .testTag("toggle_network_mode_button"),
                    shape = RoundedCornerShape(12.dp),
                    color = statusContainerBg,
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOnline) "Simulate Offline" else "Go Online",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        )
                    }
                }
            }

            // Sync Main Body
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(statusContainerBg),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = statusColor,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (!isOnline) Icons.Default.CloudOff else if (pendingQueueCount > 0) Icons.Default.CloudQueue else Icons.Default.CloudDone,
                                contentDescription = "WorkManager Status",
                                tint = statusColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "WorkManager Data Queue",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = syncStatusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Last Successful Sync: $lastSyncTime",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onTriggerSync(context) },
                    enabled = !isSyncing,
                    modifier = Modifier.testTag("force_sync_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (pendingQueueCount > 0) Color(0xFFD84315) else EmeraldPrimary
                    )
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (syncCountdownSeconds > 0) "${syncCountdownSeconds}s..." else "Syncing...",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Force Sync",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Visual Force Sync Countdown & Step Progress Panel
            AnimatedVisibility(visible = isSyncing || syncStepMessage.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3F2FD),
                    border = BorderStroke(1.dp, Color(0xFF90CAF9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = Color(0xFF1565C0),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Force Sync Execution in Progress",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }

                            if (syncCountdownSeconds > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1565C0)
                                ) {
                                    Text(
                                        text = "⏳ ${syncCountdownSeconds}s remaining",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (syncProgressFraction >= 1.0f) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF2E7D32)
                                ) {
                                    Text(
                                        text = "✓ Complete",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        if (syncStepMessage.isNotBlank()) {
                            Text(
                                text = syncStepMessage,
                                fontSize = 11.sp,
                                color = Color(0xFF0D47A1),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        LinearProgressIndicator(
                            progress = { syncProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF1976D2),
                            trackColor = Color(0xFFBBDEFB)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // WorkManager Pending Queue Row Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpandedQueue = !isExpandedQueue }
                    .testTag("pending_queue_expand_button"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Outbox,
                        contentDescription = null,
                        tint = if (pendingQueueCount > 0) Color(0xFFE65100) else Color(0xFF2E7D32),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WorkManager Upload Queue:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (pendingQueueCount > 0) Color(0xFFFFECE0) else Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = if (pendingQueueCount > 0) "$pendingQueueCount Pending Uploads" else "Queue Clear (Synced)",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (pendingQueueCount > 0) Color(0xFFE65100) else Color(0xFF2E7D32)
                            )
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpandedQueue) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand Queue",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expandable Pending Items Detail Drawer
            AnimatedVisibility(visible = isExpandedQueue) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pendingItems.isEmpty() && pendingQueueCount == 0) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "All local Room DB records have been successfully pushed to the central ministry server. WorkManager queue is clear.",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        val itemsToShow = if (pendingItems.isNotEmpty()) pendingItems else listOf(
                            PendingUploadItem("Q-101", "Shendam PHC - Artemether Stock Update (+500)", "Inventory Restock", "Just now", "Enqueued in WorkManager"),
                            PendingUploadItem("Q-102", "Pankshin Clinic - Vector Fumigation Log (#208)", "Fumigation Log", "5 mins ago", "Waiting for Connection"),
                            PendingUploadItem("Q-103", "Jos North PHC - Birth Delivery Record", "Birth Registration", "12 mins ago", "Enqueued in WorkManager")
                        )

                        itemsToShow.forEach { item ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFAF9F6),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        )
                                        Text(
                                            text = "${item.entityType} · ${item.timestamp}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFFF3E0)
                                    ) {
                                        Text(
                                            text = item.status,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFE65100)
                                            )
                                        )
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
