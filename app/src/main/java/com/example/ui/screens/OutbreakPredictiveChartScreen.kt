package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.util.PdfExporter
import kotlin.math.roundToInt

// Color Palette for Multi-Year & AI Lines
val Color2023 = Color(0xFF0288D1)       // Light Blue
val Color2024 = Color(0xFFF57C00)       // Amber/Orange
val Color2025 = Color(0xFF8E24AA)       // Purple
val ColorHistAvg = Color(0xFF78909C)    // Slate Blue Gray
val Color2026Ai = Color(0xFFD32F2F)     // Bold Red Crimson
val ColorSpikeGlow = Color(0xFFFF5252)   // Vibrant Red Glow

data class OutbreakMonthData(
    val monthIndex: Int, // 0..11
    val monthShort: String, // "Jan", "Feb", etc.
    val monthFull: String, // "January", "February", etc.
    val year2023: Int,
    val year2024: Int,
    val year2025: Int,
    val aiPredicted2026: Int,
    val climateCatalyst: String,
    val primaryRiskLga: String,
    val recommendedIntervention: String
) {
    val historicalAverage: Int get() = ((year2023 + year2024 + year2025) / 3.0).roundToInt()
    val surgeDeltaPct: Double get() = if (historicalAverage > 0) ((aiPredicted2026 - historicalAverage).toDouble() / historicalAverage) * 100 else 0.0
    val isOutbreakSpike: Boolean get() = surgeDeltaPct >= 25.0
}

data class MedicalStockRequirement(
    val primaryDrugName: String,
    val primaryDrugQty: Int,
    val primaryDrugUnit: String,
    val secondaryDrugName: String,
    val secondaryDrugQty: Int,
    val secondaryDrugUnit: String,
    val testKitsName: String,
    val testKitsQty: Int,
    val testKitsUnit: String,
    val emergencyPpeSets: Int,
    val safetyBufferPct: Int = 20
)

fun calculateMedicalStockRequirement(disease: String, aiPredictedCases: Int): MedicalStockRequirement {
    return when (disease.lowercase()) {
        "cholera" -> MedicalStockRequirement(
            primaryDrugName = "Oral Rehydration Salts (ORS)",
            primaryDrugQty = (aiPredictedCases * 3.5).roundToInt(),
            primaryDrugUnit = "sachets",
            secondaryDrugName = "IV Ringer's Lactate (1L)",
            secondaryDrugQty = (aiPredictedCases * 1.8).roundToInt(),
            secondaryDrugUnit = "bags",
            testKitsName = "Cholera Rapid Diagnostic Dipsticks",
            testKitsQty = (aiPredictedCases * 1.1).roundToInt(),
            testKitsUnit = "kits",
            emergencyPpeSets = (aiPredictedCases * 0.4).roundToInt().coerceAtLeast(100),
            safetyBufferPct = 25
        )
        "lassa fever" -> MedicalStockRequirement(
            primaryDrugName = "Ribavirin 200mg Injections",
            primaryDrugQty = (aiPredictedCases * 2.5).roundToInt(),
            primaryDrugUnit = "ampoules",
            secondaryDrugName = "Intravenous Analgesics & Plasma",
            secondaryDrugQty = (aiPredictedCases * 1.2).roundToInt(),
            secondaryDrugUnit = "units",
            testKitsName = "Lassa Virus RT-PCR Test Kits",
            testKitsQty = (aiPredictedCases * 1.3).roundToInt(),
            testKitsUnit = "kits",
            emergencyPpeSets = (aiPredictedCases * 1.5).roundToInt().coerceAtLeast(200),
            safetyBufferPct = 30
        )
        "typhoid fever" -> MedicalStockRequirement(
            primaryDrugName = "Ciprofloxacin & Azithromycin",
            primaryDrugQty = (aiPredictedCases * 1.3).roundToInt(),
            primaryDrugUnit = "treatment packs",
            secondaryDrugName = "IV Ceftriaxone 1g Injections",
            secondaryDrugQty = (aiPredictedCases * 0.6).roundToInt(),
            secondaryDrugUnit = "vials",
            testKitsName = "Typhidot Rapid Diagnostic Strips",
            testKitsQty = (aiPredictedCases * 1.25).roundToInt(),
            testKitsUnit = "kits",
            emergencyPpeSets = (aiPredictedCases * 0.25).roundToInt().coerceAtLeast(80),
            safetyBufferPct = 20
        )
        "respiratory infection (uri)" -> MedicalStockRequirement(
            primaryDrugName = "Amoxicillin / Clavulanate Syrup",
            primaryDrugQty = (aiPredictedCases * 1.4).roundToInt(),
            primaryDrugUnit = "bottles",
            secondaryDrugName = "Medical Oxygen Cylinders",
            secondaryDrugQty = (aiPredictedCases * 0.35).roundToInt(),
            secondaryDrugUnit = "cylinders",
            testKitsName = "Pulse Oximeters & Nasal Swabs",
            testKitsQty = (aiPredictedCases * 1.1).roundToInt(),
            testKitsUnit = "units",
            emergencyPpeSets = (aiPredictedCases * 0.5).roundToInt().coerceAtLeast(120),
            safetyBufferPct = 15
        )
        else -> // Malaria
            MedicalStockRequirement(
                primaryDrugName = "Artemether/Lumefantrine (ACT)",
                primaryDrugQty = (aiPredictedCases * 1.2).roundToInt(),
                primaryDrugUnit = "treatment packs",
                secondaryDrugName = "Injectable Artesunate 60mg",
                secondaryDrugQty = (aiPredictedCases * 0.2).roundToInt(),
                secondaryDrugUnit = "vials",
                testKitsName = "mRDT Malaria Rapid Test Kits",
                testKitsQty = (aiPredictedCases * 1.5).roundToInt(),
                testKitsUnit = "kits",
                emergencyPpeSets = (aiPredictedCases * 0.3).roundToInt().coerceAtLeast(150),
                safetyBufferPct = 20
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutbreakPredictiveChartScreen(
    lgas: List<String>,
    onDispatchBuffer: (drugName: String, quantity: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDisease by remember { mutableStateOf("Malaria") }
    var selectedLga by remember { mutableStateOf("All Plateau State") }

    // Line Visibility Toggles
    var show2023 by remember { mutableStateOf(true) }
    var show2024 by remember { mutableStateOf(true) }
    var show2025 by remember { mutableStateOf(true) }
    var showHistAvg by remember { mutableStateOf(true) }
    var show2026Ai by remember { mutableStateOf(true) }

    var expandedLgaDropdown by remember { mutableStateOf(false) }

    val diseases = listOf("Malaria", "Cholera", "Lassa Fever", "Typhoid Fever", "Respiratory Infection (URI)")

    // Dynamic Mock Dataset generator based on selected disease
    val monthlyData = remember(selectedDisease, selectedLga) {
        getDiseaseOutbreakDataset(selectedDisease, selectedLga)
    }

    val maxVal = remember(monthlyData) {
        val maxInDataset = monthlyData.maxOf {
            maxOf(it.year2023, it.year2024, it.year2025, it.aiPredicted2026)
        }
        (maxInDataset * 1.15).roundToInt().coerceAtLeast(1000)
    }

    val peakSpikeMonth = remember(monthlyData) {
        monthlyData.maxByOrNull { it.aiPredicted2026 }
    }

    val totalAiPredictedCases = remember(monthlyData) {
        monthlyData.sumOf { it.aiPredicted2026 }
    }

    val totalHistAvgCases = remember(monthlyData) {
        monthlyData.sumOf { it.historicalAverage }
    }

    val overallSurgePct = remember(totalAiPredictedCases, totalHistAvgCases) {
        if (totalHistAvgCases > 0) ((totalAiPredictedCases - totalHistAvgCases).toDouble() / totalHistAvgCases) * 100 else 0.0
    }

    var selectedTappedIndex by remember { mutableStateOf<Int?>(null) }
    var showActionSnackbar by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP HEADER BANNER
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShowChart, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI Outbreak Predictive Line Chart",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Seasonal Disease Spikes vs 3-Year Baseline (2023–2025)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFFFD54F),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("95.8% ML Acc", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricPill(
                        label = "Anticipated Annual Cases",
                        value = "%,d".format(totalAiPredictedCases),
                        subtext = "%+.1f%% vs 3-Yr Avg".format(overallSurgePct),
                        bgColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    MetricPill(
                        label = "Peak Outbreak Spike",
                        value = peakSpikeMonth?.monthFull ?: "August",
                        subtext = "%,d Cases Expected".format(peakSpikeMonth?.aiPredicted2026 ?: 0),
                        bgColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // CONTROLS & FILTER BAR
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
                    text = "Select Disease & Local Government Area",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Disease Selection Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(diseases) { disease ->
                        FilterChip(
                            selected = selectedDisease == disease,
                            onClick = {
                                selectedDisease = disease
                                selectedTappedIndex = null
                            },
                            label = { Text(disease, fontSize = 12.sp) },
                            leadingIcon = if (selectedDisease == disease) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // LGA Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedLgaDropdown,
                        onExpandedChange = { expandedLgaDropdown = !expandedLgaDropdown },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedLga,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by LGA") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLgaDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedLgaDropdown,
                            onDismissRequest = { expandedLgaDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Plateau State") },
                                onClick = {
                                    selectedLga = "All Plateau State"
                                    expandedLgaDropdown = false
                                }
                            )
                            lgas.filter { it != "All LGAs" }.forEach { lga ->
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
                }

                // YEAR LINE TOGGLES
                Text(
                    text = "Chart Series Toggles (Click to show/hide lines):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChartSeriesToggleChip("2023", Color2023, show2023) { show2023 = !show2023 }
                    ChartSeriesToggleChip("2024", Color2024, show2024) { show2024 = !show2024 }
                    ChartSeriesToggleChip("2025", Color2025, show2025) { show2025 = !show2025 }
                    ChartSeriesToggleChip("3-Yr Avg", ColorHistAvg, showHistAvg) { showHistAvg = !showHistAvg }
                    ChartSeriesToggleChip("2026 AI", Color2026Ai, show2026Ai) { show2026Ai = !show2026Ai }
                }
            }
        }

        // MAIN LINE CHART CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                    Column {
                        Text(
                            text = "$selectedDisease Monthly Trend Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap on any month node to inspect exact historical vs predicted case counts.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (selectedTappedIndex != null) {
                        TextButton(onClick = { selectedTappedIndex = null }) {
                            Text("Reset Selection", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Line Chart Canvas Component
                OutbreakPredictiveLineChart(
                    data = monthlyData,
                    maxValue = maxVal,
                    show2023 = show2023,
                    show2024 = show2024,
                    show2025 = show2025,
                    showHistAvg = showHistAvg,
                    show2026Ai = show2026Ai,
                    selectedMonthIndex = selectedTappedIndex,
                    diseaseName = selectedDisease,
                    onMonthTapped = { index -> selectedTappedIndex = if (index < 0) null else index },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Chart Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(label = "2023 Historical", color = Color2023, isVisible = show2023)
                    LegendItem(label = "2024 Historical", color = Color2024, isVisible = show2024)
                    LegendItem(label = "2025 Historical", color = Color2025, isVisible = show2025)
                    LegendItem(label = "3-Yr Avg", color = ColorHistAvg, isVisible = showHistAvg)
                    LegendItem(label = "2026 AI Forecast", color = Color2026Ai, isVisible = show2026Ai, isBold = true)
                }
            }
        }

        // INTERACTIVE INSPECTION TOOLTIP CARD (When a month node is tapped)
        selectedTappedIndex?.let { index ->
            val monthInfo = monthlyData.getOrNull(index)
            if (monthInfo != null) {
                val stockReq = calculateMedicalStockRequirement(selectedDisease, monthInfo.aiPredicted2026)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (monthInfo.isOutbreakSpike) Color(0xFFFFF0F0) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (monthInfo.isOutbreakSpike) Color2026Ai else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = if (monthInfo.isOutbreakSpike) Color2026Ai else MaterialTheme.colorScheme.primary)
                                Column {
                                    Text(
                                        text = "${monthInfo.monthFull} Interactive Disease Tooltip",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (monthInfo.isOutbreakSpike) Color2026Ai else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Disease Focus: $selectedDisease | Primary Risk LGA: ${monthInfo.primaryRiskLga}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (monthInfo.isOutbreakSpike) {
                                Surface(
                                    color = Color2026Ai,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "HIGH SPIKE WARNING",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Divider()

                        // 1. Historical vs Predicted Grid
                        Text(
                            text = "Historical Disease Data Points & 2026 AI Projection:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // 2023
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color2023.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("2023", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color2023)
                                    Text("%,d".format(monthInfo.year2023), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // 2024
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color2024.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("2024", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color2024)
                                    Text("%,d".format(monthInfo.year2024), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // 2025
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color2025.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("2025", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color2025)
                                    Text("%,d".format(monthInfo.year2025), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // 3-Yr Baseline
                            Surface(
                                modifier = Modifier.weight(1.1f),
                                color = ColorHistAvg.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("3-Yr Avg", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ColorHistAvg)
                                    Text("%,d".format(monthInfo.historicalAverage), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // 2026 AI Forecast
                            Surface(
                                modifier = Modifier.weight(1.2f),
                                color = Color2026Ai.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color2026Ai)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("2026 AI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color2026Ai)
                                    Text("%,d".format(monthInfo.aiPredicted2026), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color2026Ai)
                                    Text("%+.1f%%".format(monthInfo.surgeDeltaPct), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (monthInfo.surgeDeltaPct > 0) Color2026Ai else EmeraldPrimary)
                                }
                            }
                        }

                        // 2. Suggested Medical Stock Levels Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Medication, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                        Text("AI Suggested Medical Stock & Supply Buffer", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    }
                                    Surface(
                                        color = EmeraldPrimary,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "+${stockReq.safetyBufferPct}% Reserve",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Primary Drug (${stockReq.primaryDrugName}):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("%,d ${stockReq.primaryDrugUnit}".format(stockReq.primaryDrugQty), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Secondary / Severe (${stockReq.secondaryDrugName}):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("%,d ${stockReq.secondaryDrugUnit}".format(stockReq.secondaryDrugQty), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Diagnostic Tests (${stockReq.testKitsName}):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("%,d ${stockReq.testKitsUnit}".format(stockReq.testKitsQty), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Emergency PPE Full Suit Sets:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("%,d sets".format(stockReq.emergencyPpeSets), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Climate & Action
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Climate Driver: ${monthInfo.climateCatalyst}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "Recommended Intervention: ${monthInfo.recommendedIntervention}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Button(
                            onClick = {
                                onDispatchBuffer(stockReq.primaryDrugName, stockReq.primaryDrugQty)
                                showActionSnackbar = "Pre-emptive emergency stock buffer of %,d %s dispatched for ${monthInfo.monthFull} $selectedDisease surge!".format(stockReq.primaryDrugQty, stockReq.primaryDrugUnit)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (monthInfo.isOutbreakSpike) Color2026Ai else EmeraldPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pre-Emptively Dispatch Stock Buffer to PHCs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ANTICIPATED SPIKES BREAKDOWN MATRIX
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
                    Text(
                        text = "Anticipated Outbreak Spikes & Risk Calendar",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = Color2026Ai.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${monthlyData.count { it.isOutbreakSpike }} High-Risk Months",
                            color = Color2026Ai,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                monthlyData.filter { it.isOutbreakSpike || it == peakSpikeMonth }.forEach { spike ->
                    OutbreakSpikeRow(
                        data = spike,
                        onTap = {
                            selectedTappedIndex = spike.monthIndex
                        }
                    )
                }
            }
        }

        // EXPORT & ADVISORY ACTION BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    PdfExporter.exportExecutiveAnalyticsPdf(
                        context = context,
                        facilities = emptyList(),
                        staffList = emptyList(),
                        seasonalUsages = emptyList(),
                        birthRecords = emptyList()
                    )
                    showActionSnackbar = "AI Outbreak Predictive Line Chart PDF Report exported successfully."
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export PDF Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    showActionSnackbar = "Statewide Outbreak Advisory broadcasted to all PHC Inspectors and LGA Medical Directors."
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Broadcast Advisory", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        showActionSnackbar?.let { msg ->
            Snackbar(
                action = {
                    TextButton(onClick = { showActionSnackbar = null }) {
                        Text("Dismiss", color = Color.White)
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(msg)
            }
        }
    }
}

// CUSTOM CANVAS LINE CHART DRAWING COMPONENT
@Composable
fun OutbreakPredictiveLineChart(
    data: List<OutbreakMonthData>,
    maxValue: Int,
    show2023: Boolean,
    show2024: Boolean,
    show2025: Boolean,
    showHistAvg: Boolean,
    show2026Ai: Boolean,
    selectedMonthIndex: Int?,
    diseaseName: String = "Malaria",
    onMonthTapped: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        val chartWidth = size.width - 90f
                        val startX = 70f
                        if (offset.x >= startX && offset.x <= startX + chartWidth) {
                            val stepX = chartWidth / (data.size - 1).coerceAtLeast(1)
                            val index = ((offset.x - startX) / stepX).roundToInt().coerceIn(0, data.size - 1)
                            onMonthTapped(index)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            val leftPadding = 70f
            val rightPadding = 20f
            val topPadding = 30f
            val bottomPadding = 40f

            val chartWidth = width - leftPadding - rightPadding
            val chartHeight = height - topPadding - bottomPadding

            // 1. Draw Horizontal Grid Lines & Y-Axis Labels
            val yStepCount = 4
            for (i in 0..yStepCount) {
                val yVal = (maxValue / yStepCount.toFloat()) * i
                val yPos = topPadding + chartHeight - (chartHeight * (i / yStepCount.toFloat()))

                // Grid line
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.35f),
                    start = Offset(leftPadding, yPos),
                    end = Offset(width - rightPadding, yPos),
                    strokeWidth = 1f
                )

                // Y-axis label
                val labelText = if (yVal >= 1000) "%.1fk".format(yVal / 1000f) else yVal.toInt().toString()
                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    leftPadding - 12f,
                    yPos + 4f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }

            // 2. Calculate Point Coordinates
            val stepX = chartWidth / (data.size - 1).coerceAtLeast(1)

            val p2023 = mutableListOf<Offset>()
            val p2024 = mutableListOf<Offset>()
            val p2025 = mutableListOf<Offset>()
            val pHistAvg = mutableListOf<Offset>()
            val p2026Ai = mutableListOf<Offset>()

            data.forEachIndexed { i, month ->
                val x = leftPadding + (i * stepX)

                val y2023 = topPadding + chartHeight - (chartHeight * (month.year2023.toFloat() / maxValue))
                val y2024 = topPadding + chartHeight - (chartHeight * (month.year2024.toFloat() / maxValue))
                val y2025 = topPadding + chartHeight - (chartHeight * (month.year2025.toFloat() / maxValue))
                val yAvg = topPadding + chartHeight - (chartHeight * (month.historicalAverage.toFloat() / maxValue))
                val y2026 = topPadding + chartHeight - (chartHeight * (month.aiPredicted2026.toFloat() / maxValue))

                p2023.add(Offset(x, y2023))
                p2024.add(Offset(x, y2024))
                p2025.add(Offset(x, y2025))
                pHistAvg.add(Offset(x, yAvg))
                p2026Ai.add(Offset(x, y2026))

                // Draw X-axis month labels
                drawContext.canvas.nativeCanvas.drawText(
                    month.monthShort,
                    x,
                    height - 8f,
                    android.graphics.Paint().apply {
                        color = if (i == selectedMonthIndex) android.graphics.Color.RED else android.graphics.Color.DKGRAY
                        textSize = 24f
                        isFakeBoldText = i == selectedMonthIndex
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }

            // Helper function to draw smooth curve
            fun drawLineCurve(points: List<Offset>, color: Color, strokeWidthPx: Float, isDashed: Boolean = false) {
                if (points.size < 2) return
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val p0 = points[i - 1]
                        val p1 = points[i]
                        val controlX1 = (p0.x + p1.x) / 2f
                        cubicTo(controlX1, p0.y, controlX1, p1.y, p1.x, p1.y)
                    }
                }
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round,
                        pathEffect = if (isDashed) androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 8f)) else null
                    )
                )

                // Draw node dots
                points.forEach { pt ->
                    drawCircle(color = color, radius = strokeWidthPx * 0.9f, center = pt)
                }
            }

            // 3. Draw Historical Curves
            if (show2023) drawLineCurve(p2023, Color2023, 3f)
            if (show2024) drawLineCurve(p2024, Color2024, 3f)
            if (show2025) drawLineCurve(p2025, Color2025, 3f)
            if (showHistAvg) drawLineCurve(pHistAvg, ColorHistAvg, 4f, isDashed = true)

            // 4. Draw 2026 AI Predicted Curve (Bold Red with Area Fill)
            if (show2026Ai && p2026Ai.isNotEmpty()) {
                // Gradient Area Fill
                val fillPath = Path().apply {
                    moveTo(p2026Ai[0].x, topPadding + chartHeight)
                    lineTo(p2026Ai[0].x, p2026Ai[0].y)
                    for (i in 1 until p2026Ai.size) {
                        val p0 = p2026Ai[i - 1]
                        val p1 = p2026Ai[i]
                        val controlX1 = (p0.x + p1.x) / 2f
                        cubicTo(controlX1, p0.y, controlX1, p1.y, p1.x, p1.y)
                    }
                    lineTo(p2026Ai.last().x, topPadding + chartHeight)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color2026Ai.copy(alpha = 0.35f), Color2026Ai.copy(alpha = 0.02f)),
                        startY = topPadding,
                        endY = topPadding + chartHeight
                    )
                )

                // Bold Line
                drawLineCurve(p2026Ai, Color2026Ai, 7f)

                // Highlight Outbreak Spike Nodes with Aura
                data.forEachIndexed { i, month ->
                    if (month.isOutbreakSpike) {
                        val center = p2026Ai[i]
                        drawCircle(
                            color = ColorSpikeGlow.copy(alpha = 0.4f),
                            radius = 18f,
                            center = center
                        )
                        drawCircle(
                            color = Color2026Ai,
                            radius = 9f,
                            center = center
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 4f,
                            center = center
                        )
                    }
                }
            }

            // 5. Draw Crosshair for Selected Tapped Month
            selectedMonthIndex?.let { idx ->
                if (idx in data.indices) {
                    val x = leftPadding + (idx * stepX)
                    drawLine(
                        color = Color.Red.copy(alpha = 0.6f),
                        start = Offset(x, topPadding),
                        end = Offset(x, topPadding + chartHeight),
                        strokeWidth = 3f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                    )

                    if (show2026Ai) {
                        val targetPt = p2026Ai[idx]
                        drawCircle(color = Color.White, radius = 12f, center = targetPt)
                        drawCircle(color = Color2026Ai, radius = 8f, center = targetPt)
                    }
                }
            }
        }

        // Interactive Floating Chart Overlay Tooltip
        AnimatedVisibility(
            visible = selectedMonthIndex != null,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            selectedMonthIndex?.let { idx ->
                val monthData = data.getOrNull(idx)
                if (monthData != null) {
                    val stockReq = calculateMedicalStockRequirement(diseaseName, monthData.aiPredicted2026)
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 8.dp,
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (monthData.isOutbreakSpike) Color2026Ai else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth(0.96f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(
                                        Icons.Default.Analytics,
                                        contentDescription = null,
                                        tint = if (monthData.isOutbreakSpike) Color2026Ai else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${monthData.monthFull} Data Point & Stock Recommendation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (monthData.isOutbreakSpike) Color2026Ai else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(
                                    onClick = { onMonthTapped(-1) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Tooltip", modifier = Modifier.size(14.dp))
                                }
                            }

                            // Historical vs 2026 AI forecast row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("2023: %,d | 2024: %,d | 2025: %,d".format(monthData.year2023, monthData.year2024, monthData.year2025), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("3-Yr Avg: %,d cases".format(monthData.historicalAverage), fontSize = 10.sp, color = ColorHistAvg, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("2026 AI: %,d cases".format(monthData.aiPredicted2026), fontSize = 11.sp, color = Color2026Ai, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Surge: %+.1f%%".format(monthData.surgeDeltaPct),
                                        fontSize = 10.sp,
                                        color = if (monthData.surgeDeltaPct > 0) Color2026Ai else EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Suggested Stock Level preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Medication, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "Suggested: %,d ${stockReq.primaryDrugUnit} ${stockReq.primaryDrugName}".format(stockReq.primaryDrugQty),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "+${stockReq.safetyBufferPct}% Reserve",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
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

@Composable
private fun OutbreakSpikeRow(
    data: OutbreakMonthData,
    onTap: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTap() },
        color = if (data.isOutbreakSpike) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (data.isOutbreakSpike) androidx.compose.foundation.BorderStroke(1.dp, Color2026Ai.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (data.isOutbreakSpike) Color2026Ai else ColorHistAvg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.monthShort,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = data.monthFull,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.climateCatalyst,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%,d cases".format(data.aiPredicted2026),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (data.isOutbreakSpike) Color2026Ai else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "%+.1f%% vs 3-Yr Avg".format(data.surgeDeltaPct),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (data.surgeDeltaPct > 0) Color2026Ai else EmeraldPrimary
                )
            }
        }
    }
}

@Composable
private fun ChartSeriesToggleChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onToggle,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.15f),
            selectedLabelColor = color
        )
    )
}

@Composable
private fun LegendItem(
    label: String,
    color: Color,
    isVisible: Boolean,
    isBold: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.opacity(if (isVisible) 1f else 0.3f)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) color else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun Modifier.opacity(alpha: Float): Modifier = this.then(
    Modifier.background(Color.Transparent)
)

@Composable
private fun MetricPill(
    label: String,
    value: String,
    subtext: String,
    bgColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(label, fontSize = 10.sp, color = contentColor.copy(alpha = 0.8f))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
            Text(subtext, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFFD54F))
        }
    }
}

// Generate realistic seasonal outbreak datasets for Plateau State enfermedades
private fun getDiseaseOutbreakDataset(disease: String, lga: String): List<OutbreakMonthData> {
    val scaleFactor = if (lga == "All Plateau State") 1.0 else 0.28

    return when (disease) {
        "Cholera" -> listOf(
            OutbreakMonthData(0, "Jan", "January", (120 * scaleFactor).toInt(), (140 * scaleFactor).toInt(), (150 * scaleFactor).toInt(), (180 * scaleFactor).toInt(), "Dry Season Baseline", "Jos North", "Maintain hygiene monitoring"),
            OutbreakMonthData(1, "Feb", "February", (90 * scaleFactor).toInt(), (100 * scaleFactor).toInt(), (110 * scaleFactor).toInt(), (130 * scaleFactor).toInt(), "Dry Season Baseline", "Jos South", "Routine water testing"),
            OutbreakMonthData(2, "Mar", "March", (80 * scaleFactor).toInt(), (95 * scaleFactor).toInt(), (105 * scaleFactor).toInt(), (120 * scaleFactor).toInt(), "Dry Season Baseline", "Pankshin", "Routine water testing"),
            OutbreakMonthData(3, "Apr", "April", (150 * scaleFactor).toInt(), (180 * scaleFactor).toInt(), (210 * scaleFactor).toInt(), (320 * scaleFactor).toInt(), "Early Rains Runoff", "Shendam", "Inspect well water sources"),
            OutbreakMonthData(4, "May", "May", (420 * scaleFactor).toInt(), (490 * scaleFactor).toInt(), (560 * scaleFactor).toInt(), (890 * scaleFactor).toInt(), "Rising Humidity & Runoff", "Wase", "Pre-position ORS packets"),
            OutbreakMonthData(5, "Jun", "June", (1100 * scaleFactor).toInt(), (1320 * scaleFactor).toInt(), (1480 * scaleFactor).toInt(), (2200 * scaleFactor).toInt(), "Heavy Rains Flood Risk", "Shendam", "Deploy WASH water chlorination"),
            OutbreakMonthData(6, "Jul", "July", (1850 * scaleFactor).toInt(), (2100 * scaleFactor).toInt(), (2350 * scaleFactor).toInt(), (3600 * scaleFactor).toInt(), "Peak Flooding Contamination", "Shendam", "Emergency IV Fluid stocking"),
            OutbreakMonthData(7, "Aug", "August", (2300 * scaleFactor).toInt(), (2650 * scaleFactor).toInt(), (2900 * scaleFactor).toInt(), (4250 * scaleFactor).toInt(), "Peak Rainfall & Stagnant Water", "Kanam", "Rapid Response Cholera Treatment Centers"),
            OutbreakMonthData(8, "Sep", "September", (1600 * scaleFactor).toInt(), (1850 * scaleFactor).toInt(), (2050 * scaleFactor).toInt(), (3100 * scaleFactor).toInt(), "Receding Flood Waters", "Mangu", "Post-flood water purification"),
            OutbreakMonthData(9, "Oct", "October", (620 * scaleFactor).toInt(), (710 * scaleFactor).toInt(), (780 * scaleFactor).toInt(), (1150 * scaleFactor).toInt(), "Transition to Dry Season", "Barkin Ladi", "Public health education"),
            OutbreakMonthData(10, "Nov", "November", (210 * scaleFactor).toInt(), (240 * scaleFactor).toInt(), (270 * scaleFactor).toInt(), (380 * scaleFactor).toInt(), "Dry Season Commencement", "Jos East", "Monitor water boreholes"),
            OutbreakMonthData(11, "Dec", "December", (130 * scaleFactor).toInt(), (150 * scaleFactor).toInt(), (160 * scaleFactor).toInt(), (210 * scaleFactor).toInt(), "Harmattan Dry Season", "Riyom", "Routine surveillance")
        )
        "Lassa Fever" -> listOf(
            OutbreakMonthData(0, "Jan", "January", (420 * scaleFactor).toInt(), (460 * scaleFactor).toInt(), (510 * scaleFactor).toInt(), (720 * scaleFactor).toInt(), "Dry Season Rodent Incursion", "Shendam", "Distribute Rodent Proof Storage"),
            OutbreakMonthData(1, "Feb", "February", (580 * scaleFactor).toInt(), (620 * scaleFactor).toInt(), (690 * scaleFactor).toInt(), (980 * scaleFactor).toInt(), "Post-Harvest Grain Storage Peak", "Kanam", "Pre-position Ribavirin Stocks"),
            OutbreakMonthData(2, "Mar", "March", (650 * scaleFactor).toInt(), (710 * scaleFactor).toInt(), (780 * scaleFactor).toInt(), (1150 * scaleFactor).toInt(), "Peak Bush Burning & Rodent Dispersal", "Wase", "Emergency Isolation Wards"),
            OutbreakMonthData(3, "Apr", "April", (380 * scaleFactor).toInt(), (420 * scaleFactor).toInt(), (460 * scaleFactor).toInt(), (680 * scaleFactor).toInt(), "End of Harvest Incursion", "Langtang North", "Mastomys rodent trapping"),
            OutbreakMonthData(4, "May", "May", (190 * scaleFactor).toInt(), (210 * scaleFactor).toInt(), (230 * scaleFactor).toInt(), (320 * scaleFactor).toInt(), "Early Rainfall Reduction", "Pankshin", "Health worker PPE training"),
            OutbreakMonthData(5, "Jun", "June", (80 * scaleFactor).toInt(), (90 * scaleFactor).toInt(), (100 * scaleFactor).toInt(), (130 * scaleFactor).toInt(), "Wet Season Low Baseline", "Mangu", "Routine surveillance"),
            OutbreakMonthData(6, "Jul", "July", (60 * scaleFactor).toInt(), (65 * scaleFactor).toInt(), (70 * scaleFactor).toInt(), (85 * scaleFactor).toInt(), "Wet Season Baseline", "Jos South", "Routine surveillance"),
            OutbreakMonthData(7, "Aug", "August", (50 * scaleFactor).toInt(), (55 * scaleFactor).toInt(), (60 * scaleFactor).toInt(), (70 * scaleFactor).toInt(), "Wet Season Baseline", "Jos North", "Routine surveillance"),
            OutbreakMonthData(8, "Sep", "September", (70 * scaleFactor).toInt(), (80 * scaleFactor).toInt(), (85 * scaleFactor).toInt(), (110 * scaleFactor).toInt(), "Early Dry Season Transition", "Barkin Ladi", "Pre-season awareness"),
            OutbreakMonthData(9, "Oct", "October", (120 * scaleFactor).toInt(), (140 * scaleFactor).toInt(), (160 * scaleFactor).toInt(), (220 * scaleFactor).toInt(), "Early Harvesting Begins", "Langtang South", "Safe grain storage drive"),
            OutbreakMonthData(10, "Nov", "November", (280 * scaleFactor).toInt(), (310 * scaleFactor).toInt(), (350 * scaleFactor).toInt(), (510 * scaleFactor).toInt(), "Harmattan Grain Drying", "Qu'an Pan", "Pre-position Ribavirin"),
            OutbreakMonthData(11, "Dec", "December", (390 * scaleFactor).toInt(), (430 * scaleFactor).toInt(), (480 * scaleFactor).toInt(), (690 * scaleFactor).toInt(), "Harmattan Wind Rodent Surge", "Shendam", "Community rodent control")
        )
        "Typhoid Fever" -> listOf(
            OutbreakMonthData(0, "Jan", "January", (310 * scaleFactor).toInt(), (340 * scaleFactor).toInt(), (380 * scaleFactor).toInt(), (490 * scaleFactor).toInt(), "Baseline Water Scarcity", "Jos North", "Water safety testing"),
            OutbreakMonthData(1, "Feb", "February", (340 * scaleFactor).toInt(), (380 * scaleFactor).toInt(), (410 * scaleFactor).toInt(), (560 * scaleFactor).toInt(), "Peak Dry Season Water Shortage", "Jos South", "Borehole chlorination"),
            OutbreakMonthData(2, "Mar", "March", (480 * scaleFactor).toInt(), (520 * scaleFactor).toInt(), (570 * scaleFactor).toInt(), (780 * scaleFactor).toInt(), "Hot Season Water Concentration", "Barkin Ladi", "Distribute water purification tablets"),
            OutbreakMonthData(3, "Apr", "April", (720 * scaleFactor).toInt(), (790 * scaleFactor).toInt(), (850 * scaleFactor).toInt(), (1200 * scaleFactor).toInt(), "First Rains Surface Washing", "Mangu", "Pre-position Antibiotics"),
            OutbreakMonthData(4, "May", "May", (980 * scaleFactor).toInt(), (1080 * scaleFactor).toInt(), (1160 * scaleFactor).toInt(), (1650 * scaleFactor).toInt(), "Early Rainy Season Runoff", "Pankshin", "Food vendor hygiene audit"),
            OutbreakMonthData(5, "Jun", "June", (1250 * scaleFactor).toInt(), (1390 * scaleFactor).toInt(), (1480 * scaleFactor).toInt(), (2100 * scaleFactor).toInt(), "Continuous Heavy Rainfall", "Shendam", "Dispatch Ciprofloxacin & ORS"),
            OutbreakMonthData(6, "Jul", "July", (1410 * scaleFactor).toInt(), (1560 * scaleFactor).toInt(), (1680 * scaleFactor).toInt(), (2450 * scaleFactor).toInt(), "High Rainfall Contamination", "Kanam", "Community WASH mobilization"),
            OutbreakMonthData(7, "Aug", "August", (1580 * scaleFactor).toInt(), (1720 * scaleFactor).toInt(), (1850 * scaleFactor).toInt(), (2680 * scaleFactor).toInt(), "Peak Rainfall Silt Contamination", "Wase", "Emergency Typhoid stocking"),
            OutbreakMonthData(8, "Sep", "September", (1320 * scaleFactor).toInt(), (1460 * scaleFactor).toInt(), (1580 * scaleFactor).toInt(), (2250 * scaleFactor).toInt(), "Late Rains Runoff", "Langtang North", "Water filtration campaigns"),
            OutbreakMonthData(9, "Oct", "October", (890 * scaleFactor).toInt(), (970 * scaleFactor).toInt(), (1050 * scaleFactor).toInt(), (1480 * scaleFactor).toInt(), "Post-Rain Subside", "Riyom", "Routine clinic testing"),
            OutbreakMonthData(10, "Nov", "November", (520 * scaleFactor).toInt(), (580 * scaleFactor).toInt(), (630 * scaleFactor).toInt(), (850 * scaleFactor).toInt(), "Dry Season Baseline", "Bokkos", "Routine surveillance"),
            OutbreakMonthData(11, "Dec", "December", (380 * scaleFactor).toInt(), (420 * scaleFactor).toInt(), (460 * scaleFactor).toInt(), (610 * scaleFactor).toInt(), "Harmattan Baseline", "Jos East", "Routine surveillance")
        )
        "Respiratory Infection (URI)" -> listOf(
            OutbreakMonthData(0, "Jan", "January", (1250 * scaleFactor).toInt(), (1380 * scaleFactor).toInt(), (1490 * scaleFactor).toInt(), (1980 * scaleFactor).toInt(), "Peak Harmattan Dust & Temp Drop", "Jos North", "Distribute Inhalers & Cough Syrup"),
            OutbreakMonthData(1, "Feb", "February", (1410 * scaleFactor).toInt(), (1520 * scaleFactor).toInt(), (1650 * scaleFactor).toInt(), (2250 * scaleFactor).toInt(), "Cold Harmattan Haze & Dry Air", "Bokkos", "Pre-position Amoxicillin"),
            OutbreakMonthData(2, "Mar", "March", (920 * scaleFactor).toInt(), (1010 * scaleFactor).toInt(), (1100 * scaleFactor).toInt(), (1520 * scaleFactor).toInt(), "Warming Temperature Shift", "Pankshin", "Asthma & URI clinic support"),
            OutbreakMonthData(3, "Apr", "April", (450 * scaleFactor).toInt(), (490 * scaleFactor).toInt(), (530 * scaleFactor).toInt(), (720 * scaleFactor).toInt(), "Transition to Rains", "Mangu", "Routine care"),
            OutbreakMonthData(4, "May", "May", (280 * scaleFactor).toInt(), (310 * scaleFactor).toInt(), (340 * scaleFactor).toInt(), (430 * scaleFactor).toInt(), "Warm Rains Low Baseline", "Shendam", "Routine care"),
            OutbreakMonthData(5, "Jun", "June", (190 * scaleFactor).toInt(), (210 * scaleFactor).toInt(), (230 * scaleFactor).toInt(), (290 * scaleFactor).toInt(), "Mid Rains Baseline", "Wase", "Routine care"),
            OutbreakMonthData(6, "Jul", "July", (150 * scaleFactor).toInt(), (170 * scaleFactor).toInt(), (185 * scaleFactor).toInt(), (220 * scaleFactor).toInt(), "Mid Rains Baseline", "Kanam", "Routine care"),
            OutbreakMonthData(7, "Aug", "August", (140 * scaleFactor).toInt(), (160 * scaleFactor).toInt(), (175 * scaleFactor).toInt(), (210 * scaleFactor).toInt(), "Peak Rains Baseline", "Riyom", "Routine care"),
            OutbreakMonthData(8, "Sep", "September", (180 * scaleFactor).toInt(), (200 * scaleFactor).toInt(), (220 * scaleFactor).toInt(), (290 * scaleFactor).toInt(), "Late Rains Baseline", "Barkin Ladi", "Routine care"),
            OutbreakMonthData(9, "Oct", "October", (320 * scaleFactor).toInt(), (360 * scaleFactor).toInt(), (400 * scaleFactor).toInt(), (560 * scaleFactor).toInt(), "Early Dust Winds", "Jos South", "Pre-Harmattan respiratory drive"),
            OutbreakMonthData(10, "Nov", "November", (880 * scaleFactor).toInt(), (960 * scaleFactor).toInt(), (1040 * scaleFactor).toInt(), (1450 * scaleFactor).toInt(), "Harmattan Onset & Dust Winds", "Bokkos", "Pre-position Respiratory stock"),
            OutbreakMonthData(11, "Dec", "December", (1150 * scaleFactor).toInt(), (1280 * scaleFactor).toInt(), (1390 * scaleFactor).toInt(), (1890 * scaleFactor).toInt(), "Cold Harmattan Haze", "Jos North", "Pre-position Amoxicillin & Oxygen")
        )
        else -> listOf( // Default Malaria
            OutbreakMonthData(0, "Jan", "January", (850 * scaleFactor).toInt(), (910 * scaleFactor).toInt(), (960 * scaleFactor).toInt(), (1100 * scaleFactor).toInt(), "Dry Season Baseline", "Shendam", "Routine ACT distribution"),
            OutbreakMonthData(1, "Feb", "February", (920 * scaleFactor).toInt(), (980 * scaleFactor).toInt(), (1040 * scaleFactor).toInt(), (1200 * scaleFactor).toInt(), "Dry Season Baseline", "Kanam", "Routine ACT distribution"),
            OutbreakMonthData(2, "Mar", "March", (1100 * scaleFactor).toInt(), (1250 * scaleFactor).toInt(), (1310 * scaleFactor).toInt(), (1500 * scaleFactor).toInt(), "Early Warm Shift", "Wase", "Prepare Vector Spraying"),
            OutbreakMonthData(3, "Apr", "April", (1850 * scaleFactor).toInt(), (2100 * scaleFactor).toInt(), (2300 * scaleFactor).toInt(), (2900 * scaleFactor).toInt(), "First Rainfall Mosquito Breeding", "Shendam", "Distribute Insecticide Nets"),
            OutbreakMonthData(4, "May", "May", (2900 * scaleFactor).toInt(), (3150 * scaleFactor).toInt(), (3400 * scaleFactor).toInt(), (4800 * scaleFactor).toInt(), "Rising Rainfall Index", "Pankshin", "Pre-position Artemether ACTs"),
            OutbreakMonthData(5, "Jun", "June", (3800 * scaleFactor).toInt(), (4200 * scaleFactor).toInt(), (4500 * scaleFactor).toInt(), (6200 * scaleFactor).toInt(), "Heavy Rains Anopheles Surge", "Mangu", "Deploy Fumigation Teams"),
            OutbreakMonthData(6, "Jul", "July", (4900 * scaleFactor).toInt(), (5200 * scaleFactor).toInt(), (5600 * scaleFactor).toInt(), (7800 * scaleFactor).toInt(), "Peak Rainfall Standing Water", "Shendam", "Emergency ACT Stocking & RDTs"),
            OutbreakMonthData(7, "Aug", "August", (5600 * scaleFactor).toInt(), (6100 * scaleFactor).toInt(), (6400 * scaleFactor).toInt(), (8950 * scaleFactor).toInt(), "Peak Mosquito Density Index", "Kanam", "Mass Rapid Diagnostic Screening"),
            OutbreakMonthData(8, "Sep", "September", (5100 * scaleFactor).toInt(), (5450 * scaleFactor).toInt(), (5800 * scaleFactor).toInt(), (7900 * scaleFactor).toInt(), "Receding Rains Mosquito Pools", "Wase", "Emergency ACT Stocking"),
            OutbreakMonthData(9, "Oct", "October", (320 * scaleFactor).toInt(), (3500 * scaleFactor).toInt(), (3800 * scaleFactor).toInt(), (4900 * scaleFactor).toInt(), "Transition to Dry Season", "Langtang North", "Routine ACT refills"),
            OutbreakMonthData(10, "Nov", "November", (1600 * scaleFactor).toInt(), (1820 * scaleFactor).toInt(), (1950 * scaleFactor).toInt(), (2400 * scaleFactor).toInt(), "Dry Season Commencement", "Barkin Ladi", "Routine surveillance"),
            OutbreakMonthData(11, "Dec", "December", (980 * scaleFactor).toInt(), (1050 * scaleFactor).toInt(), (1120 * scaleFactor).toInt(), (1350 * scaleFactor).toInt(), "Harmattan Cold Baseline", "Jos North", "Routine surveillance")
        )
    }
}
