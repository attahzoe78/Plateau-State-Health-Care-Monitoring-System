package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.entity.*

@Database(
    entities = [
        FacilityEntity::class,
        MedicalStaffEntity::class,
        DrugInventoryEntity::class,
        SeasonalDrugUsageEntity::class,
        FumigationLogEntity::class,
        PatientRecordEntity::class,
        BirthRecordEntity::class,
        OutbreakAlertEntity::class,
        FacilityDrugRequirementEntity::class,
        FacilityMedicalSupplyEntity::class,
        FacilityRequirementEntity::class,
        MedicalSupplyInventoryEntity::class,
        SeasonalIllnessMappingEntity::class,
        HealthcarePersonnelEntity::class,
        InventoryNotificationEntity::class,
        FacilityIssueReportEntity::class,
        MapTileCacheEntity::class,
        FacilityAssessmentEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun facilityDao(): FacilityDao
    abstract fun medicalStaffDao(): MedicalStaffDao
    abstract fun drugInventoryDao(): DrugInventoryDao
    abstract fun seasonalDrugUsageDao(): SeasonalDrugUsageDao
    abstract fun fumigationLogDao(): FumigationLogDao
    abstract fun patientRecordDao(): PatientRecordDao
    abstract fun birthRecordDao(): BirthRecordDao
    abstract fun outbreakAlertDao(): OutbreakAlertDao
    abstract fun facilityDrugRequirementDao(): FacilityDrugRequirementDao
    abstract fun facilityMedicalSupplyDao(): FacilityMedicalSupplyDao
    abstract fun facilityRequirementDao(): FacilityRequirementDao
    abstract fun medicalSupplyInventoryDao(): MedicalSupplyInventoryDao
    abstract fun seasonalIllnessMappingDao(): SeasonalIllnessMappingDao
    abstract fun healthcarePersonnelDao(): HealthcarePersonnelDao
    abstract fun inventoryNotificationDao(): InventoryNotificationDao
    abstract fun facilityIssueReportDao(): FacilityIssueReportDao
    abstract fun mapTileCacheDao(): MapTileCacheDao
    abstract fun facilityAssessmentDao(): FacilityAssessmentDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plateau_phc_monitor.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

