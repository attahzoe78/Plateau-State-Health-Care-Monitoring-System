package com.example.ui.components

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.entity.DrugInventoryEntity
import com.example.data.entity.FacilityEntity
import com.example.ui.theme.SleekPrimary
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

sealed class ScanResult {
    data class FacilityMatch(val facility: FacilityEntity) : ScanResult()
    data class BatchMatch(val batch: DrugInventoryEntity) : ScanResult()
    data class UnknownCode(val code: String) : ScanResult()
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScannerModal(
    facilities: List<FacilityEntity>,
    drugs: List<DrugInventoryEntity>,
    onDismissRequest: () -> Unit,
    onFacilityInspected: ((FacilityEntity) -> Unit)? = null,
    onBatchInspected: ((DrugInventoryEntity) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    var isFlashOn by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var manualInputCode by remember { mutableStateOf("") }
    var showManualInput by remember { mutableStateOf(false) }

    // Laser line animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    // Pre-calculated sample barcodes / QR tokens for testing/inspection
    val sampleFacilityCodes = remember(facilities) {
        facilities.take(5).map { fac ->
            val code = "FAC-${fac.lga.take(3).uppercase()}-${fac.id}"
            code to fac
        }
    }

    val sampleBatchCodes = remember(drugs) {
        drugs.take(5).map { drug ->
            val code = drug.batchNumber
            code to drug
        }
    }

    fun handleCodeScanned(code: String) {
        val trimmed = code.trim()
        val matchedFacility = facilities.find { fac ->
            fac.id.toString() == trimmed ||
                    trimmed.contains("FAC-${fac.id}", true) ||
                    trimmed.contains(fac.lga, true) ||
                    trimmed.contains(fac.name, true)
        }

        if (matchedFacility != null) {
            scanResult = ScanResult.FacilityMatch(matchedFacility)
            return
        }

        val matchedBatch = drugs.find { drug ->
            drug.batchNumber.equals(trimmed, ignoreCase = true) ||
                    trimmed.contains(drug.batchNumber, ignoreCase = true) ||
                    trimmed.contains(drug.drugName, ignoreCase = true)
        }

        if (matchedBatch != null) {
            scanResult = ScanResult.BatchMatch(matchedBatch)
            return
        }

        scanResult = ScanResult.UnknownCode(trimmed)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SleekPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "QR Inspection Scanner",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Plateau Facility & Supply Authentication",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (scanResult == null) {
                    // Camera Viewport Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.Black)
                            .border(2.dp, SleekPrimary, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cameraPermissionState.status.isGranted) {
                            // CameraX Viewport
                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx)
                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                    cameraProviderFuture.addListener({
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.setSurfaceProvider(previewView.surfaceProvider)
                                        }
                                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                        try {
                                            cameraProvider.unbindAll()
                                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }, ContextCompat.getMainExecutor(ctx))
                                    previewView
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Permission Request Banner
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Camera Permission Required",
                                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Grant camera access to scan physical QR codes on medical supplies & PHC door tags.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { cameraPermissionState.launchPermissionRequest() },
                                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                                ) {
                                    Text("Grant Permission")
                                }
                            }
                        }

                        // Scanning Overlay Reticle & Animated Laser Line
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val boxSize = w * 0.65f
                            val left = (w - boxSize) / 2f
                            val top = (h - boxSize) / 2f
                            val right = left + boxSize
                            val bottom = top + boxSize

                            // Dark overlay outside reticle
                            drawRect(
                                color = Color.Black.copy(alpha = 0.45f)
                            )

                            // Animated Laser Line
                            val laserY = top + (boxSize * laserYRatio)
                            drawLine(
                                color = Color(0xFF00E676),
                                start = Offset(left + 10f, laserY),
                                end = Offset(right - 10f, laserY),
                                strokeWidth = 5f
                            )

                            // Four Corner Targets
                            val cornerLen = 30f
                            val strokeW = 8f
                            val targetColor = Color.White

                            // Top Left
                            drawLine(targetColor, Offset(left, top), Offset(left + cornerLen, top), strokeW)
                            drawLine(targetColor, Offset(left, top), Offset(left, top + cornerLen), strokeW)

                            // Top Right
                            drawLine(targetColor, Offset(right, top), Offset(right - cornerLen, top), strokeW)
                            drawLine(targetColor, Offset(right, top), Offset(right, top + cornerLen), strokeW)

                            // Bottom Left
                            drawLine(targetColor, Offset(left, bottom), Offset(left + cornerLen, bottom), strokeW)
                            drawLine(targetColor, Offset(left, bottom), Offset(left, bottom - cornerLen), strokeW)

                            // Bottom Right
                            drawLine(targetColor, Offset(right, bottom), Offset(right - cornerLen, bottom), strokeW)
                            drawLine(targetColor, Offset(right, bottom), Offset(right, bottom - cornerLen), strokeW)
                        }

                        // Flash & Camera controls overlay
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            IconButton(
                                onClick = { isFlashOn = !isFlashOn },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Flash",
                                    tint = if (isFlashOn) Color.Yellow else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Sample Code Selector for Instant Testing / Inspection Simulation
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tap Sample QR Tag to Simulate Scan:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(sampleFacilityCodes) { (code, fac) ->
                                SuggestionChip(
                                    onClick = { handleCodeScanned(code) },
                                    label = { Text("🏥 ${fac.name.take(16)}...", fontSize = 11.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = SleekPrimary.copy(alpha = 0.12f))
                                )
                            }
                            items(sampleBatchCodes) { (code, drug) ->
                                SuggestionChip(
                                    onClick = { handleCodeScanned(code) },
                                    label = { Text("💊 Batch #${drug.batchNumber.take(12)}", fontSize = 11.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.15f))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Manual Code Input Toggle
                    OutlinedTextField(
                        value = manualInputCode,
                        onValueChange = { manualInputCode = it },
                        placeholder = { Text("Or type batch / facility tag...", fontSize = 12.sp) },
                        trailingIcon = {
                            if (manualInputCode.isNotEmpty()) {
                                IconButton(onClick = { handleCodeScanned(manualInputCode) }) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Submit", tint = SleekPrimary)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                } else {
                    // Display Scan Match Results
                    when (val res = scanResult!!) {
                        is ScanResult.FacilityMatch -> {
                            val fac = res.facility
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SleekPrimary.copy(alpha = 0.08f)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SleekPrimary))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Facility Verified!",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                color = SleekPrimary
                                            )
                                        }
                                        StatusBadge(text = fac.operationalStatus, type = "success")
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = fac.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                                    Text(text = "${fac.facilityType} · ${fac.lga} LGA (${fac.ward} Ward)", style = MaterialTheme.typography.bodySmall)

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Available Beds", style = MaterialTheme.typography.labelSmall)
                                            Text("${fac.availableBeds} / ${fac.totalBeds}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Column {
                                            Text("Active Personnel", style = MaterialTheme.typography.labelSmall)
                                            Text("${fac.activeStaffCount} medical staff", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Column {
                                            Text("Alert Level", style = MaterialTheme.typography.labelSmall)
                                            StatusBadge(text = fac.emergencyAlertLevel, type = if (fac.emergencyAlertLevel == "Epidemic") "danger" else "success")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            onFacilityInspected?.invoke(fac)
                                            onDismissRequest()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Open Facility Inspection Record")
                                    }
                                }
                            }
                        }

                        is ScanResult.BatchMatch -> {
                            val drug = res.batch
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFF8F00)))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Supply Batch Authenticated",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                                color = Color(0xFFE65100)
                                            )
                                        }
                                        StatusBadge(text = drug.status, type = if (drug.status == "In Stock") "success" else "warning")
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = drug.drugName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                                    Text(text = "Batch #${drug.batchNumber} · Category: ${drug.category}", style = MaterialTheme.typography.bodySmall)

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Current Stock", style = MaterialTheme.typography.labelSmall)
                                            Text("${drug.stockQuantity} ${drug.unit}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SleekPrimary))
                                        }
                                        Column {
                                            Text("Facility Location", style = MaterialTheme.typography.labelSmall)
                                            Text(drug.facilityName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Column {
                                            Text("Expiration", style = MaterialTheme.typography.labelSmall)
                                            Text(drug.expiryDate, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            onBatchInspected?.invoke(drug)
                                            onDismissRequest()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Inventory, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Audit & Update Batch Stock")
                                    }
                                }
                            }
                        }

                        is ScanResult.UnknownCode -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Unrecognized QR Tag Code",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Code '${res.code}' is not registered in the Plateau State Health Database.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    OutlinedButton(onClick = { scanResult = null }) {
                                        Text("Scan Again")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { scanResult = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Scan Another QR Code")
                    }
                }
            }
        }
    }
}
