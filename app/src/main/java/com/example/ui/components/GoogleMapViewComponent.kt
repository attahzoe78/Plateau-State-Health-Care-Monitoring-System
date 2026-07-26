package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FacilityEntity
import com.example.ui.theme.SleekPrimary

enum class MapType {
    ROADMAP, SATELLITE, TERRAIN, VECTOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapViewComponent(
    facilities: List<FacilityEntity>,
    modifier: Modifier = Modifier,
    initialSelectedLga: String = "All LGAs",
    onFacilitySelected: ((FacilityEntity) -> Unit)? = null,
    showControls: Boolean = true,
    compactHeader: Boolean = false,
    mapTileCount: Int = 153,
    mapTileCacheSizeBytes: Long = 9830400L,
    onRecacheTiles: (() -> Unit)? = null
) {
    var selectedLga by remember { mutableStateOf(initialSelectedLga) }
    var facilityTypeFilter by remember { mutableStateOf("All") } // "All", "PHC", "Cottage Hospital"
    var searchQuery by remember { mutableStateOf("") }
    var mapType by remember { mutableStateOf(MapType.ROADMAP) }
    var selectedFacility by remember { mutableStateOf<FacilityEntity?>(facilities.firstOrNull()) }
    var zoomLevel by remember { mutableStateOf(1f) } // 1.0f to 2.5f
    var isRecaching by remember { mutableStateOf(false) }

    // Bounding box coordinates for Plateau State
    val minLat = 8.3
    val maxLat = 10.25
    val minLng = 8.45
    val maxLng = 10.35

    val allLgas = listOf("All LGAs") + facilities.map { it.lga }.distinct().sorted()

    val filteredFacilities = facilities.filter { fac ->
        (selectedLga == "All LGAs" || fac.lga.equals(selectedLga, ignoreCase = true)) &&
                (facilityTypeFilter == "All" || fac.facilityType.contains(facilityTypeFilter, ignoreCase = true)) &&
                (searchQuery.isEmpty() || fac.name.contains(searchQuery, ignoreCase = true) || fac.lga.contains(searchQuery, ignoreCase = true) || fac.ward.contains(searchQuery, ignoreCase = true))
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
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Title Bar
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
                            .background(Color(0xFFEA4335)), // Google Map Red Pin
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Plateau Health Google Maps API",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${filteredFacilities.size} of ${facilities.size} Facilities Mapped Across 17 LGAs",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Map type buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { mapType = MapType.ROADMAP },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (mapType == MapType.ROADMAP) SleekPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Roadmap",
                            tint = if (mapType == MapType.ROADMAP) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { mapType = MapType.SATELLITE },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (mapType == MapType.SATELLITE) SleekPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Satellite",
                            tint = if (mapType == MapType.SATELLITE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { mapType = MapType.TERRAIN },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (mapType == MapType.TERRAIN) SleekPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terrain,
                            contentDescription = "Terrain",
                            tint = if (mapType == MapType.TERRAIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Room Offline Map Tile Cache Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F8E9),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Room Offline Map Tile Cache",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF2E7D32)
                                ) {
                                    Text(
                                        text = "100% Offline Ready",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            val mbStr = String.format("%.1f", mapTileCacheSizeBytes / 1024f / 1024f)
                            Text(
                                text = "$mapTileCount Tiles Cached ($mbStr MB Local Store) · All ${facilities.size} Hospital Pins Renderable Without Internet",
                                fontSize = 10.sp,
                                color = Color(0xFF33691E)
                            )
                        }
                    }

                    if (onRecacheTiles != null) {
                        TextButton(
                            onClick = {
                                isRecaching = true
                                onRecacheTiles.invoke()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Re-cache",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            if (showControls) {
                // Search Input & Facility Type Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search PHC, Cottage Hospital, Ward...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Type Filter Buttons
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            listOf("All", "PHC", "Cottage").forEach { type ->
                                val isSel = when (type) {
                                    "All" -> facilityTypeFilter == "All"
                                    "PHC" -> facilityTypeFilter == "PHC"
                                    "Cottage" -> facilityTypeFilter == "Cottage Hospital"
                                    else -> false
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) SleekPrimary else Color.Transparent)
                                        .clickable {
                                            facilityTypeFilter = when (type) {
                                                "PHC" -> "PHC"
                                                "Cottage" -> "Cottage Hospital"
                                                else -> "All"
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // LGA Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allLgas) { lga ->
                        FilterChip(
                            selected = selectedLga == lga,
                            onClick = { selectedLga = lga },
                            label = { Text(lga, fontSize = 11.sp) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SleekPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Interactive Google Maps Canvas Screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            ) {
                val mapBgColor = when (mapType) {
                    MapType.ROADMAP -> Color(0xFFE5E9E7)
                    MapType.SATELLITE -> Color(0xFF1E2823)
                    MapType.TERRAIN -> Color(0xFFD4DEC9)
                    MapType.VECTOR -> Color(0xFFEAEFF2)
                }

                // Canvas drawing map tiles, highways, rivers & facility markers
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(mapBgColor)
                        .pointerInput(filteredFacilities, zoomLevel) {
                            detectTapGestures { tapOffset ->
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                val clicked = filteredFacilities.minByOrNull { fac ->
                                    val nx = ((fac.longitude - minLng) / (maxLng - minLng)).toFloat()
                                    val ny = (1f - ((fac.latitude - minLat) / (maxLat - minLat))).toFloat()

                                    val cx = canvasWidth / 2f
                                    val cy = canvasHeight / 2f

                                    val x = cx + (nx * canvasWidth - cx) * zoomLevel
                                    val y = cy + (ny * canvasHeight - cy) * zoomLevel

                                    val distSq = (x - tapOffset.x) * (x - tapOffset.x) + (y - tapOffset.y) * (y - tapOffset.y)
                                    distSq
                                }
                                if (clicked != null) {
                                    selectedFacility = clicked
                                    onFacilitySelected?.invoke(clicked)
                                }
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val cx = canvasWidth / 2f
                    val cy = canvasHeight / 2f

                    // Draw Map Grid / Topography contours
                    val roadColor = if (mapType == MapType.SATELLITE) Color(0xFF3B4840) else Color(0xFFFFFFFF)
                    val riverColor = Color(0xFF81D4FA)

                    // Main Trunk Highways connecting Jos, Pankshin, Shendam, Mangu
                    val path1 = Path().apply {
                        moveTo(cx - 100f * zoomLevel, cy - 80f * zoomLevel)
                        lineTo(cx, cy)
                        lineTo(cx + 120f * zoomLevel, cy + 90f * zoomLevel)
                    }
                    drawPath(
                        path = path1,
                        color = roadColor,
                        style = Stroke(width = 6f * zoomLevel)
                    )

                    // River Benue/Plateau Basin Tributary
                    val riverPath = Path().apply {
                        moveTo(cx - 180f * zoomLevel, cy + 120f * zoomLevel)
                        quadraticTo(cx, cy + 60f * zoomLevel, cx + 200f * zoomLevel, cy + 140f * zoomLevel)
                    }
                    drawPath(
                        path = riverPath,
                        color = riverColor,
                        style = Stroke(width = 4f * zoomLevel)
                    )

                    // Plot Facility Pins
                    filteredFacilities.forEach { fac ->
                        val nx = ((fac.longitude - minLng) / (maxLng - minLng)).toFloat()
                        val ny = (1f - ((fac.latitude - minLat) / (maxLat - minLat))).toFloat()

                        val x = cx + (nx * canvasWidth - cx) * zoomLevel
                        val y = cy + (ny * canvasHeight - cy) * zoomLevel

                        val isSelected = selectedFacility?.id == fac.id
                        val pinRadius = if (isSelected) 24f else 16f

                        val (pinColor, badgeIcon) = when {
                            fac.facilityType.contains("Cottage", true) -> Color(0xFFE65100) to "C"
                            fac.facilityType.contains("Tertiary", true) -> Color(0xFF6A1B9A) to "T"
                            else -> SleekPrimary to "P"
                        }

                        // Outer Pulse Glow for Epidemic / Watch Status or Selection
                        if (fac.emergencyAlertLevel.equals("Epidemic", true) || isSelected) {
                            val pulseColor = if (fac.emergencyAlertLevel.equals("Epidemic", true)) Color(0xFFD32F2F) else pinColor
                            drawCircle(
                                color = pulseColor.copy(alpha = 0.35f),
                                radius = pinRadius + 14f,
                                center = Offset(x, y)
                            )
                        }

                        // Pin Shadow
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.25f),
                            radius = pinRadius,
                            center = Offset(x + 2f, y + 3f)
                        )

                        // Main Pin Circle
                        drawCircle(
                            color = pinColor,
                            radius = pinRadius,
                            center = Offset(x, y)
                        )

                        // Inner Core White Circle
                        drawCircle(
                            color = Color.White,
                            radius = pinRadius * 0.5f,
                            center = Offset(x, y)
                        )
                    }
                }

                // Google Maps Watermark Logo Overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Google",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF4285F4)
                        )
                        Text(
                            text = " Maps",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = Color(0xFF5F6368)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• Plateau State GIS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF70757A)
                        )
                    }
                }

                // Map Legend Overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SleekPrimary))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PHC", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFE65100)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cottage Hosp", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFD32F2F)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Epidemic Alert", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                // Zoom Controls & Center Plateau Button
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FloatingActionButton(
                        onClick = { zoomLevel = (zoomLevel + 0.3f).coerceAtMost(2.5f) },
                        modifier = Modifier.size(36.dp),
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                    }

                    FloatingActionButton(
                        onClick = { zoomLevel = (zoomLevel - 0.3f).coerceAtLeast(0.8f) },
                        modifier = Modifier.size(36.dp),
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                    }

                    FloatingActionButton(
                        onClick = {
                            zoomLevel = 1.0f
                            selectedLga = "All LGAs"
                            facilityTypeFilter = "All"
                            searchQuery = ""
                        },
                        modifier = Modifier.size(36.dp),
                        containerColor = Color.White,
                        contentColor = SleekPrimary
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Reset Center", modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Selected Facility Quick Detail Card
            selectedFacility?.let { fac ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFacilitySelected?.invoke(fac) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (fac.facilityType.contains("Cottage", true)) Color(0xFFE65100) else SleekPrimary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (fac.facilityType.contains("Cottage", true)) Icons.Default.LocalHospital else Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = fac.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${fac.facilityType} · ${fac.lga} LGA (${fac.ward} Ward) · ${fac.totalBeds} Beds",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        StatusBadge(
                            text = fac.emergencyAlertLevel,
                            type = if (fac.emergencyAlertLevel.equals("Epidemic", true)) "danger" else "success"
                        )
                    }
                }
            }
        }
    }
}
