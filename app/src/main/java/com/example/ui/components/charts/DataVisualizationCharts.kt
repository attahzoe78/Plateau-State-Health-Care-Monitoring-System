package com.example.ui.components.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BirthRecordEntity
import com.example.data.entity.PatientRecordEntity
import com.example.data.entity.SeasonalDrugUsageEntity
import com.example.ui.theme.EmeraldPrimary

// Disease Color Palette
val MalariaColor = Color(0xFFD32F2F)  // Deep Red
val TyphoidColor = Color(0xFFE65100)  // Deep Orange
val CholeraColor = Color(0xFF0288D1)  // Ocean Blue
val LassaColor = Color(0xFF7B1FA2)    // Purple
val UriColor = Color(0xFF2E7D32)      // Forest Green
val DefaultDiseaseColor = Color(0xFF00897B)

val BoyGenderColor = Color(0xFF1976D2)
val GirlGenderColor = Color(0xFFD81B60)

data class SeasonalTrendDataPoint(
    val illnessName: String,
    val season: String, // "Dry Season (Harmattan)", "Hot Season", "Rainy Season"
    val estimatedCases: Int,
    val unitsDispensed: Int,
    val priorityLevel: String,
    val color: Color
)

@Composable
fun SeasonalIllnessTrendsChart(
    seasonalUsages: List<SeasonalDrugUsageEntity>,
    patientRecords: List<PatientRecordEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    var selectedDiseaseFilter by remember { mutableStateOf("All Diseases") }
    var selectedDataPoint by remember { mutableStateOf<SeasonalTrendDataPoint?>(null) }

    val diseases = listOf("All Diseases", "Malaria", "Typhoid", "Cholera", "Lassa Fever", "URI")

    // Map Room DB seasonal entities to Data Points
    val dataPoints = remember(seasonalUsages, patientRecords) {
        if (seasonalUsages.isNotEmpty()) {
            seasonalUsages.map { usage ->
                val cases = when (usage.illnessName.lowercase()) {
                    "malaria" -> usage.totalUnitsDispensedStatewide / 3
                    "typhoid" -> usage.totalUnitsDispensedStatewide / 4
                    "cholera" -> usage.totalUnitsDispensedStatewide / 2
                    "lassa fever" -> usage.totalUnitsDispensedStatewide / 5
                    else -> usage.totalUnitsDispensedStatewide / 3
                }
                val color = when (usage.illnessName.lowercase()) {
                    "malaria" -> MalariaColor
                    "typhoid" -> TyphoidColor
                    "cholera" -> CholeraColor
                    "lassa fever" -> LassaColor
                    "upper respiratory infection (uri)", "uri" -> UriColor
                    else -> DefaultDiseaseColor
                }
                SeasonalTrendDataPoint(
                    illnessName = usage.illnessName,
                    season = usage.season,
                    estimatedCases = cases.coerceAtLeast(120),
                    unitsDispensed = usage.totalUnitsDispensedStatewide,
                    priorityLevel = usage.priorityLevel,
                    color = color
                )
            }
        } else {
            // Default baseline data points if database is empty
            listOf(
                SeasonalTrendDataPoint("Malaria", "Rainy Season", 4800, 14200, "Critical", MalariaColor),
                SeasonalTrendDataPoint("Typhoid", "Hot Season", 2100, 8400, "High", TyphoidColor),
                SeasonalTrendDataPoint("Cholera", "Rainy Season", 3200, 9600, "Critical", CholeraColor),
                SeasonalTrendDataPoint("Lassa Fever", "Dry Season (Harmattan)", 850, 2500, "High", LassaColor),
                SeasonalTrendDataPoint("URI", "Dry Season (Harmattan)", 2900, 8700, "Moderate", UriColor)
            )
        }
    }

    val filteredPoints = remember(selectedDiseaseFilter, dataPoints) {
        if (selectedDiseaseFilter == "All Diseases") dataPoints
        else dataPoints.filter { it.illnessName.contains(selectedDiseaseFilter, ignoreCase = true) }
    }

    val maxVal = remember(filteredPoints) {
        (filteredPoints.maxOfOrNull { it.estimatedCases } ?: 5000).coerceAtLeast(1000)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Seasonal Illness & Outbreak Trends",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }
                    Text(
                        text = "Interactive Room Database analytics of disease surges by season",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "ROOM DB DATA",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Disease Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(diseases) { disease ->
                    FilterChip(
                        selected = selectedDiseaseFilter == disease,
                        onClick = { selectedDiseaseFilter = disease },
                        label = { Text(disease) },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Interactive Bar Graph Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(filteredPoints) {
                            detectTapGestures { tapOffset ->
                                val width = size.width
                                val barWidth = (width / (filteredPoints.size * 2f)).coerceAtLeast(20f)
                                val spacing = width / filteredPoints.size

                                filteredPoints.forEachIndexed { index, point ->
                                    val barX = index * spacing + spacing / 4f
                                    if (tapOffset.x >= barX && tapOffset.x <= barX + barWidth) {
                                        selectedDataPoint = point
                                    }
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height - 30f

                    // Draw Horizontal Gridlines
                    val gridSteps = 4
                    for (i in 0..gridSteps) {
                        val y = height - (i * (height / gridSteps))
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Draw Bars
                    val itemCount = filteredPoints.size
                    val spacing = width / itemCount
                    val barWidth = (spacing * 0.55f).coerceIn(16f, 48f)

                    filteredPoints.forEachIndexed { index, point ->
                        val barHeight = (point.estimatedCases.toFloat() / maxVal.toFloat()) * height
                        val barX = index * spacing + (spacing - barWidth) / 2f
                        val barY = height - barHeight

                        val isSelected = selectedDataPoint == point

                        // Draw Bar Shadow/Background
                        drawRoundRect(
                            color = point.color.copy(alpha = if (isSelected) 0.35f else 0.15f),
                            topLeft = Offset(barX, 0f),
                            size = Size(barWidth, height),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        // Draw Animated Active Bar
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    point.color,
                                    point.color.copy(alpha = 0.75f)
                                )
                            ),
                            topLeft = Offset(barX, barY),
                            size = Size(barWidth, barHeight.coerceAtLeast(8f)),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        if (isSelected) {
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(barX - 2f, barY - 2f),
                                size = Size(barWidth + 4f, barHeight + 4f),
                                cornerRadius = CornerRadius(10f, 10f),
                                style = Stroke(width = 3f)
                            )
                        }
                    }
                }
            }

            // Legend / Category Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                filteredPoints.take(5).forEach { point ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(point.color)
                        )
                        Text(
                            text = point.illnessName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                        )
                    }
                }
            }

            // Data Point Details Box when selected or default
            val activePoint = selectedDataPoint ?: filteredPoints.firstOrNull()
            if (activePoint != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = activePoint.color.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${activePoint.illnessName} (${activePoint.season})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = activePoint.color
                                )
                            )
                            Text(
                                text = "Est. Seasonal Cases: ${activePoint.estimatedCases} · Units Dispensed: ${activePoint.unitsDispensed}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = activePoint.color
                        ) {
                            Text(
                                text = "${activePoint.priorityLevel} Priority",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BirthRateStatisticsChart(
    birthRecords: List<BirthRecordEntity>,
    modifier: Modifier = Modifier
) {
    val totalBirths = birthRecords.size
    val boys = birthRecords.count { it.babyGender.equals("Boy", ignoreCase = true) }
    val girls = birthRecords.count { it.babyGender.equals("Girl", ignoreCase = true) }

    val boyRatio = if (totalBirths > 0) boys.toFloat() / totalBirths.toFloat() else 0.52f
    val girlRatio = 1f - boyRatio

    val normalDeliveries = birthRecords.count { it.deliveryType.contains("Normal", ignoreCase = true) }
    val cSectionDeliveries = birthRecords.count { it.deliveryType.contains("Caesarean", ignoreCase = true) || it.deliveryType.contains("C-Section", ignoreCase = true) }
    val assistedDeliveries = totalBirths - normalDeliveries - cSectionDeliveries

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChildCare,
                            contentDescription = null,
                            tint = GirlGenderColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Birth Rate & Gender Demographics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }
                    Text(
                        text = "Maternal deliveries distribution recorded in Room DB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GirlGenderColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "$totalBirths TOTAL BIRTHS",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GirlGenderColor,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Gender Distribution Donut Chart Canvas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 22f
                        val radius = (size.minDimension - strokeWidth) / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Draw Boy Arc
                        val boySweep = boyRatio * 360f
                        drawArc(
                            color = BoyGenderColor,
                            startAngle = -90f,
                            sweepAngle = boySweep,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2f, radius * 2f),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Draw Girl Arc
                        val girlSweep = girlRatio * 360f
                        drawArc(
                            color = GirlGenderColor,
                            startAngle = -90f + boySweep + 4f,
                            sweepAngle = (girlSweep - 4f).coerceAtLeast(0f),
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2f, radius * 2f),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalBirths",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "Deliveries",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BoyGenderColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Boys ($boys)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF0D47A1)
                                )
                            }
                            Text(
                                text = "%.1f%%".format(boyRatio * 100),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color(0xFF0D47A1)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFCE4EC)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(GirlGenderColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Girls ($girls)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF880E4F)
                                )
                            }
                            Text(
                                text = "%.1f%%".format(girlRatio * 100),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color(0xFF880E4F)
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Delivery Type Proportional Breakdown
            Text(
                text = "DELIVERY METHOD DISTRIBUTION",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DeliveryTypeProgressRow(
                    label = "Normal Vaginal Delivery",
                    count = normalDeliveries,
                    total = totalBirths.coerceAtLeast(1),
                    color = EmeraldPrimary
                )
                DeliveryTypeProgressRow(
                    label = "Caesarean Section (C-Section)",
                    count = cSectionDeliveries,
                    total = totalBirths.coerceAtLeast(1),
                    color = Color(0xFF0288D1)
                )
                DeliveryTypeProgressRow(
                    label = "Assisted / Complicated",
                    count = assistedDeliveries.coerceAtLeast(0),
                    total = totalBirths.coerceAtLeast(1),
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
fun DeliveryTypeProgressRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "$count (%.0f%%)".format(progress * 100),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}
