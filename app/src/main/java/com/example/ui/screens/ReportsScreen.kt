package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.components.CompanyBrandingFooter
import com.example.ui.components.charts.BirthRateStatisticsChart
import com.example.ui.components.charts.SeasonalIllnessTrendsChart
import com.example.ui.theme.EmeraldPrimary

import androidx.compose.ui.platform.LocalContext
import com.example.util.PdfExporter

@Composable
fun ReportsScreen(
    facilities: List<FacilityEntity>,
    staffList: List<MedicalStaffEntity>,
    drugList: List<DrugInventoryEntity>,
    seasonalUsages: List<SeasonalDrugUsageEntity>,
    fumigationLogs: List<FumigationLogEntity>,
    birthRecords: List<BirthRecordEntity> = emptyList(),
    patientRecords: List<PatientRecordEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val phcCount = facilities.count { it.facilityType == "PHC" }
    val cottageCount = facilities.count { it.facilityType == "Cottage Hospital" }
    val tertiaryCount = facilities.count { it.facilityType == "Tertiary Annex" }

    val doctors = staffList.count { it.role.contains("Doctor", ignoreCase = true) }
    val nurses = staffList.count { it.role.contains("Nurse", ignoreCase = true) || it.role.contains("Midwife", ignoreCase = true) }
    val others = staffList.size - doctors - nurses

    val organicFum = fumigationLogs.count { it.fumigationType.equals("Organic", ignoreCase = true) }
    val chemFum = fumigationLogs.count { it.fumigationType.equals("Chemical", ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
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
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Statewide Health Analytics & Executive Report",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            PdfExporter.exportExecutiveAnalyticsPdf(
                                context = context,
                                facilities = facilities,
                                staffList = staffList,
                                seasonalUsages = seasonalUsages,
                                birthRecords = birthRecords
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export PDF",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Export PDF",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Consolidated primary healthcare intelligence report for Plateau State Ministry of Health.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Seasonal Illness Trends Chart
        SeasonalIllnessTrendsChart(
            seasonalUsages = seasonalUsages,
            patientRecords = patientRecords
        )

        // Birth Rate Statistics Chart
        BirthRateStatisticsChart(
            birthRecords = birthRecords
        )

        // Facility Type Breakdown
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
                Text("FACILITY DISTRIBUTION BY TYPE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PHC Centres", style = MaterialTheme.typography.bodyMedium)
                        Text("$phcCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary))
                    }
                    Column {
                        Text("Cottage Hospitals", style = MaterialTheme.typography.bodyMedium)
                        Text("$cottageCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF0288D1)))
                    }
                    Column {
                        Text("Tertiary Annexes", style = MaterialTheme.typography.bodyMedium)
                        Text("$tertiaryCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF7B1FA2)))
                    }
                }
            }
        }

        // Staffing Composition
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
                Text("MEDICAL PERSONNEL DISTRIBUTION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Doctors", style = MaterialTheme.typography.bodyMedium)
                        Text("$doctors", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary))
                    }
                    Column {
                        Text("Nurses & Midwives", style = MaterialTheme.typography.bodyMedium)
                        Text("$nurses", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32)))
                    }
                    Column {
                        Text("Health Workers", style = MaterialTheme.typography.bodyMedium)
                        Text("$others", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100)))
                    }
                }
            }
        }

        // Vector Control Ratio
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
                Text("VECTOR FUMIGATION RATIO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Organic (Botanical)", style = MaterialTheme.typography.bodyMedium)
                        Text("$organicFum sessions", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32)))
                    }
                    Column {
                        Text("Chemical (Synthetic)", style = MaterialTheme.typography.bodyMedium)
                        Text("$chemFum sessions", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFFF57C00)))
                    }
                }
            }
        }

        CompanyBrandingFooter()
    }
}
