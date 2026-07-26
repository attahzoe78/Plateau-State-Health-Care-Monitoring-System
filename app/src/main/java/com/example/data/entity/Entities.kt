package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "facilities")
data class FacilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val lga: String, // e.g., "Jos North", "Pankshin", "Shendam", "Barkin Ladi", "Bokkos", "Mangu", "Wase", "Bassa", "Riyom", "Kanke", "Kanam", "Langtang North", "Langtang South", "Jos South", "Jos East", "Mikang", "Qua'an Pan"
    val ward: String,
    val address: String,
    val facilityType: String, // "PHC", "Cottage Hospital", "Tertiary Annex"
    val latitude: Double,
    val longitude: Double,
    val contactPhone: String,
    val totalBeds: Int,
    val availableBeds: Int,
    val activeStaffCount: Int,
    val operationalStatus: String, // "Operational", "Under Repair", "Closed", "Epidemic Alert"
    val emergencyAlertLevel: String, // "Normal", "Watch", "Alert", "Epidemic"
    val notes: String = ""
)

@Entity(tableName = "medical_staff")
data class MedicalStaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val fullName: String,
    val role: String, // "Doctor", "Nurse", "Midwife", "Community Health Worker", "Pharmacist"
    val specialization: String, // e.g., "General Practice", "Maternal Health", "Pediatrics", "Disease Surveillance"
    val phone: String,
    val email: String,
    val dutyStatus: String, // "Active", "On Duty", "Off Duty", "On Leave", "Emergency Call"
    val assignedShift: String // "Morning", "Afternoon", "Night"
)

@Entity(tableName = "drug_inventory")
data class DrugInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val drugName: String,
    val category: String, // "Antimalarial", "Antibiotic", "Analgesic", "Antiviral", "Vaccine", "Antivenom", "IV Fluid"
    val stockQuantity: Int,
    val unit: String, // "tablets", "vials", "ampoules", "bottles", "sachets"
    val reorderLevel: Int,
    val expiryDate: String, // YYYY-MM-DD
    val batchNumber: String,
    val status: String // "In Stock", "Low Stock", "Out of Stock", "Expired"
)

@Entity(tableName = "seasonal_drug_usage")
data class SeasonalDrugUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val illnessName: String, // "Malaria", "Cholera", "Upper Respiratory Infection (URI)", "Meningitis", "Typhoid", "Measles", "Snakebite Envenomation", "Lassa Fever"
    val season: String, // "Dry Season (Harmattan)", "Hot Season", "Rainy Season"
    val primaryDrugs: String, // e.g. "Artemether-Lumefantrine, Paracetamol"
    val recommendedDosage: String,
    val totalUnitsDispensedStatewide: Int,
    val averageMonthlyDemand: Int,
    val priorityLevel: String, // "High", "Critical", "Moderate"
    val riskFactorNotes: String
)

@Entity(tableName = "fumigation_logs")
data class FumigationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val fumigationType: String, // "Organic", "Chemical"
    val agentUsed: String, // e.g., "Neem & Pyrethrum Botanical Extract", "Deltamethrin WP 5%"
    val dateScheduled: String,
    val dateCompleted: String = "",
    val status: String, // "Completed", "Scheduled", "Overdue"
    val supervisorName: String,
    val targetPests: String // "Mosquitoes & Larvae", "Blackflies & Rodents", "General Vector Control"
)

@Entity(tableName = "patient_records")
data class PatientRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: String, // e.g., "PL-PS-HC-00001"
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val fullName: String,
    val gender: String, // "Male", "Female"
    val age: Int,
    val familyHeadName: String,
    val bloodGroup: String,
    val allergies: String,
    val chronicConditions: String,
    val visitDate: String,
    val diagnosis: String,
    val prescribedDrugs: String,
    val season: String
)

@Entity(tableName = "birth_records")
data class BirthRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val deliveryDate: String,
    val babyGender: String, // "Boy", "Girl"
    val birthWeightKg: Double,
    val deliveryType: String, // "Normal", "Caesarean", "Assisted"
    val motherName: String,
    val birthStatus: String // "Healthy", "NICU Care", "Complicated"
)

@Entity(tableName = "outbreak_alerts")
data class OutbreakAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diseaseName: String,
    val lga: String,
    val severityLevel: String, // "Normal", "Watch", "Alert", "Epidemic"
    val reportedCases: Int,
    val weeklyTrendPercentage: Int,
    val activeDate: String,
    val recommendedResponse: String
)

@Entity(tableName = "facility_requirements")
data class FacilityRequirementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val itemName: String,
    val itemType: String, // "Drug" or "Medical Supply"
    val category: String, // "Antimalarial", "Antivenom", "Vaccine", "Antibiotic", "Diagnostic Supply", "IV Fluid", "PPE"
    val monthlyRequirementUnits: Int,
    val currentStockUnits: Int,
    val requiredDeficitUnits: Int,
    val seasonalIllnessTargeted: String,
    val peakSeason: String, // "Rainy Season", "Dry Season (Harmattan)", "Hot Season"
    val priorityLevel: String // "Critical", "High", "Moderate"
)

@Entity(tableName = "medical_supply_inventory")
data class MedicalSupplyInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val supplyName: String,
    val category: String, // "Diagnostic Supply", "Cold Chain Equipment", "Protection & Safety", "Consumable", "Surgical/Delivery"
    val quantityInStock: Int,
    val unitOfMeasure: String, // "kits", "units", "boxes", "packs"
    val reorderThreshold: Int,
    val status: String, // "Sufficient", "Low Stock", "Critical Low"
    val lastInspectionDate: String
)

@Entity(tableName = "seasonal_illness_mapping")
data class SeasonalIllnessMappingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val illnessName: String,
    val season: String, // "Rainy Season", "Dry Season (Harmattan)", "Hot Season"
    val riskLevel: String, // "Critical", "High", "Moderate"
    val primaryDrugsRequired: String,
    val essentialSuppliesRequired: String,
    val recommendedDosage: String,
    val averageMonthlyDemandStatewide: Int,
    val expectedSurgeMultiplier: Double,
    val priorityLgas: String,
    val riskFactorNotes: String
)


@Entity(
    tableName = "facility_drug_requirements",
    foreignKeys = [
        ForeignKey(
            entity = FacilityEntity::class,
            parentColumns = ["id"],
            childColumns = ["facilityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["facilityId"]), Index(value = ["illnessName"]), Index(value = ["season"])]
)
data class FacilityDrugRequirementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val illnessName: String, // e.g., "Malaria", "Cholera", "Meningitis", "Lassa Fever", "Snakebite Envenomation", "Upper Respiratory Infection (URI)"
    val season: String, // "Rainy Season", "Dry Season (Harmattan)", "Hot Season"
    val requiredDrugName: String, // e.g. "Artemether-Lumefantrine 80/480mg", "Polyvalent Snake Antivenom", "Ceftriaxone 1g Injectable", "ORS sachets", "Ribavirin 200mg"
    val category: String, // "Antimalarial", "Antivenom", "Antibiotic", "Vaccine", "IV Fluid", "Antiviral"
    val monthlyRequiredUnits: Int,
    val currentStockUnits: Int,
    val bufferStockRequired: Int,
    val quarterlyQuotaAllocated: Int,
    val priorityLevel: String, // "Critical", "High", "Moderate"
    val lastRestockDate: String = "2026-07-01",
    val nextDeliveryScheduled: String = "2026-08-15"
)

@Entity(
    tableName = "facility_medical_supplies",
    foreignKeys = [
        ForeignKey(
            entity = FacilityEntity::class,
            parentColumns = ["id"],
            childColumns = ["facilityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["facilityId"])]
)
data class FacilityMedicalSupplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val itemType: String, // "Malaria RDT Kits", "Sterile Syringes & Needles", "PPE Isolation Suits", "Cold Chain Vaccine Carrier", "Surgical Gloves", "IV Infusion Sets"
    val category: String, // "Diagnostics", "Consumables", "Equipment", "PPE", "Cold Chain"
    val quantityOnHand: Int,
    val minimumThreshold: Int,
    val unitOfMeasure: String, // "boxes", "kits", "units", "packs", "sets"
    val conditionStatus: String, // "Optimal", "Low Stock", "Critical Depletion"
    val lastAuditDate: String = "2026-07-20"
)

@Entity(
    tableName = "healthcare_personnel",
    foreignKeys = [
        ForeignKey(
            entity = FacilityEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignedFacilityId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["assignedFacilityId"]),
        Index(value = ["cadre"]),
        Index(value = ["specialization"]),
        Index(value = ["licenseNumber"], unique = true)
    ]
)
data class HealthcarePersonnelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val cadre: String, // "Doctor" or "Nurse"
    val licenseNumber: String, // e.g., "MDCN/2025/1042" or "NMCN/2024/9021"
    val specialization: String, // e.g., "Obstetrics & Gynecology", "Pediatric Care", "Epidemiology & Infectious Diseases", "Anesthesia", "Public Health Nursing", "General Practice"
    val qualification: String, // e.g., "MBBS", "FWACS", "B.NSc", "RN/RM", "MPH"
    val phone: String,
    val email: String,
    val assignedFacilityId: Long?,
    val assignedFacilityName: String,
    val lga: String,
    val registrationDate: String = "2026-01-15",
    val assignmentDate: String = "2026-02-01",
    val employmentType: String = "Permanent", // "Permanent", "NYSC Doctor", "Contract Staff", "Visiting Specialist"
    val dutyShift: String = "Morning", // "Morning", "Afternoon", "Night", "On-Call"
    val activeStatus: Boolean = true
)

@Entity(tableName = "inventory_notifications")
data class InventoryNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val drugOrSupplyName: String,
    val category: String, // "Antimalarial", "Antivenom", "Antibiotic", "Vaccine", "IV Fluid", "Diagnostics"
    val currentStockUnits: Int,
    val definedThresholdUnits: Int,
    val unitOfMeasure: String = "units",
    val severity: String, // "CRITICAL_DEFICIT", "LOW_STOCK", "OUT_OF_STOCK"
    val notificationTitle: String,
    val notificationMessage: String,
    val timestamp: String,
    val isAcknowledged: Boolean = false,
    val actionTaken: String? = null // e.g., "Pending Restock", "Emergency Restock Dispatched (+500 units)", "Acknowledged"
)

@Entity(
    tableName = "facility_issue_reports",
    foreignKeys = [
        ForeignKey(
            entity = FacilityEntity::class,
            parentColumns = ["id"],
            childColumns = ["facilityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["facilityId"])]
)
data class FacilityIssueReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val category: String, // "Equipment Failure", "Infrastructure & Building", "Power / Generator Fault", "Cold Chain / Solar Refrigerator", "Water & Sanitation Outage", "Medical Supply Shortage", "Security & Facility Safety"
    val issueTitle: String,
    val description: String,
    val urgencyLevel: String, // "URGENT_CRITICAL", "HIGH", "MEDIUM", "LOW_ROUTINE"
    val reportedByStaffName: String,
    val reportedByRole: String, // "Officer in Charge (OIC)", "Senior Nurse", "Pharmacy Tech", "Lab Technologist", "Community Health Extension Worker"
    val contactPhone: String,
    val departmentOrWard: String = "General PHC Ward",
    val dateReported: String,
    val status: String = "Pending Review", // "Pending Review", "Work Order Issued", "Under Repair", "Resolved"
    val workOrderTicketNumber: String = "",
    val resolutionNotes: String? = null
)

@Entity(
    tableName = "facility_assessments",
    foreignKeys = [
        ForeignKey(
            entity = FacilityEntity::class,
            parentColumns = ["id"],
            childColumns = ["facilityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["facilityId"])]
)
data class FacilityAssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facilityId: Long,
    val facilityName: String,
    val lga: String,
    val inspectorName: String,
    val inspectorBadgeId: String,
    val assessmentDate: String, // e.g. YYYY-MM-DD HH:mm
    val overallScorePct: Int, // 0-100
    val cleanlinessRating: String, // "Excellent", "Good", "Fair", "Poor"
    val coldChainStatus: String, // "Functional Solar Refrigerator", "Battery Backup Low", "Non-Functional", "Grid Power Only"
    val waterSanitationRating: String, // "Borehole Operational", "Water Shortage", "Contaminated Supply", "No Running Water"
    val staffingAdequacy: String, // "Fully Staffed", "Moderate Deficit", "Severe Doctor Shortage", "Critical Shortage"
    val drugStockRating: String, // "Sufficient Antimalarials & Antivenom", "Low Stocks", "Critical Stockout"
    val buildingStructureCondition: String, // "Intact Roof & Walls", "Minor Repairs Needed", "Severe Leaks/Damage"
    val inspectorComments: String,
    val recommendedAction: String, // "Immediate Emergency Support", "Routine Quarterly Restock", "Infrastructure Maintenance Required", "Compliance Approved"
    val syncStatus: String = "BUFFERED_OFFLINE", // "BUFFERED_OFFLINE", "QUEUED_WORK_MANAGER", "SYNCED_TO_MINISTRY"
    val isBufferedOffline: Boolean = true,
    val bufferedAtTimestamp: Long = System.currentTimeMillis()
)



