package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarHeader(
    onMenuClick: () -> Unit,
    onInstallAppClick: () -> Unit,
    onQrScanClick: (() -> Unit)? = null,
    onNotificationBellClick: (() -> Unit)? = null,
    unacknowledgedNotificationCount: Int = 0,
    onBiometricLockClick: (() -> Unit)? = null,
    onOfflineAuditClick: (() -> Unit)? = null,
    bufferedAuditCount: Int = 0,
    onToggleDarkTheme: () -> Unit,
    isDarkTheme: Boolean,
    activeTabTitle: String,
    activeTabCategory: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1E4FF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color(0xFF001D36),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "STATE HEALTH MONITOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                                fontSize = 10.sp
                            ),
                            color = SleekPrimary
                        )
                        Text(
                            text = if (activeTabTitle == "Overview") "Plateau Health Link" else activeTabTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onNotificationBellClick != null) {
                        IconButton(
                            onClick = onNotificationBellClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (unacknowledgedNotificationCount > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unacknowledgedNotificationCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFD32F2F),
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (unacknowledgedNotificationCount > 99) "99+" else unacknowledgedNotificationCount.toString(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (unacknowledgedNotificationCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Inventory Notifications",
                                    tint = if (unacknowledgedNotificationCount > 0) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (onQrScanClick != null) {
                        IconButton(
                            onClick = onQrScanClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SleekPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR Tag",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (onBiometricLockClick != null) {
                        IconButton(
                            onClick = onBiometricLockClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Lock",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (onOfflineAuditClick != null) {
                        IconButton(
                            onClick = onOfflineAuditClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF3E0))
                        ) {
                            BadgedBox(
                                badge = {
                                    if (bufferedAuditCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFE65100),
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = bufferedAuditCount.toString(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AssignmentTurnedIn,
                                    contentDescription = "Offline Audit Mode",
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }


                    // Dark/Light Theme toggle button with Sleek pill background
                    IconButton(
                        onClick = onToggleDarkTheme,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1E4FF))
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Color(0xFF001D36),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Install App CTA Button
                    Button(
                        onClick = onInstallAppClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GetApp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Install",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}


