package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.PictureAsPdf
import com.example.util.PdfExporter
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.ForecastResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiForecastScreen(
    lgas: List<String>,
    forecastResult: ForecastResult?,
    onGenerateForecast: (season: String, lga: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSeason by remember { mutableStateOf("Rainy Season") }
    var selectedLga by remember { mutableStateOf(lgas.firstOrNull { it != "All LGAs" } ?: "Shendam") }

    var expandedSeasonDropdown by remember { mutableStateOf(false) }
    var expandedLgaDropdown by remember { mutableStateOf(false) }

    val seasons = listOf("Dry Season (Harmattan)", "Hot Season", "Rainy Season")
    val availableLgas = lgas.filter { it != "All LGAs" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Seasonal Medication Supply Forecaster",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Predictive drug demand modeling based on multi-year surveillance trends, seasonal climate shifts, and population risk factors across Plateau State.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Controls Card
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
                    text = "Configure Predictive Model Parameters",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                // Select Season Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedSeasonDropdown,
                    onExpandedChange = { expandedSeasonDropdown = !expandedSeasonDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedSeason,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Season") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeasonDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSeasonDropdown,
                        onDismissRequest = { expandedSeasonDropdown = false }
                    ) {
                        seasons.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    selectedSeason = s
                                    expandedSeasonDropdown = false
                                }
                            )
                        }
                    }
                }

                // Select LGA Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedLgaDropdown,
                    onExpandedChange = { expandedLgaDropdown = !expandedLgaDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedLga,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Local Government Area (LGA)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLgaDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedLgaDropdown,
                        onDismissRequest = { expandedLgaDropdown = false }
                    ) {
                        availableLgas.forEach { lga ->
                            DropdownMenuItem(
                                text = { Text(lga) },
                                onClick = {
                                    selectedLga = lga
                                    expandedLgaDropdown = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = { onGenerateForecast(selectedSeason, selectedLga) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate AI Supply Forecast", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Result View
        forecastResult?.let { res ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                        Text(
                            text = "AI Forecast Report: ${res.lga}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = res.season,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f))
                    ) {
                        Text(
                            text = res.riskSummary,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Text("PROJECTED HIGH-SURGE DISEASES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    res.predictedHighDemandIllnesses.forEach { disease ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(disease, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("RECOMMENDED PRE-POSITIONED STOCK DISPATCH", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    res.recommendedStockList.forEach { (drugName, rec) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(drugName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(rec, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            PdfExporter.exportAiForecastPdf(context, res)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export Official PDF Report")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Official PDF Report", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
