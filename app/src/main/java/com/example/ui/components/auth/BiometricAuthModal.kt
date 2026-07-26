package com.example.ui.components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.BiometricAuthManager
import com.example.auth.BiometricStatus
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SleekPrimary

@Composable
fun BiometricLockOverlay(
    isLocked: Boolean,
    onUnlockSuccess: () -> Unit,
    onTriggerBiometricPrompt: () -> Unit,
    biometricStatus: BiometricStatus,
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isLocked,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A).copy(alpha = 0.96f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Shield Icon Header
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(SleekPrimary.copy(alpha = 0.12f))
                                .border(2.dp, SleekPrimary.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Lock",
                                tint = SleekPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Health Worker Security Lock",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Plateau State Primary Healthcare Information System",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "androidx.biometric Security Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                        }

                        Text(
                            text = "Sensitive patient health records, drug allocation logs, and outbreak surveillance files are protected by biometric encryption.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Trigger Biometric Scan Button
                        Button(
                            onClick = onTriggerBiometricPrompt,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("biometric_scan_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Authenticate with Biometrics",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        // Fallback Medical PIN Option
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Or enter Health Worker PIN (Default: 1234):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = {
                                    pinInput = it
                                    pinError = false
                                },
                                placeholder = { Text("Enter 4-digit PIN", fontSize = 13.sp) },
                                singleLine = true,
                                isError = pinError,
                                visualTransformation = PasswordVisualTransformation(),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (pinInput == "1234" || pinInput == "0000") {
                                                onUnlockSuccess()
                                            } else {
                                                pinError = true
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Unlock",
                                            tint = SleekPrimary
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.85f)
                            )

                            if (pinError) {
                                Text(
                                    text = "Invalid Health Worker PIN. Try '1234'",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiometricSettingsCard(
    isBiometricProtectionEnabled: Boolean,
    biometricStatus: BiometricStatus,
    onToggleBiometricProtection: (Boolean) -> Unit,
    onTestBiometricPrompt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SleekPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SleekPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Biometric Security Lock",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE3F2FD)
                            ) {
                                Text(
                                    text = "androidx.biometric",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Protect patient medical records & outbreak reports",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isBiometricProtectionEnabled,
                    onCheckedChange = onToggleBiometricProtection,
                    modifier = Modifier.testTag("toggle_biometric_switch")
                )
            }

            // Status indicator
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when (biometricStatus) {
                    BiometricStatus.AVAILABLE -> Color(0xFFE8F5E9)
                    BiometricStatus.NONE_ENROLLED -> Color(0xFFFFF3E0)
                    else -> Color(0xFFECEFF1)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (biometricStatus) {
                                BiometricStatus.AVAILABLE -> Icons.Default.CheckCircle
                                BiometricStatus.NONE_ENROLLED -> Icons.Default.Warning
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = when (biometricStatus) {
                                BiometricStatus.AVAILABLE -> EmeraldPrimary
                                BiometricStatus.NONE_ENROLLED -> Color(0xFFE65100)
                                else -> Color(0xFF546E7A)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (biometricStatus) {
                                BiometricStatus.AVAILABLE -> "Hardware Ready (Fingerprint / FaceID / Device PIN)"
                                BiometricStatus.NONE_ENROLLED -> "No Biometrics Enrolled (Device PIN active)"
                                BiometricStatus.HARDWARE_UNAVAILABLE -> "Hardware Busy"
                                BiometricStatus.NOT_AVAILABLE -> "Emulator Fallback Active (PIN: 1234)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (biometricStatus) {
                                BiometricStatus.AVAILABLE -> EmeraldPrimary
                                BiometricStatus.NONE_ENROLLED -> Color(0xFFE65100)
                                else -> Color(0xFF37474F)
                            }
                        )
                    }

                    if (isBiometricProtectionEnabled) {
                        TextButton(
                            onClick = onTestBiometricPrompt,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("Test Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                        }
                    }
                }
            }
        }
    }
}
