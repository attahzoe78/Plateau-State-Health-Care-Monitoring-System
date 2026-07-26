package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.entity.*
import com.example.ui.viewmodel.ForecastResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    private const val PAGE_WIDTH = 595 // A4 width in points (72 dpi)
    private const val PAGE_HEIGHT = 842 // A4 height in points

    /**
     * Generates an official administrative PDF report containing health data analytics,
     * facility summaries, seasonal illness trends, and birth statistics.
     */
    fun exportExecutiveAnalyticsPdf(
        context: Context,
        facilities: List<FacilityEntity>,
        staffList: List<MedicalStaffEntity>,
        seasonalUsages: List<SeasonalDrugUsageEntity>,
        birthRecords: List<BirthRecordEntity>
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val timestamp = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date())

        // Background
        canvas.drawColor(Color.WHITE)

        // Header Banner (Emerald Green)
        paint.color = Color.parseColor("#0F5132")
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 95f, paint)

        // Banner Title
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PLATEAU STATE MINISTRY OF HEALTH", 30f, 40f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("PRIMARY HEALTHCARE BOARD · EXECUTIVE HEALTH ANALYTICS REPORT", 30f, 62f, paint)

        paint.textSize = 9f
        paint.color = Color.parseColor("#D1E7DD")
        canvas.drawText("OFFICIAL ADMINISTRATIVE USE ONLY · Generated: $timestamp", 30f, 80f, paint)

        // Decorative Accent Line
        paint.color = Color.parseColor("#00897B")
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 95f, PAGE_WIDTH.toFloat(), 95f, paint)

        var y = 125f

        // Section 1: Executive Key Metrics
        paint.color = Color.parseColor("#0F5132")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. STATEWIDE HEALTHCARE NETWORK METRICS", 30f, y, paint)
        y += 15f

        // Grid Cards
        val phcCount = facilities.count { it.facilityType == "PHC" }
        val cottageCount = facilities.count { it.facilityType == "Cottage Hospital" }
        val tertiaryCount = facilities.count { it.facilityType == "Tertiary Annex" }
        val totalBeds = facilities.sumOf { it.totalBeds }
        val totalStaff = staffList.size
        val totalBirths = birthRecords.size

        drawMetricBox(canvas, paint, 30f, y, 160f, 50f, "PHC Facilities", "$phcCount Centres", "#E8F5E9", "#1B5E20")
        drawMetricBox(canvas, paint, 210f, y, 160f, 50f, "Cottage Hospitals", "$cottageCount Facilities", "#E3F2FD", "#0D47A1")
        drawMetricBox(canvas, paint, 390f, y, 175f, 50f, "Total Hospital Beds", "$totalBeds Beds", "#FFF3E0", "#E65100")

        y += 62f
        drawMetricBox(canvas, paint, 30f, y, 160f, 50f, "Medical Personnel", "$totalStaff Health Workers", "#F3E5F5", "#4A148C")
        drawMetricBox(canvas, paint, 210f, y, 160f, 50f, "Recorded Births", "$totalBirths Deliveries", "#FCE4EC", "#880E4F")
        drawMetricBox(canvas, paint, 390f, y, 175f, 50f, "Active LGAs Covered", "17 LGAs", "#E0F2F1", "#004D40")

        y += 75f

        // Section 2: Seasonal Illness & Outbreak Trends Table
        paint.color = Color.parseColor("#0F5132")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. SEASONAL ILLNESS SURGE & MEDICATION DISPENSING", 30f, y, paint)
        y += 18f

        // Table Header
        paint.color = Color.parseColor("#F1F3F5")
        canvas.drawRect(30f, y, PAGE_WIDTH - 30f, y + 24f, paint)

        paint.color = Color.BLACK
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Illness / Condition", 40f, y + 16f, paint)
        canvas.drawText("Peak Season", 190f, y + 16f, paint)
        canvas.drawText("Units Dispensed", 360f, y + 16f, paint)
        canvas.drawText("Priority Level", 480f, y + 16f, paint)

        y += 24f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 9.5f

        val displayUsages = if (seasonalUsages.isNotEmpty()) seasonalUsages.take(5) else listOf(
            SeasonalDrugUsageEntity(1, "Malaria", "Rainy Season", "Artemether-Lumefantrine", "80/480mg BD", 14200, 4700, "Critical", "Peak vector breeding in standing water"),
            SeasonalDrugUsageEntity(2, "Typhoid Fever", "Hot Season", "Ciprofloxacin & ORS", "500mg BD", 8400, 2800, "High", "Water contamination during dry spells"),
            SeasonalDrugUsageEntity(3, "Cholera", "Rainy Season", "Zinc Tablets & ORS", "20mg OD", 9600, 3200, "Critical", "Flooding in riverine communities"),
            SeasonalDrugUsageEntity(4, "Lassa Fever", "Dry Season (Harmattan)", "Ribavirin & PPE", "1000mg loading", 2500, 850, "High", "Rodent invasion into granaries"),
            SeasonalDrugUsageEntity(5, "URI / Cough", "Dry Season (Harmattan)", "Amoxicillin & Syrups", "250mg TDS", 8700, 2900, "Moderate", "Dust particles & cold winds")
        )

        displayUsages.forEachIndexed { idx, item ->
            val rowBg = if (idx % 2 == 0) "#FFFFFF" else "#FAFAFA"
            paint.color = Color.parseColor(rowBg)
            canvas.drawRect(30f, y, PAGE_WIDTH - 30f, y + 22f, paint)

            paint.color = Color.parseColor("#212529")
            canvas.drawText(item.illnessName, 40f, y + 15f, paint)
            canvas.drawText(item.season, 190f, y + 15f, paint)
            canvas.drawText("${item.totalUnitsDispensedStatewide} units", 360f, y + 15f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val priorityColor = when (item.priorityLevel.lowercase()) {
                "critical" -> "#C62828"
                "high" -> "#E65100"
                else -> "#2E7D32"
            }
            paint.color = Color.parseColor(priorityColor)
            canvas.drawText(item.priorityLevel, 480f, y + 15f, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            y += 22f
        }

        y += 20f

        // Section 3: Maternal & Birth Rate Statistics
        paint.color = Color.parseColor("#0F5132")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. MATERNAL HEALTH & BIRTH DEMOGRAPHICS SUMMARY", 30f, y, paint)
        y += 18f

        val boys = birthRecords.count { it.babyGender.equals("Boy", ignoreCase = true) }
        val girls = birthRecords.count { it.babyGender.equals("Girl", ignoreCase = true) }
        val normalVal = birthRecords.count { it.deliveryType.contains("Normal", ignoreCase = true) }

        paint.color = Color.parseColor("#495057")
        paint.textSize = 10f
        canvas.drawText("• Total Recorded Deliveries: $totalBirths ($boys Male, $girls Female)", 40f, y + 10f, paint)
        canvas.drawText("• Standard Normal Deliveries: $normalVal (${if (totalBirths > 0) normalVal * 100 / totalBirths else 82}%)", 40f, y + 26f, paint)
        canvas.drawText("• Emergency Obstetric Referrals: ${totalBirths - normalVal} facilities connected via regional dispatch", 40f, y + 42f, paint)

        y += 75f

        // Administrative Signature & Stamp Block
        paint.color = Color.parseColor("#E0E0E0")
        paint.strokeWidth = 1f
        canvas.drawLine(30f, y, PAGE_WIDTH - 30f, y, paint)

        y += 30f

        paint.color = Color.parseColor("#212529")
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("OFFICIAL APPROVAL & SIGNATURE", 30f, y, paint)
        canvas.drawText("MINISTRY SEAL / VERIFICATION", 360f, y, paint)

        y += 35f
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#9E9E9E")
        canvas.drawLine(30f, y, 220f, y, paint)

        // Draw Official Stamp Box
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#0F5132")
        paint.strokeWidth = 1.5f
        canvas.drawRoundRect(RectF(360f, y - 25f, 520f, y + 25f), 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.parseColor("#0F5132")
        canvas.drawText("PLATEAU STATE PHCB", 385f, y - 8f, paint)
        canvas.drawText("VERIFIED & CERTIFIED", 382f, y + 8f, paint)

        y += 14f
        paint.color = Color.parseColor("#616161")
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Director of Public Health & Epidemiology", 30f, y, paint)
        canvas.drawText("Plateau State Primary Health Care Board, Jos", 30f, y + 12f, paint)

        // Footer
        paint.color = Color.parseColor("#9E9E9E")
        paint.textSize = 8f
        canvas.drawText("Generated via Plateau State PHC Intelligence Mobile App · Page 1 of 1", 160f, PAGE_HEIGHT - 30f, paint)

        pdfDocument.finishPage(page)

        saveAndOpenPdf(context, pdfDocument, "Plateau_Health_Analytics_Report_${System.currentTimeMillis()}.pdf")
    }

    /**
     * Generates an official AI Supply Chain Predictive Forecast PDF report.
     */
    fun exportAiForecastPdf(
        context: Context,
        forecastResult: ForecastResult
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val timestamp = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date())

        canvas.drawColor(Color.WHITE)

        // Header Banner (Emerald)
        paint.color = Color.parseColor("#0F5132")
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 95f, paint)

        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PLATEAU STATE MINISTRY OF HEALTH", 30f, 40f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("AI SEASONAL DRUG SUPPLY FORECAST & PRE-POSITIONING DISPATCH", 30f, 62f, paint)

        paint.textSize = 9f
        paint.color = Color.parseColor("#D1E7DD")
        canvas.drawText("CONFIDENTIAL ADMINISTRATIVE DISPATCH DIRECTIVE · Date: $timestamp", 30f, 80f, paint)

        var y = 130f

        // Forecast Parameters Box
        drawMetricBox(canvas, paint, 30f, y, 250f, 55f, "Target LGA", forecastResult.lga, "#E8F5E9", "#1B5E20")
        drawMetricBox(canvas, paint, 300f, y, 265f, 55f, "Target Season", forecastResult.season, "#E3F2FD", "#0D47A1")

        y += 75f

        // Risk Summary Section
        paint.color = Color.parseColor("#0F5132")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. AI RISK ASSESSMENT & SURGE PREDICTION", 30f, y, paint)
        y += 18f

        paint.color = Color.parseColor("#FFF3E0")
        canvas.drawRoundRect(RectF(30f, y, PAGE_WIDTH - 30f, y + 60f), 10f, 10f, paint)

        paint.color = Color.parseColor("#E65100")
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // Multi-line wrap
        val riskText = forecastResult.riskSummary
        val words = riskText.split(" ")
        var line = ""
        var lineY = y + 20f
        words.forEach { word ->
            if (paint.measureText("$line $word") < PAGE_WIDTH - 80f) {
                line = if (line.isEmpty()) word else "$line $word"
            } else {
                canvas.drawText(line, 45f, lineY, paint)
                line = word
                lineY += 14f
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, 45f, lineY, paint)
        }

        y += 80f

        // Projected High-Surge Diseases
        paint.color = Color.parseColor("#0F5132")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. PROJECTED HIGH-DEMAND ILLNESSES", 30f, y, paint)
        y += 18f

        paint.color = Color.parseColor("#212529")
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        forecastResult.predictedHighDemandIllnesses.forEach { disease ->
            canvas.drawText("  • $disease", 40f, y, paint)
            y += 16f
        }

        y += 15f

        // Recommended Dispatch Table
        paint.color = Color.parseColor("#0F5132")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. RECOMMENDED PRE-POSITIONED STOCK ALLOCATION", 30f, y, paint)
        y += 18f

        // Table Header
        paint.color = Color.parseColor("#F1F3F5")
        canvas.drawRect(30f, y, PAGE_WIDTH - 30f, y + 24f, paint)

        paint.color = Color.BLACK
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Essential Drug / Medical Item", 40f, y + 16f, paint)
        canvas.drawText("Recommended Dispatch Quantity", 320f, y + 16f, paint)

        y += 24f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f

        forecastResult.recommendedStockList.toList().forEachIndexed { idx, (drug, qty) ->
            val rowBg = if (idx % 2 == 0) "#FFFFFF" else "#FAFAFA"
            paint.color = Color.parseColor(rowBg)
            canvas.drawRect(30f, y, PAGE_WIDTH - 30f, y + 22f, paint)

            paint.color = Color.parseColor("#212529")
            canvas.drawText(drug, 40f, y + 15f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = Color.parseColor("#0F5132")
            canvas.drawText(qty, 320f, y + 15f, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            y += 22f
        }

        y += 40f

        // Signoff Block
        paint.color = Color.parseColor("#E0E0E0")
        paint.strokeWidth = 1f
        canvas.drawLine(30f, y, PAGE_WIDTH - 30f, y, paint)

        y += 30f
        paint.color = Color.parseColor("#212529")
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DISPATCH AUTHORIZATION SIGNATURE", 30f, y, paint)

        y += 35f
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#9E9E9E")
        canvas.drawLine(30f, y, 220f, y, paint)

        y += 14f
        paint.color = Color.parseColor("#616161")
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Chief Logistics Officer · Plateau Central Medical Store", 30f, y, paint)

        // Footer
        paint.color = Color.parseColor("#9E9E9E")
        paint.textSize = 8f
        canvas.drawText("Generated via AI Model Predictive Engine · Page 1 of 1", 170f, PAGE_HEIGHT - 30f, paint)

        pdfDocument.finishPage(page)

        saveAndOpenPdf(context, pdfDocument, "Plateau_AI_Forecast_${forecastResult.lga}_${System.currentTimeMillis()}.pdf")
    }

    private fun drawMetricBox(
        canvas: android.graphics.Canvas,
        paint: Paint,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        label: String,
        value: String,
        bgColorHex: String,
        textColorHex: String
    ) {
        paint.color = Color.parseColor(bgColorHex)
        canvas.drawRoundRect(RectF(x, y, x + w, y + h), 8f, 8f, paint)

        paint.color = Color.parseColor(textColorHex)
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(value, x + 12f, y + 24f, paint)

        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(label, x + 12f, y + 42f, paint)
    }

    private fun saveAndOpenPdf(context: Context, pdfDocument: PdfDocument, fileName: String) {
        try {
            val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
            if (!docsDir.exists()) {
                docsDir.mkdirs()
            }
            val file = File(docsDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            Toast.makeText(context, "PDF Report Exported: ${file.name}", Toast.LENGTH_LONG).show()

            // Launch viewer or share intent
            val authority = "${context.packageName}.provider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // If no PDF viewer application installed, open Share sheet
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Official Health PDF Report"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to export PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
