package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {
    @Query("SELECT * FROM facilities ORDER BY name ASC")
    fun getAllFacilities(): Flow<List<FacilityEntity>>

    @Query("SELECT * FROM facilities WHERE lga = :lga ORDER BY name ASC")
    fun getFacilitiesByLga(lga: String): Flow<List<FacilityEntity>>

    @Query("SELECT * FROM facilities WHERE id = :id")
    suspend fun getFacilityById(id: Long): FacilityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacility(facility: FacilityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facilities: List<FacilityEntity>)

    @Update
    suspend fun updateFacility(facility: FacilityEntity)

    @Delete
    suspend fun deleteFacility(facility: FacilityEntity)
}

@Dao
interface MedicalStaffDao {
    @Query("SELECT * FROM medical_staff ORDER BY fullName ASC")
    fun getAllStaff(): Flow<List<MedicalStaffEntity>>

    @Query("SELECT * FROM medical_staff WHERE facilityId = :facilityId ORDER BY fullName ASC")
    fun getStaffByFacility(facilityId: Long): Flow<List<MedicalStaffEntity>>

    @Query("SELECT * FROM medical_staff WHERE role = :role ORDER BY fullName ASC")
    fun getStaffByRole(role: String): Flow<List<MedicalStaffEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: MedicalStaffEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(staffList: List<MedicalStaffEntity>)

    @Update
    suspend fun updateStaff(staff: MedicalStaffEntity)

    @Delete
    suspend fun deleteStaff(staff: MedicalStaffEntity)
}

@Dao
interface DrugInventoryDao {
    @Query("SELECT * FROM drug_inventory ORDER BY drugName ASC")
    fun getAllDrugInventory(): Flow<List<DrugInventoryEntity>>

    @Query("SELECT * FROM drug_inventory WHERE facilityId = :facilityId ORDER BY drugName ASC")
    fun getInventoryByFacility(facilityId: Long): Flow<List<DrugInventoryEntity>>

    @Query("SELECT * FROM drug_inventory WHERE status = 'Low Stock' OR status = 'Out of Stock'")
    fun getCriticalStock(): Flow<List<DrugInventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrug(drug: DrugInventoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drugs: List<DrugInventoryEntity>)

    @Update
    suspend fun updateDrug(drug: DrugInventoryEntity)

    @Delete
    suspend fun deleteDrug(drug: DrugInventoryEntity)
}

@Dao
interface SeasonalDrugUsageDao {
    @Query("SELECT * FROM seasonal_drug_usage ORDER BY illnessName ASC")
    fun getAllSeasonalUsages(): Flow<List<SeasonalDrugUsageEntity>>

    @Query("SELECT * FROM seasonal_drug_usage WHERE season = :season ORDER BY totalUnitsDispensedStatewide DESC")
    fun getUsagesBySeason(season: String): Flow<List<SeasonalDrugUsageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: SeasonalDrugUsageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usages: List<SeasonalDrugUsageEntity>)
}

@Dao
interface FumigationLogDao {
    @Query("SELECT * FROM fumigation_logs ORDER BY dateScheduled DESC")
    fun getAllFumigationLogs(): Flow<List<FumigationLogEntity>>

    @Query("SELECT * FROM fumigation_logs WHERE fumigationType = :type ORDER BY dateScheduled DESC")
    fun getFumigationsByType(type: String): Flow<List<FumigationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: FumigationLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<FumigationLogEntity>)

    @Update
    suspend fun updateLog(log: FumigationLogEntity)
}

@Dao
interface PatientRecordDao {
    @Query("SELECT * FROM patient_records ORDER BY id DESC")
    fun getAllPatients(): Flow<List<PatientRecordEntity>>

    @Query("SELECT * FROM patient_records WHERE cardId = :cardId")
    suspend fun getPatientByCardId(cardId: String): PatientRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<PatientRecordEntity>)
}

@Dao
interface BirthRecordDao {
    @Query("SELECT * FROM birth_records ORDER BY id DESC")
    fun getAllBirths(): Flow<List<BirthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBirth(birth: BirthRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(births: List<BirthRecordEntity>)
}

@Dao
interface OutbreakAlertDao {
    @Query("SELECT * FROM outbreak_alerts ORDER BY severityLevel DESC")
    fun getAllAlerts(): Flow<List<OutbreakAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: OutbreakAlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<OutbreakAlertEntity>)
}

@Dao
interface FacilityDrugRequirementDao {
    @Query("SELECT * FROM facility_drug_requirements ORDER BY facilityName ASC, illnessName ASC")
    fun getAllDrugRequirements(): Flow<List<FacilityDrugRequirementEntity>>

    @Query("SELECT * FROM facility_drug_requirements WHERE facilityId = :facilityId ORDER BY illnessName ASC")
    fun getRequirementsForFacility(facilityId: Long): Flow<List<FacilityDrugRequirementEntity>>

    @Query("SELECT * FROM facility_drug_requirements WHERE season = :season ORDER BY facilityName ASC")
    fun getRequirementsBySeason(season: String): Flow<List<FacilityDrugRequirementEntity>>

    @Query("SELECT * FROM facility_drug_requirements WHERE illnessName = :illnessName ORDER BY facilityName ASC")
    fun getRequirementsByIllness(illnessName: String): Flow<List<FacilityDrugRequirementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(requirement: FacilityDrugRequirementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requirements: List<FacilityDrugRequirementEntity>)

    @Update
    suspend fun updateRequirement(requirement: FacilityDrugRequirementEntity)

    @Delete
    suspend fun deleteRequirement(requirement: FacilityDrugRequirementEntity)
}

@Dao
interface FacilityMedicalSupplyDao {
    @Query("SELECT * FROM facility_medical_supplies ORDER BY facilityName ASC, itemType ASC")
    fun getAllMedicalSupplies(): Flow<List<FacilityMedicalSupplyEntity>>

    @Query("SELECT * FROM facility_medical_supplies WHERE facilityId = :facilityId ORDER BY itemType ASC")
    fun getSuppliesForFacility(facilityId: Long): Flow<List<FacilityMedicalSupplyEntity>>

    @Query("SELECT * FROM facility_medical_supplies WHERE conditionStatus = 'Low Stock' OR conditionStatus = 'Critical Depletion'")
    fun getCriticalSupplies(): Flow<List<FacilityMedicalSupplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupply(supply: FacilityMedicalSupplyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(supplies: List<FacilityMedicalSupplyEntity>)

    @Update
    suspend fun updateSupply(supply: FacilityMedicalSupplyEntity)

    @Delete
    suspend fun deleteSupply(supply: FacilityMedicalSupplyEntity)
}

@Dao
interface FacilityRequirementDao {
    @Query("SELECT * FROM facility_requirements ORDER BY priorityLevel DESC")
    fun getAllFacilityRequirements(): Flow<List<FacilityRequirementEntity>>

    @Query("SELECT * FROM facility_requirements WHERE facilityId = :facilityId")
    fun getRequirementsByFacility(facilityId: Long): Flow<List<FacilityRequirementEntity>>

    @Query("SELECT * FROM facility_requirements WHERE seasonalIllnessTargeted = :illness")
    fun getRequirementsByIllness(illness: String): Flow<List<FacilityRequirementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(req: FacilityRequirementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reqs: List<FacilityRequirementEntity>)
}

@Dao
interface MedicalSupplyInventoryDao {
    @Query("SELECT * FROM medical_supply_inventory ORDER BY supplyName ASC")
    fun getAllMedicalSupplies(): Flow<List<MedicalSupplyInventoryEntity>>

    @Query("SELECT * FROM medical_supply_inventory WHERE facilityId = :facilityId")
    fun getSuppliesByFacility(facilityId: Long): Flow<List<MedicalSupplyInventoryEntity>>

    @Query("SELECT * FROM medical_supply_inventory WHERE status = 'Critical Low' OR status = 'Low Stock'")
    fun getLowStockSupplies(): Flow<List<MedicalSupplyInventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupply(supply: MedicalSupplyInventoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(supplies: List<MedicalSupplyInventoryEntity>)
}

@Dao
interface SeasonalIllnessMappingDao {
    @Query("SELECT * FROM seasonal_illness_mapping ORDER BY illnessName ASC")
    fun getAllSeasonalIllnessMappings(): Flow<List<SeasonalIllnessMappingEntity>>

    @Query("SELECT * FROM seasonal_illness_mapping WHERE season = :season")
    fun getMappingsBySeason(season: String): Flow<List<SeasonalIllnessMappingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: SeasonalIllnessMappingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mappings: List<SeasonalIllnessMappingEntity>)
}

@Dao
interface HealthcarePersonnelDao {
    @Query("SELECT * FROM healthcare_personnel ORDER BY fullName ASC")
    fun getAllPersonnel(): Flow<List<HealthcarePersonnelEntity>>

    @Query("SELECT * FROM healthcare_personnel WHERE assignedFacilityId = :facilityId ORDER BY cadre ASC, fullName ASC")
    fun getPersonnelByFacility(facilityId: Long): Flow<List<HealthcarePersonnelEntity>>

    @Query("SELECT * FROM healthcare_personnel WHERE cadre = :cadre ORDER BY fullName ASC")
    fun getPersonnelByCadre(cadre: String): Flow<List<HealthcarePersonnelEntity>>

    @Query("SELECT * FROM healthcare_personnel WHERE specialization = :specialization ORDER BY fullName ASC")
    fun getPersonnelBySpecialization(specialization: String): Flow<List<HealthcarePersonnelEntity>>

    @Query("SELECT * FROM healthcare_personnel WHERE assignedFacilityId = :facilityId AND cadre = :cadre ORDER BY fullName ASC")
    fun getPersonnelByFacilityAndCadre(facilityId: Long, cadre: String): Flow<List<HealthcarePersonnelEntity>>

    @Query("SELECT * FROM healthcare_personnel WHERE licenseNumber = :licenseNumber")
    suspend fun getPersonnelByLicense(licenseNumber: String): HealthcarePersonnelEntity?

    @Query("UPDATE healthcare_personnel SET assignedFacilityId = :facilityId, assignedFacilityName = :facilityName, lga = :lga, assignmentDate = :assignmentDate WHERE id = :personnelId")
    suspend fun assignPersonnelToFacility(personnelId: Long, facilityId: Long?, facilityName: String, lga: String, assignmentDate: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonnel(personnel: HealthcarePersonnelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(personnelList: List<HealthcarePersonnelEntity>)

    @Update
    suspend fun updatePersonnel(personnel: HealthcarePersonnelEntity)

    @Delete
    suspend fun deletePersonnel(personnel: HealthcarePersonnelEntity)
}

@Dao
interface InventoryNotificationDao {
    @Query("SELECT * FROM inventory_notifications ORDER BY isAcknowledged ASC, id DESC")
    fun getAllNotifications(): Flow<List<InventoryNotificationEntity>>

    @Query("SELECT * FROM inventory_notifications WHERE isAcknowledged = 0 ORDER BY id DESC")
    fun getUnacknowledgedNotifications(): Flow<List<InventoryNotificationEntity>>

    @Query("SELECT COUNT(*) FROM inventory_notifications WHERE isAcknowledged = 0")
    fun getUnacknowledgedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: InventoryNotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<InventoryNotificationEntity>)

    @Update
    suspend fun updateNotification(notification: InventoryNotificationEntity)

    @Query("UPDATE inventory_notifications SET isAcknowledged = 1, actionTaken = :actionTaken WHERE id = :id")
    suspend fun acknowledgeNotification(id: Long, actionTaken: String)

    @Query("DELETE FROM inventory_notifications")
    suspend fun clearAllNotifications()
}

@Dao
interface FacilityIssueReportDao {
    @Query("SELECT * FROM facility_issue_reports ORDER BY id DESC")
    fun getAllIssueReports(): Flow<List<FacilityIssueReportEntity>>

    @Query("SELECT * FROM facility_issue_reports WHERE facilityId = :facilityId ORDER BY id DESC")
    fun getReportsByFacility(facilityId: Long): Flow<List<FacilityIssueReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: FacilityIssueReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<FacilityIssueReportEntity>)

    @Update
    suspend fun updateReport(report: FacilityIssueReportEntity)

    @Delete
    suspend fun deleteReport(report: FacilityIssueReportEntity)
}

@Dao
interface FacilityAssessmentDao {
    @Query("SELECT * FROM facility_assessments ORDER BY id DESC")
    fun getAllAssessments(): Flow<List<FacilityAssessmentEntity>>

    @Query("SELECT * FROM facility_assessments WHERE facilityId = :facilityId ORDER BY id DESC")
    fun getAssessmentsByFacility(facilityId: Long): Flow<List<FacilityAssessmentEntity>>

    @Query("SELECT * FROM facility_assessments WHERE isBufferedOffline = 1 ORDER BY id DESC")
    fun getBufferedOfflineAssessments(): Flow<List<FacilityAssessmentEntity>>

    @Query("SELECT COUNT(*) FROM facility_assessments WHERE isBufferedOffline = 1")
    fun getBufferedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM facility_assessments WHERE isBufferedOffline = 1")
    suspend fun getBufferedCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: FacilityAssessmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assessments: List<FacilityAssessmentEntity>)

    @Query("UPDATE facility_assessments SET isBufferedOffline = 0, syncStatus = 'SYNCED_TO_MINISTRY' WHERE isBufferedOffline = 1")
    suspend fun markAllSynced()

    @Delete
    suspend fun deleteAssessment(assessment: FacilityAssessmentEntity)
}





