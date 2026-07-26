package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OutbreakAlertEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.alerts.Outbreak30DayPredictiveAlertCard
import com.example.ui.components.alerts.OutbreakPredictiveAlert
import com.example.ui.theme.EmeraldPrimary

@Composable
fun SurveillanceScreen(
    outbreakAlerts: List<OutbreakAlertEntity>,
    predictive30DayAlerts: List<OutbreakPredictiveAlert> = emptyList(),
    onDispatchBuffer: (String, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Epidemic Surveillance & Disease Watch",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time surveillance & 30-day predictive disease outbreak warning module using Room database historical records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Section: 30-Day Outbreak Early Warning Predictions
        if (predictive30DayAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "🛡️ 30-Day Outbreak Predictive Alerts (${predictive30DayAlerts.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                )
            }

            items(predictive30DayAlerts) { alert ->
                Outbreak30DayPredictiveAlertCard(
                    alert = alert,
                    onAutoDispatchBuffer = { drug, qty -> onDispatchBuffer(drug, qty) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Active Surveillance Outbreak Cases (${outbreakAlerts.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(outbreakAlerts) { alert ->
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
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = alert.diseaseName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                                color = Color(0xFFB71C1C)
                            )
                        }
                        StatusBadge(text = alert.severityLevel, type = "danger")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Local Govt Area", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(alert.lga, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text("Reported Cases", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("%,d cases".format(alert.reportedCases), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFFD32F2F)))
                        }
                        Column {
                            Text("Weekly Surge", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("+${alert.weeklyTrendPercentage}%", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFE65100)))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("RECOMMENDED EMERGENCY RESPONSE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = Color(0xFFB71C1C))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(alert.recommendedResponse, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium), color = Color(0xFF4E342E))
                        }
                    }
                }
            }
        }
    }
}

