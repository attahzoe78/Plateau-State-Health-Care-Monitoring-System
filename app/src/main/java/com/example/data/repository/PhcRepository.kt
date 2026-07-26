package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.InitialDataSeed
import com.example.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PhcRepository(private val db: AppDatabase) {

    val allFacilities: Flow<List<FacilityEntity>> = db.facilityDao().getAllFacilities()
    val allMedicalStaff: Flow<List<MedicalStaffEntity>> = db.medicalStaffDao().getAllStaff()
    val allDrugInventory: Flow<List<DrugInventoryEntity>> = db.drugInventoryDao().getAllDrugInventory()
    val criticalDrugs: Flow<List<DrugInventoryEntity>> = db.drugInventoryDao().getCriticalStock()
    val allSeasonalUsages: Flow<List<SeasonalDrugUsageEntity>> = db.seasonalDrugUsageDao().getAllSeasonalUsages()
    val allFumigationLogs: Flow<List<FumigationLogEntity>> = db.fumigationLogDao().getAllFumigationLogs()
    val allPatients: Flow<List<PatientRecordEntity>> = db.patientRecordDao().getAllPatients()
    val allBirths: Flow<List<BirthRecordEntity>> = db.birthRecordDao().getAllBirths()
    val allOutbreakAlerts: Flow<List<OutbreakAlertEntity>> = db.outbreakAlertDao().getAllAlerts()
    val allFacilityRequirements: Flow<List<FacilityDrugRequirementEntity>> = db.facilityDrugRequirementDao().getAllDrugRequirements()
    val allFacilitySupplies: Flow<List<FacilityMedicalSupplyEntity>> = db.facilityMedicalSupplyDao().getAllMedicalSupplies()
    val facilityRequirements: Flow<List<FacilityRequirementEntity>> = db.facilityRequirementDao().getAllFacilityRequirements()
    val medicalSupplyInventory: Flow<List<MedicalSupplyInventoryEntity>> = db.medicalSupplyInventoryDao().getAllMedicalSupplies()
    val seasonalIllnessMappings: Flow<List<SeasonalIllnessMappingEntity>> = db.seasonalIllnessMappingDao().getAllSeasonalIllnessMappings()
    val allHealthcarePersonnel: Flow<List<HealthcarePersonnelEntity>> = db.healthcarePersonnelDao().getAllPersonnel()
    val allInventoryNotifications: Flow<List<InventoryNotificationEntity>> = db.inventoryNotificationDao().getAllNotifications()
    val unacknowledgedNotifications: Flow<List<InventoryNotificationEntity>> = db.inventoryNotificationDao().getUnacknowledgedNotifications()
    val unacknowledgedNotificationCount: Flow<Int> = db.inventoryNotificationDao().getUnacknowledgedCount()
    val allFacilityIssueReports: Flow<List<FacilityIssueReportEntity>> = db.facilityIssueReportDao().getAllIssueReports()
    val allFacilityAssessments: Flow<List<FacilityAssessmentEntity>> = db.facilityAssessmentDao().getAllAssessments()
    val bufferedAssessmentCount: Flow<Int> = db.facilityAssessmentDao().getBufferedCountFlow()

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingFacilities = db.facilityDao().getAllFacilities().first()
        if (existingFacilities.isEmpty()) {
            db.facilityDao().insertAll(InitialDataSeed.getFacilities())
            db.medicalStaffDao().insertAll(InitialDataSeed.getMedicalStaff())
            db.drugInventoryDao().insertAll(InitialDataSeed.getDrugInventory())
            db.seasonalDrugUsageDao().insertAll(InitialDataSeed.getSeasonalDrugUsage())
            db.fumigationLogDao().insertAll(InitialDataSeed.getFumigationLogs())
            db.patientRecordDao().insertAll(InitialDataSeed.getPatientRecords())
            db.birthRecordDao().insertAll(InitialDataSeed.getBirthRecords())
            db.outbreakAlertDao().insertAll(InitialDataSeed.getOutbreakAlerts())
            db.facilityDrugRequirementDao().insertAll(InitialDataSeed.getFacilityDrugRequirements())
            db.facilityMedicalSupplyDao().insertAll(InitialDataSeed.getFacilityMedicalSupplies())
            db.facilityRequirementDao().insertAll(InitialDataSeed.getFacilityRequirements())
            db.medicalSupplyInventoryDao().insertAll(InitialDataSeed.getMedicalSupplyInventory())
            db.seasonalIllnessMappingDao().insertAll(InitialDataSeed.getSeasonalIllnessMappings())
            db.healthcarePersonnelDao().insertAll(InitialDataSeed.getHealthcarePersonnel())
            db.facilityIssueReportDao().insertAll(InitialDataSeed.getFacilityIssueReports())
            db.facilityAssessmentDao().insertAll(InitialDataSeed.getFacilityAssessments())
        } else {
            val existingPersonnel = db.healthcarePersonnelDao().getAllPersonnel().first()
            if (existingPersonnel.isEmpty()) {
                db.healthcarePersonnelDao().insertAll(InitialDataSeed.getHealthcarePersonnel())
            }
            val existingReports = db.facilityIssueReportDao().getAllIssueReports().first()
            if (existingReports.isEmpty()) {
                db.facilityIssueReportDao().insertAll(InitialDataSeed.getFacilityIssueReports())
            }
            val existingAssessments = db.facilityAssessmentDao().getAllAssessments().first()
            if (existingAssessments.isEmpty()) {
                db.facilityAssessmentDao().insertAll(InitialDataSeed.getFacilityAssessments())
            }
        }
    }

    suspend fun addFacilityAssessment(assessment: FacilityAssessmentEntity): Long = withContext(Dispatchers.IO) {
        db.facilityAssessmentDao().insertAssessment(assessment)
    }

    suspend fun syncAllBufferedAssessments() = withContext(Dispatchers.IO) {
        db.facilityAssessmentDao().markAllSynced()
    }

    suspend fun deleteFacilityAssessment(assessment: FacilityAssessmentEntity) = withContext(Dispatchers.IO) {
        db.facilityAssessmentDao().deleteAssessment(assessment)
    }

    suspend fun addFacilityIssueReport(report: FacilityIssueReportEntity): Long = withContext(Dispatchers.IO) {

        db.facilityIssueReportDao().insertReport(report)
    }

    suspend fun updateFacilityIssueReport(report: FacilityIssueReportEntity) = withContext(Dispatchers.IO) {
        db.facilityIssueReportDao().updateReport(report)
    }



    suspend fun addFacility(facility: FacilityEntity): Long = withContext(Dispatchers.IO) {
        db.facilityDao().insertFacility(facility)
    }

    suspend fun updateFacility(facility: FacilityEntity) = withContext(Dispatchers.IO) {
        db.facilityDao().updateFacility(facility)
    }

    suspend fun deleteFacility(facility: FacilityEntity) = withContext(Dispatchers.IO) {
        db.facilityDao().deleteFacility(facility)
    }

    suspend fun addMedicalStaff(staff: MedicalStaffEntity): Long = withContext(Dispatchers.IO) {
        db.medicalStaffDao().insertStaff(staff)
    }

    suspend fun updateMedicalStaff(staff: MedicalStaffEntity) = withContext(Dispatchers.IO) {
        db.medicalStaffDao().updateStaff(staff)
    }

    suspend fun deleteMedicalStaff(staff: MedicalStaffEntity) = withContext(Dispatchers.IO) {
        db.medicalStaffDao().deleteStaff(staff)
    }

    suspend fun addDrugItem(drug: DrugInventoryEntity): Long = withContext(Dispatchers.IO) {
        db.drugInventoryDao().insertDrug(drug)
    }

    suspend fun updateDrugItem(drug: DrugInventoryEntity) = withContext(Dispatchers.IO) {
        db.drugInventoryDao().updateDrug(drug)
    }

    suspend fun deleteDrugItem(drug: DrugInventoryEntity) = withContext(Dispatchers.IO) {
        db.drugInventoryDao().deleteDrug(drug)
    }

    suspend fun addFumigationLog(log: FumigationLogEntity): Long = withContext(Dispatchers.IO) {
        db.fumigationLogDao().insertLog(log)
    }

    suspend fun updateFumigationLog(log: FumigationLogEntity) = withContext(Dispatchers.IO) {
        db.fumigationLogDao().updateLog(log)
    }

    suspend fun addPatientRecord(patient: PatientRecordEntity): Long = withContext(Dispatchers.IO) {
        db.patientRecordDao().insertPatient(patient)
    }

    suspend fun addBirthRecord(birth: BirthRecordEntity): Long = withContext(Dispatchers.IO) {
        db.birthRecordDao().insertBirth(birth)
    }

    suspend fun addDrugRequirement(req: FacilityDrugRequirementEntity): Long = withContext(Dispatchers.IO) {
        db.facilityDrugRequirementDao().insertRequirement(req)
    }

    suspend fun updateDrugRequirement(req: FacilityDrugRequirementEntity) = withContext(Dispatchers.IO) {
        db.facilityDrugRequirementDao().updateRequirement(req)
    }

    suspend fun deleteDrugRequirement(req: FacilityDrugRequirementEntity) = withContext(Dispatchers.IO) {
        db.facilityDrugRequirementDao().deleteRequirement(req)
    }

    suspend fun addMedicalSupply(supply: FacilityMedicalSupplyEntity): Long = withContext(Dispatchers.IO) {
        db.facilityMedicalSupplyDao().insertSupply(supply)
    }

    suspend fun updateMedicalSupply(supply: FacilityMedicalSupplyEntity) = withContext(Dispatchers.IO) {
        db.facilityMedicalSupplyDao().updateSupply(supply)
    }

    suspend fun deleteMedicalSupply(supply: FacilityMedicalSupplyEntity) = withContext(Dispatchers.IO) {
        db.facilityMedicalSupplyDao().deleteSupply(supply)
    }

    suspend fun addHealthcarePersonnel(personnel: HealthcarePersonnelEntity): Long = withContext(Dispatchers.IO) {
        db.healthcarePersonnelDao().insertPersonnel(personnel)
    }

    suspend fun updateHealthcarePersonnel(personnel: HealthcarePersonnelEntity) = withContext(Dispatchers.IO) {
        db.healthcarePersonnelDao().updatePersonnel(personnel)
    }

    suspend fun deleteHealthcarePersonnel(personnel: HealthcarePersonnelEntity) = withContext(Dispatchers.IO) {
        db.healthcarePersonnelDao().deletePersonnel(personnel)
    }

    suspend fun assignPersonnelToFacility(personnelId: Long, facilityId: Long?, facilityName: String, lga: String, assignmentDate: String) = withContext(Dispatchers.IO) {
        db.healthcarePersonnelDao().assignPersonnelToFacility(personnelId, facilityId, facilityName, lga, assignmentDate)
    }

    suspend fun addInventoryNotification(notification: InventoryNotificationEntity): Long = withContext(Dispatchers.IO) {
        db.inventoryNotificationDao().insertNotification(notification)
    }

    suspend fun acknowledgeInventoryNotification(notificationId: Long, actionTaken: String) = withContext(Dispatchers.IO) {
        db.inventoryNotificationDao().acknowledgeNotification(notificationId, actionTaken)
    }

    suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        db.inventoryNotificationDao().clearAllNotifications()
    }
}



