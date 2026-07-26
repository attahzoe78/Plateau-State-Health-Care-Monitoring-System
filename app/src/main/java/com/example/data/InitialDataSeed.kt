package com.example.data

import com.example.data.entity.*

object InitialDataSeed {

    fun getFacilities(): List<FacilityEntity> = listOf(
        FacilityEntity(
            id = 1,
            name = "Jos University Teaching Hospital PHC Annex",
            lga = "Jos North",
            ward = "Jishe",
            address = "Murtala Mohammed Way, Jos North",
            facilityType = "Cottage Hospital",
            latitude = 9.9285,
            longitude = 8.8921,
            contactPhone = "+234 803 123 4567",
            totalBeds = 60,
            availableBeds = 24,
            activeStaffCount = 28,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "Central referral center for primary care in Jos metropolis."
        ),
        FacilityEntity(
            id = 2,
            name = "Tudun Wada PHC",
            lga = "Jos North",
            ward = "Tudun Wada",
            address = "Ring Road, Tudun Wada, Jos",
            facilityType = "PHC",
            latitude = 9.9150,
            longitude = 8.8800,
            contactPhone = "+234 802 987 6543",
            totalBeds = 25,
            availableBeds = 8,
            activeStaffCount = 14,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "High volume immunization and maternal health clinic."
        ),
        FacilityEntity(
            id = 3,
            name = "Laranto PHC",
            lga = "Jos North",
            ward = "Laranto",
            address = "Katako Market Road, Laranto, Jos",
            facilityType = "PHC",
            latitude = 9.9400,
            longitude = 8.8720,
            contactPhone = "+234 805 444 3322",
            totalBeds = 20,
            availableBeds = 5,
            activeStaffCount = 12,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "Focus on vector control and cholera watch during rainy season."
        ),
        FacilityEntity(
            id = 4,
            name = "Bukuru Cottage Hospital",
            lga = "Jos South",
            ward = "Bukuru",
            address = "Old Airport Road, Bukuru, Jos South",
            facilityType = "Cottage Hospital",
            latitude = 9.7900,
            longitude = 8.8600,
            contactPhone = "+234 803 999 1122",
            totalBeds = 45,
            availableBeds = 18,
            activeStaffCount = 22,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "Secondary cottage hub serving Jos South and Rayfield corridor."
        ),
        FacilityEntity(
            id = 5,
            name = "Kuru PHC",
            lga = "Jos South",
            ward = "Kuru",
            address = "Science School Junction, Kuru",
            facilityType = "PHC",
            latitude = 9.7200,
            longitude = 8.8400,
            contactPhone = "+234 806 777 8899",
            totalBeds = 18,
            availableBeds = 10,
            activeStaffCount = 10,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Active community outreach for tuberculosis and malaria."
        ),
        FacilityEntity(
            id = 6,
            name = "Pankshin Cottage Hospital",
            lga = "Pankshin",
            ward = "Pankshin Central",
            address = "Hospital Road, Pankshin",
            facilityType = "Cottage Hospital",
            latitude = 9.3300,
            longitude = 9.4400,
            contactPhone = "+234 807 555 1234",
            totalBeds = 50,
            availableBeds = 15,
            activeStaffCount = 25,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "Central healthcare hospital for Central Senatorial District."
        ),
        FacilityEntity(
            id = 7,
            name = "Amper PHC",
            lga = "Pankshin",
            ward = "Amper",
            address = "Main Market Way, Amper",
            facilityType = "PHC",
            latitude = 9.3500,
            longitude = 9.5800,
            contactPhone = "+234 808 333 9900",
            totalBeds = 15,
            availableBeds = 6,
            activeStaffCount = 8,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Seasonal respiratory infection tracking center."
        ),
        FacilityEntity(
            id = 8,
            name = "Shendam Cottage Hospital",
            lga = "Shendam",
            ward = "Shendam",
            address = "Yelwa Road, Shendam",
            facilityType = "Cottage Hospital",
            latitude = 8.8800,
            longitude = 9.5000,
            contactPhone = "+234 802 111 2233",
            totalBeds = 55,
            availableBeds = 20,
            activeStaffCount = 26,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "Lassa fever surveillance & snakebite antivenom depot."
        ),
        FacilityEntity(
            id = 9,
            name = "Dokan Tofa PHC",
            lga = "Shendam",
            ward = "Dokan Tofa",
            address = "Shendam-Lafia Express Road",
            facilityType = "PHC",
            latitude = 8.8100,
            longitude = 9.4200,
            contactPhone = "+234 803 222 6677",
            totalBeds = 16,
            availableBeds = 9,
            activeStaffCount = 9,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Watch",
            notes = "Agricultural region with high seasonal snakebite interventions."
        ),
        FacilityEntity(
            id = 10,
            name = "Barkin Ladi General PHC",
            lga = "Barkin Ladi",
            ward = "Ganawuri",
            address = "Barkin Ladi Express Way",
            facilityType = "Cottage Hospital",
            latitude = 9.5300,
            longitude = 8.8900,
            contactPhone = "+234 805 888 1122",
            totalBeds = 40,
            availableBeds = 12,
            activeStaffCount = 18,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Alert",
            notes = "Organic vector control pilot facility."
        ),
        FacilityEntity(
            id = 11,
            name = "Foron PHC",
            lga = "Barkin Ladi",
            ward = "Foron",
            address = "Foron District Head Palace Road",
            facilityType = "PHC",
            latitude = 9.5800,
            longitude = 8.9500,
            contactPhone = "+234 806 123 9876",
            totalBeds = 14,
            availableBeds = 5,
            activeStaffCount = 7,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Equipped with cold chain solar refrigerator for vaccines."
        ),
        FacilityEntity(
            id = 12,
            name = "Bokkos PHC",
            lga = "Bokkos",
            ward = "Bokkos Central",
            address = "College of Education Road, Bokkos",
            facilityType = "PHC",
            latitude = 9.3000,
            longitude = 8.9900,
            contactPhone = "+234 809 333 4455",
            totalBeds = 22,
            availableBeds = 11,
            activeStaffCount = 11,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "High elevation Harmattan respiratory infection monitoring."
        ),
        FacilityEntity(
            id = 13,
            name = "Manguna Cottage Hospital",
            lga = "Bokkos",
            ward = "Manguna",
            address = "Manguna-Daffo Road",
            facilityType = "Cottage Hospital",
            latitude = 9.2500,
            longitude = 8.9200,
            contactPhone = "+234 803 777 1144",
            totalBeds = 30,
            availableBeds = 14,
            activeStaffCount = 15,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Provides maternal delivery and child nutrition services."
        ),
        FacilityEntity(
            id = 14,
            name = "Mangu General Cottage Hospital",
            lga = "Mangu",
            ward = "Mangu Central",
            address = "Gindiri Express Road, Mangu",
            facilityType = "Cottage Hospital",
            latitude = 9.5200,
            longitude = 9.1000,
            contactPhone = "+234 802 888 3311",
            totalBeds = 48,
            availableBeds = 19,
            activeStaffCount = 21,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "Main healthcare facility serving agricultural communities in Mangu."
        ),
        FacilityEntity(
            id = 15,
            name = "Panyam PHC",
            lga = "Mangu",
            ward = "Panyam",
            address = "Fish Farm Road, Panyam",
            facilityType = "PHC",
            latitude = 9.4300,
            longitude = 9.1300,
            contactPhone = "+234 806 222 1100",
            totalBeds = 18,
            availableBeds = 8,
            activeStaffCount = 9,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Epidemic",
            notes = "Specializes in waterborne disease prevention during rainy season."
        ),
        FacilityEntity(
            id = 16,
            name = "Wase Cottage Hospital",
            lga = "Wase",
            ward = "Wase Central",
            address = "Rock View Road, Wase",
            facilityType = "Cottage Hospital",
            latitude = 9.0900,
            longitude = 9.9600,
            contactPhone = "+234 807 111 8899",
            totalBeds = 35,
            availableBeds = 15,
            activeStaffCount = 17,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Alert",
            notes = "Covers eastern border area with high meningitis surveillance."
        ),
        FacilityEntity(
            id = 17,
            name = "Basham PHC",
            lga = "Wase",
            ward = "Basham",
            address = "Basham Village Square",
            facilityType = "PHC",
            latitude = 9.1500,
            longitude = 10.0200,
            contactPhone = "+234 808 666 5544",
            totalBeds = 12,
            availableBeds = 4,
            activeStaffCount = 6,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Provides essential mobile clinic outreach."
        ),
        FacilityEntity(
            id = 18,
            name = "Bassa Cottage Hospital",
            lga = "Bassa",
            ward = "Bassa Central",
            address = "Rukuba Barracks Road, Bassa",
            facilityType = "Cottage Hospital",
            latitude = 9.9300,
            longitude = 8.7300,
            contactPhone = "+234 803 555 9911",
            totalBeds = 32,
            availableBeds = 12,
            activeStaffCount = 16,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Community health & epidemic response center."
        ),
        FacilityEntity(
            id = 19,
            name = "Miango PHC",
            lga = "Bassa",
            ward = "Miango",
            address = "Miango Rest Home Way",
            facilityType = "PHC",
            latitude = 9.8500,
            longitude = 8.6800,
            contactPhone = "+234 802 444 8877",
            totalBeds = 16,
            availableBeds = 7,
            activeStaffCount = 8,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Cool climate health post with dry season flu watch."
        ),
        FacilityEntity(
            id = 20,
            name = "Riyom Cottage Hospital",
            lga = "Riyom",
            ward = "Riyom Central",
            address = "Jos-Akwanga Highway, Riyom",
            facilityType = "Cottage Hospital",
            latitude = 9.6400,
            longitude = 8.7600,
            contactPhone = "+234 805 111 4433",
            totalBeds = 28,
            availableBeds = 10,
            activeStaffCount = 15,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Watch",
            notes = "Emergency trauma and primary care post on trunk A highway."
        ),
        FacilityEntity(
            id = 21,
            name = "Baching PHC",
            lga = "Riyom",
            ward = "Baching",
            address = "Baching Village Centre",
            facilityType = "PHC",
            latitude = 9.6000,
            longitude = 8.7100,
            contactPhone = "+234 806 333 2211",
            totalBeds = 12,
            availableBeds = 5,
            activeStaffCount = 6,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Rural maternal health post."
        ),
        FacilityEntity(
            id = 22,
            name = "Kanke PHC",
            lga = "Kanke",
            ward = "Kanke",
            address = "Kwal Road, Kanke",
            facilityType = "PHC",
            latitude = 9.4000,
            longitude = 9.7500,
            contactPhone = "+234 807 999 8811",
            totalBeds = 15,
            availableBeds = 6,
            activeStaffCount = 7,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Immunization and nutritional supplement hub."
        ),
        FacilityEntity(
            id = 23,
            name = "Kanam Cottage Hospital",
            lga = "Kanam",
            ward = "Dengi",
            address = "Emir Palace Road, Dengi, Kanam",
            facilityType = "Cottage Hospital",
            latitude = 9.4500,
            longitude = 10.0500,
            contactPhone = "+234 808 222 3344",
            totalBeds = 30,
            availableBeds = 11,
            activeStaffCount = 14,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Alert",
            notes = "Active Lassa fever & typhoid screening ward."
        ),
        FacilityEntity(
            id = 24,
            name = "Langtang General Cottage Hospital",
            lga = "Langtang North",
            ward = "Langtang Central",
            address = "Shendam Road, Langtang",
            facilityType = "Cottage Hospital",
            latitude = 8.6300,
            longitude = 9.7900,
            contactPhone = "+234 803 888 7766",
            totalBeds = 42,
            availableBeds = 16,
            activeStaffCount = 20,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Key hospital serving Langtang North & South axis."
        ),
        FacilityEntity(
            id = 25,
            name = "Mabudi PHC",
            lga = "Langtang South",
            ward = "Mabudi",
            address = "Main Street, Mabudi",
            facilityType = "PHC",
            latitude = 8.4500,
            longitude = 9.8500,
            contactPhone = "+234 802 666 1122",
            totalBeds = 14,
            availableBeds = 7,
            activeStaffCount = 7,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Southernmost primary health facility in Plateau State."
        ),
        FacilityEntity(
            id = 26,
            name = "Fobur PHC",
            lga = "Jos East",
            ward = "Fobur",
            address = "Fobur District Road",
            facilityType = "PHC",
            latitude = 9.8800,
            longitude = 9.1000,
            contactPhone = "+234 805 777 6655",
            totalBeds = 15,
            availableBeds = 8,
            activeStaffCount = 6,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Serves mountainous rural hamlets of Jos East."
        ),
        FacilityEntity(
            id = 27,
            name = "Tunkus Cottage Hospital",
            lga = "Mikang",
            ward = "Tunkus",
            address = "Tunkus Junction, Mikang",
            facilityType = "Cottage Hospital",
            latitude = 8.7800,
            longitude = 9.6800,
            contactPhone = "+234 806 888 4433",
            totalBeds = 25,
            availableBeds = 10,
            activeStaffCount = 12,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Provides maternal delivery & child welfare services."
        ),
        FacilityEntity(
            id = 28,
            name = "Doemak PHC",
            lga = "Qua'an Pan",
            ward = "Doemak",
            address = "Doemak Central Square",
            facilityType = "PHC",
            latitude = 8.6500,
            longitude = 9.2000,
            contactPhone = "+234 807 444 2211",
            totalBeds = 16,
            availableBeds = 6,
            activeStaffCount = 8,
            operationalStatus = "Operational",
            emergencyAlertLevel = "Normal",
            notes = "Community primary care and family health center."
        )
    )

    fun getMedicalStaff(): List<MedicalStaffEntity> {
        val roles = listOf("Doctor", "Nurse", "Midwife", "Community Health Worker", "Pharmacist")
        val specs = listOf("General Practice", "Maternal Health", "Pediatrics", "Disease Surveillance", "Health Promotion")
        val shifts = listOf("Morning", "Afternoon", "Night")
        val names = listOf(
            "Dr. Nyam Gyang", "Dr. Dachollom Bot", "Dr. Danjuma Gontor", "Dr. Nanribet Longjan",
            "Dr. Bala Dachomo", "Dr. Mary Dachomo", "Dr. Daniel Dimka", "Dr. Yakubu Pam",
            "Dr. Rotgat Lar", "Dr. Joseph Chollom", "Dr. Grace Vongtau", "Dr. Solomon Kwande",
            "Nurse Laraba Pam", "Nurse Maryam Agboola", "Nurse Esther Choji", "Nurse Stella Adefila",
            "Nurse Blessing Dusu", "Nurse Rahila Tok", "Nurse Jennifer Dung", "Nurse Mercy Kim",
            "Nurse Patience Jang", "Nurse Na'omi Rwang", "Midwife Ladi Bako", "Midwife Jummai Plang",
            "Midwife Sarah Zang", "Community Worker Bitrus Mwadkwon", "Community Worker Moses Gyang",
            "Community Worker Tabitha Bot", "Pharmacist Usman Sambo", "Pharmacist Kenneth Dimka"
        )

        val list = mutableListOf<MedicalStaffEntity>()
        var idCounter = 1L
        val facilities = getFacilities()

        facilities.forEach { fac ->
            // Assign 3-4 staff per facility
            val staffForFac = listOf(
                MedicalStaffEntity(
                    id = idCounter++,
                    facilityId = fac.id,
                    facilityName = fac.name,
                    lga = fac.lga,
                    fullName = "Dr. ${getDoctorName(fac.id)}",
                    role = "Doctor",
                    specialization = "General Practice & Primary Care",
                    phone = "+234 ${8030000000L + fac.id * 1111}",
                    email = "doctor.fac${fac.id}@plateauhealth.gov.ng",
                    dutyStatus = "Active",
                    assignedShift = "Morning"
                ),
                MedicalStaffEntity(
                    id = idCounter++,
                    facilityId = fac.id,
                    facilityName = fac.name,
                    lga = fac.lga,
                    fullName = "Nurse ${getNurseName(fac.id)}",
                    role = "Nurse",
                    specialization = "Maternal & Child Health",
                    phone = "+234 ${8020000000L + fac.id * 2222}",
                    email = "nurse.fac${fac.id}@plateauhealth.gov.ng",
                    dutyStatus = "Active",
                    assignedShift = "Morning"
                ),
                MedicalStaffEntity(
                    id = idCounter++,
                    facilityId = fac.id,
                    facilityName = fac.name,
                    lga = fac.lga,
                    fullName = "Midwife ${getMidwifeName(fac.id)}",
                    role = "Midwife",
                    specialization = "Antenatal & Delivery",
                    phone = "+234 ${8050000000L + fac.id * 3333}",
                    email = "midwife.fac${fac.id}@plateauhealth.gov.ng",
                    dutyStatus = "Active",
                    assignedShift = "Afternoon"
                )
            )
            list.addAll(staffForFac)
        }
        return list
    }

    private fun getDoctorName(id: Long): String {
        val docNames = listOf("Nyam Gyang", "Dachollom Bot", "Danjuma Gontor", "Nanribet Longjan", "Bala Dachomo", "Mary Dachomo", "Daniel Dimka", "Yakubu Pam", "Rotgat Lar", "Joseph Chollom")
        return docNames[(id % docNames.size).toInt()]
    }

    private fun getNurseName(id: Long): String {
        val nurseNames = listOf("Laraba Pam", "Maryam Agboola", "Esther Choji", "Stella Adefila", "Blessing Dusu", "Rahila Tok", "Jennifer Dung", "Mercy Kim", "Patience Jang", "Na'omi Rwang")
        return nurseNames[(id % nurseNames.size).toInt()]
    }

    private fun getMidwifeName(id: Long): String {
        val midwifeNames = listOf("Ladi Bako", "Jummai Plang", "Sarah Zang", "Hanatu Adefila", "Aisha Bot", "Grace Bot", "David Bot", "Yusuf Dakwak", "Daniel Agboola", "Musa Agboola")
        return midwifeNames[(id % midwifeNames.size).toInt()]
    }

    fun getDrugInventory(): List<DrugInventoryEntity> {
        val drugs = listOf(
            Triple("Artemether-Lumefantrine 80/480mg", "Antimalarial", "tablets"),
            Triple("Injectable Artesunate 60mg", "Antimalarial", "vials"),
            Triple("Amoxicillin 500mg", "Antibiotic", "capsules"),
            Triple("Ciprofloxacin 500mg", "Antibiotic", "tablets"),
            Triple("ORS (Oral Rehydration Salts)", "IV Fluid", "sachets"),
            Triple("Zinc Sulfate 20mg", "Analgesic", "tablets"),
            Triple("Paracetamol Syrup 125mg/5ml", "Analgesic", "bottles"),
            Triple("Polyvalent Snake Antivenom", "Antivenom", "vials"),
            Triple("Ribavirin 200mg (Lassa)", "Antiviral", "tablets"),
            Triple("Ceftriaxone 1g Injectable", "Antibiotic", "vials"),
            Triple("Measles Vaccine (10 doses)", "Vaccine", "vials"),
            Triple("Pentavalent Vaccine", "Vaccine", "vials"),
            Triple("Acyclovir 400mg", "Antiviral", "tablets"),
            Triple("Metronidazole 400mg", "Antibiotic", "tablets"),
            Triple("Normal Saline 0.9% 500ml", "IV Fluid", "bottles"),
            Triple("Ibuprofen 400mg", "Analgesic", "tablets"),
            Triple("Chlorpheniramine 4mg (Cold/Flu)", "Analgesic", "tablets")
        )

        val list = mutableListOf<DrugInventoryEntity>()
        var idCounter = 1L
        val facilities = getFacilities()

        facilities.forEach { fac ->
            drugs.forEachIndexed { idx, (dName, cat, unit) ->
                val qty = (200..2500).random()
                val status = if (qty < 300) "Low Stock" else "In Stock"
                list.add(
                    DrugInventoryEntity(
                        id = idCounter++,
                        facilityId = fac.id,
                        facilityName = fac.name,
                        lga = fac.lga,
                        drugName = dName,
                        category = cat,
                        stockQuantity = qty,
                        unit = unit,
                        reorderLevel = 300,
                        expiryDate = "2027-${(1..12).random().toString().padStart(2, '0')}-15",
                        batchNumber = "BN${(100000..999999).random()}",
                        status = status
                    )
                )
            }
        }
        return list
    }

    fun getSeasonalDrugUsage(): List<SeasonalDrugUsageEntity> = listOf(
        // Harmattan / Dry Season (Nov - Feb)
        SeasonalDrugUsageEntity(
            id = 1,
            illnessName = "Upper Respiratory Infection (URI) & Bronchitis",
            season = "Dry Season (Harmattan)",
            primaryDrugs = "Amoxicillin, Chlorpheniramine, Paracetamol Syrup, Salbutamol Inhaler",
            recommendedDosage = "Amoxicillin 500mg TDS for 5 days, Chlorpheniramine 4mg BD",
            totalUnitsDispensedStatewide = 48500,
            averageMonthlyDemand = 12125,
            priorityLevel = "High",
            riskFactorNotes = "Severe Harmattan dust causes dust-induced respiratory flare-ups across Pankshin, Bokkos, Jos North."
        ),
        SeasonalDrugUsageEntity(
            id = 2,
            illnessName = "Meningitis (Neisseria meningitidis)",
            season = "Dry Season (Harmattan)",
            primaryDrugs = "Ceftriaxone 1g Injectable, Oily Chloramphenicol, Meningococcal ACWY Vaccine",
            recommendedDosage = "Ceftriaxone 2g IV daily for 7 days or single dose oily Chloramphenicol",
            totalUnitsDispensedStatewide = 18200,
            averageMonthlyDemand = 4550,
            priorityLevel = "Critical",
            riskFactorNotes = "High risk in dry heat conditions across Wase, Kanam, Kanke, and Shendam LGAs."
        ),
        SeasonalDrugUsageEntity(
            id = 3,
            illnessName = "Measles (Rubeola)",
            season = "Dry Season (Harmattan)",
            primaryDrugs = "Measles Vaccine, Vitamin A 200,000 IU Capsules, Paracetamol",
            recommendedDosage = "Vitamin A single capsule on Day 1 & Day 2",
            totalUnitsDispensedStatewide = 22400,
            averageMonthlyDemand = 5600,
            priorityLevel = "High",
            riskFactorNotes = "Dry weather accelerates airborne viral spread among unimmunized under-5 children."
        ),
        SeasonalDrugUsageEntity(
            id = 4,
            illnessName = "Eye Infections (Conjunctivitis / Harmattan Blindness)",
            season = "Dry Season (Harmattan)",
            primaryDrugs = "Chloramphenicol Eye Drops, Tetracycline Eye Ointment",
            recommendedDosage = "Chloramphenicol 1 drop QDS for 5 days",
            totalUnitsDispensedStatewide = 15300,
            averageMonthlyDemand = 3825,
            priorityLevel = "Moderate",
            riskFactorNotes = "Irritation caused by fine silica particles carried by Sahara dust winds."
        ),

        // Hot Season (Mar - May)
        SeasonalDrugUsageEntity(
            id = 5,
            illnessName = "Lassa Fever (Viral Hemorrhagic Fever)",
            season = "Hot Season",
            primaryDrugs = "Ribavirin 200mg, IV Fluids (Normal Saline), Paracetamol",
            recommendedDosage = "Ribavirin IV loading dose 30mg/kg followed by 16mg/kg 6-hourly",
            totalUnitsDispensedStatewide = 12800,
            averageMonthlyDemand = 4260,
            priorityLevel = "Critical",
            riskFactorNotes = "Mastomys rodent activity spikes around human habitations in heat season in Shendam & Langtang."
        ),
        SeasonalDrugUsageEntity(
            id = 6,
            illnessName = "Typhoid Fever (Salmonella enterica)",
            season = "Hot Season",
            primaryDrugs = "Ciprofloxacin 500mg, Azithromycin 500mg, Paracetamol",
            recommendedDosage = "Ciprofloxacin 500mg BD for 7-10 days",
            totalUnitsDispensedStatewide = 31000,
            averageMonthlyDemand = 10333,
            priorityLevel = "High",
            riskFactorNotes = "Water scarcity during dry heat leads to unsafe drinking water usage in rural wards."
        ),
        SeasonalDrugUsageEntity(
            id = 7,
            illnessName = "Gastroenteritis & Heat Dehydration",
            season = "Hot Season",
            primaryDrugs = "Oral Rehydration Salts (ORS), Zinc Sulfate 20mg, Metronidazole",
            recommendedDosage = "1 ORS sachet dissolved in 1L clean water + Zinc 20mg daily for 10 days",
            totalUnitsDispensedStatewide = 39500,
            averageMonthlyDemand = 13166,
            priorityLevel = "High",
            riskFactorNotes = "Prevalent among infants and field workers during extreme daytime heat."
        ),

        // Rainy Season (Jun - Oct)
        SeasonalDrugUsageEntity(
            id = 8,
            illnessName = "Malaria (Plasmodium falciparum)",
            season = "Rainy Season",
            primaryDrugs = "Artemether-Lumefantrine 80/480mg, Injectable Artesunate, Paracetamol",
            recommendedDosage = "AL 1 tablet BD for 3 days; Severe: Artesunate IV 2.4mg/kg at 0, 12, 24 hrs",
            totalUnitsDispensedStatewide = 142000,
            averageMonthlyDemand = 28400,
            priorityLevel = "Critical",
            riskFactorNotes = "Peak transmission season statewide due to standing rainwater breeding Anopheles mosquitoes."
        ),
        SeasonalDrugUsageEntity(
            id = 9,
            illnessName = "Cholera & Acute Watery Diarrhea",
            season = "Rainy Season",
            primaryDrugs = "ORS, Doxycycline 100mg, Ringer's Lactate IV Infusion, Zinc Sulfate",
            recommendedDosage = "Doxycycline 300mg single dose + rapid IV rehydration",
            totalUnitsDispensedStatewide = 28600,
            averageMonthlyDemand = 5720,
            priorityLevel = "Critical",
            riskFactorNotes = "Flooding contaminates shallow wells in Laranto, Katako, and agricultural river basins."
        ),
        SeasonalDrugUsageEntity(
            id = 10,
            illnessName = "Snakebite Envenomation (Echis ocellatus / Carpet Viper)",
            season = "Rainy Season",
            primaryDrugs = "Polyvalent Snake Antivenom (EchiTox / Anti-Vip), Tetanus Toxoid, IV Fluids",
            recommendedDosage = "1-2 vials Polyvalent Antivenom IV diluted in 250ml Normal Saline over 1 hr",
            totalUnitsDispensedStatewide = 9400,
            averageMonthlyDemand = 1880,
            priorityLevel = "Critical",
            riskFactorNotes = "Farming season in Shendam, Mikang, Langtang, Qua'an Pan increases human-viper encounters."
        ),
        SeasonalDrugUsageEntity(
            id = 11,
            illnessName = "River Blindness (Onchocerciasis)",
            season = "Rainy Season",
            primaryDrugs = "Ivermectin 3mg (Mectizan)",
            recommendedDosage = "Single dose annual treatment based on height/weight",
            totalUnitsDispensedStatewide = 18500,
            averageMonthlyDemand = 3700,
            priorityLevel = "Moderate",
            riskFactorNotes = "Simulium blackflies breed near fast-flowing streams in Barkin Ladi, Mangu, and Pankshin."
        )
    )

    fun getFumigationLogs(): List<FumigationLogEntity> = listOf(
        FumigationLogEntity(
            id = 1,
            facilityId = 1,
            facilityName = "Jos University Teaching Hospital PHC Annex",
            lga = "Jos North",
            fumigationType = "Organic",
            agentUsed = "Neem & Pyrethrum Botanical Extract (Biological Vector Control)",
            dateScheduled = "2026-07-15",
            dateCompleted = "2026-07-15",
            status = "Completed",
            supervisorName = "Sanitarian Nyam Gyang",
            targetPests = "Mosquitoes & Larvae"
        ),
        FumigationLogEntity(
            id = 2,
            facilityId = 2,
            facilityName = "Tudun Wada PHC",
            lga = "Jos North",
            fumigationType = "Organic",
            agentUsed = "Bacillus thuringiensis israelensis (BTI Bio-larvicide)",
            dateScheduled = "2026-07-18",
            dateCompleted = "2026-07-18",
            status = "Completed",
            supervisorName = "Officer Maryam Agboola",
            targetPests = "Mosquito Larvae in Standing Drainage"
        ),
        FumigationLogEntity(
            id = 3,
            facilityId = 8,
            facilityName = "Shendam Cottage Hospital",
            lga = "Shendam",
            fumigationType = "Chemical",
            agentUsed = "Deltamethrin WP 5% (Indoor Residual Spray)",
            dateScheduled = "2026-07-10",
            dateCompleted = "2026-07-10",
            status = "Completed",
            supervisorName = "Sanitarian Nanribet Longjan",
            targetPests = "Mastomys Rodents & Mosquitoes"
        ),
        FumigationLogEntity(
            id = 4,
            facilityId = 10,
            facilityName = "Barkin Ladi General PHC",
            lga = "Barkin Ladi",
            fumigationType = "Organic",
            agentUsed = "Essential Oil Botanical Spray (Eucalyptus & Clove Blend)",
            dateScheduled = "2026-07-22",
            dateCompleted = "2026-07-22",
            status = "Completed",
            supervisorName = "Sanitarian Rotgat Lar",
            targetPests = "Blackflies & Mosquitoes"
        ),
        FumigationLogEntity(
            id = 5,
            facilityId = 14,
            facilityName = "Mangu General Cottage Hospital",
            lga = "Mangu",
            fumigationType = "Chemical",
            agentUsed = "Alpha-Cypermethrin 10% EC",
            dateScheduled = "2026-07-28",
            dateCompleted = "",
            status = "Scheduled",
            supervisorName = "Officer Daniel Dimka",
            targetPests = "General Vector Control"
        ),
        FumigationLogEntity(
            id = 6,
            facilityId = 16,
            facilityName = "Wase Cottage Hospital",
            lga = "Wase",
            fumigationType = "Organic",
            agentUsed = "Botanical Citronella & Neem Extract",
            dateScheduled = "2026-08-01",
            dateCompleted = "",
            status = "Scheduled",
            supervisorName = "Sanitarian Danjuma Gontor",
            targetPests = "Fly breeding control around wards"
        )
    )

    fun getPatientRecords(): List<PatientRecordEntity> = listOf(
        PatientRecordEntity(
            id = 1,
            cardId = "PL-PS-HC-00001",
            facilityId = 12,
            facilityName = "Bokkos PHC",
            lga = "Bokkos",
            fullName = "Esther Musa",
            gender = "Female",
            age = 66,
            familyHeadName = "Musa Yakubu",
            bloodGroup = "O-",
            allergies = "None",
            chronicConditions = "Hypertension",
            visitDate = "2026-07-24",
            diagnosis = "Acute Bronchitis (Harmattan Dust Exacerbation)",
            prescribedDrugs = "Amoxicillin 500mg, Salbutamol, Paracetamol",
            season = "Dry Season (Harmattan)"
        ),
        PatientRecordEntity(
            id = 2,
            cardId = "PL-PS-HC-00002",
            facilityId = 12,
            facilityName = "Bokkos PHC",
            lga = "Bokkos",
            fullName = "Bala Musa",
            gender = "Male",
            age = 13,
            familyHeadName = "Musa Yakubu",
            bloodGroup = "AB-",
            allergies = "Aspirin",
            chronicConditions = "Asthma",
            visitDate = "2026-07-25",
            diagnosis = "Malaria (Plasmodium falciparum)",
            prescribedDrugs = "Artemether-Lumefantrine, Paracetamol",
            season = "Rainy Season"
        ),
        PatientRecordEntity(
            id = 3,
            cardId = "PL-PS-HC-00003",
            facilityId = 12,
            facilityName = "Bokkos PHC",
            lga = "Bokkos",
            fullName = "Esther Musa",
            gender = "Female",
            age = 3,
            familyHeadName = "Musa Yakubu",
            bloodGroup = "O-",
            allergies = "None",
            chronicConditions = "None",
            visitDate = "2026-07-25",
            diagnosis = "Child Routine Immunization (Pentavalent 3)",
            prescribedDrugs = "Pentavalent Vaccine, Vitamin A Drops",
            season = "Rainy Season"
        ),
        PatientRecordEntity(
            id = 4,
            cardId = "PL-PS-HC-00004",
            facilityId = 12,
            facilityName = "Bokkos PHC",
            lga = "Bokkos",
            fullName = "Michael Musa",
            gender = "Male",
            age = 56,
            familyHeadName = "Musa Yakubu",
            bloodGroup = "AB-",
            allergies = "Latex",
            chronicConditions = "Diabetes Mellitus",
            visitDate = "2026-07-20",
            diagnosis = "Routine Diabetes Checkup & Blood Sugar Monitoring",
            prescribedDrugs = "Metformin 500mg",
            season = "Dry Season (Harmattan)"
        ),
        PatientRecordEntity(
            id = 5,
            cardId = "PL-PS-HC-00005",
            facilityId = 12,
            facilityName = "Bokkos PHC",
            lga = "Bokkos",
            fullName = "James Musa",
            gender = "Male",
            age = 61,
            familyHeadName = "Musa Yakubu",
            bloodGroup = "B-",
            allergies = "Aspirin",
            chronicConditions = "Hypertension",
            visitDate = "2026-07-22",
            diagnosis = "Hypertensive Emergency - Controlled",
            prescribedDrugs = "Amlodipine 10mg",
            season = "Dry Season (Harmattan)"
        ),
        PatientRecordEntity(
            id = 6,
            cardId = "PL-PS-HC-00006",
            facilityId = 6,
            facilityName = "Pankshin Cottage Hospital",
            lga = "Pankshin",
            fullName = "Helen Dung",
            gender = "Female",
            age = 50,
            familyHeadName = "Dung Gyang",
            bloodGroup = "A-",
            allergies = "None",
            chronicConditions = "None",
            visitDate = "2026-07-26",
            diagnosis = "Antenatal Consultation - 3rd Trimester",
            prescribedDrugs = "Folic Acid, Ferrous Sulfate, IPTp Sulfadoxine-Pyrimethamine",
            season = "Rainy Season"
        )
    )

    fun getBirthRecords(): List<BirthRecordEntity> = listOf(
        BirthRecordEntity(id = 1, facilityId = 1, facilityName = "Jos University Teaching Hospital PHC Annex", lga = "Jos North", deliveryDate = "2026-07-25", babyGender = "Boy", birthWeightKg = 3.4, deliveryType = "Normal", motherName = "Patience Dung", birthStatus = "Healthy"),
        BirthRecordEntity(id = 2, facilityId = 1, facilityName = "Jos University Teaching Hospital PHC Annex", lga = "Jos North", deliveryDate = "2026-07-24", babyGender = "Girl", birthWeightKg = 3.1, deliveryType = "Normal", motherName = "Rahila Pam", birthStatus = "Healthy"),
        BirthRecordEntity(id = 3, facilityId = 4, facilityName = "Bukuru Cottage Hospital", lga = "Jos South", deliveryDate = "2026-07-24", babyGender = "Boy", birthWeightKg = 3.6, deliveryType = "Caesarean", motherName = "Blessing Bot", birthStatus = "Healthy"),
        BirthRecordEntity(id = 4, facilityId = 6, facilityName = "Pankshin Cottage Hospital", lga = "Pankshin", deliveryDate = "2026-07-23", babyGender = "Boy", birthWeightKg = 3.2, deliveryType = "Normal", motherName = "Grace Vongtau", birthStatus = "Healthy"),
        BirthRecordEntity(id = 5, facilityId = 8, facilityName = "Shendam Cottage Hospital", lga = "Shendam", deliveryDate = "2026-07-22", babyGender = "Girl", birthWeightKg = 2.9, deliveryType = "Normal", motherName = "Jummai Plang", birthStatus = "Healthy"),
        BirthRecordEntity(id = 6, facilityId = 14, facilityName = "Mangu General Cottage Hospital", lga = "Mangu", deliveryDate = "2026-07-21", babyGender = "Boy", birthWeightKg = 3.5, deliveryType = "Normal", motherName = "Ladi Bako", birthStatus = "Healthy")
    )

    fun getOutbreakAlerts(): List<OutbreakAlertEntity> = listOf(
        OutbreakAlertEntity(id = 1, diseaseName = "Lassa Fever", lga = "Shendam", severityLevel = "Epidemic", reportedCases = 845, weeklyTrendPercentage = 16900, activeDate = "2026-07-26", recommendedResponse = "Deploy Ribavirin stock, activate isolation ward, intensify rodent control."),
        OutbreakAlertEntity(id = 2, diseaseName = "Meningitis", lga = "Wase", severityLevel = "Epidemic", reportedCases = 907, weeklyTrendPercentage = 9070, activeDate = "2026-07-26", recommendedResponse = "Emergency reactive vaccination in border communities; Ceftriaxone stock boost."),
        OutbreakAlertEntity(id = 3, diseaseName = "Measles", lga = "Jos North", severityLevel = "Epidemic", reportedCases = 673, weeklyTrendPercentage = 4487, activeDate = "2026-07-26", recommendedResponse = "Door-to-door under-5 catch-up immunization campaign in Laranto & Katako."),
        OutbreakAlertEntity(id = 4, diseaseName = "Snake Bite", lga = "Shendam", severityLevel = "Epidemic", reportedCases = 589, weeklyTrendPercentage = 2945, activeDate = "2026-07-26", recommendedResponse = "Emergency dispatch of Polyvalent Antivenom vials to Dokan Tofa & Shendam."),
        OutbreakAlertEntity(id = 5, diseaseName = "Yellow Fever", lga = "Langtang South", severityLevel = "Epidemic", reportedCases = 412, weeklyTrendPercentage = 1373, activeDate = "2026-07-26", recommendedResponse = "Vector control, targeted yellow fever vaccination drive in Mabudi."),
        OutbreakAlertEntity(id = 6, diseaseName = "Malaria (Peak)", lga = "Statewide (All 17 LGAs)", severityLevel = "Epidemic", reportedCases = 142000, weeklyTrendPercentage = 280, activeDate = "2026-07-26", recommendedResponse = "Distribution of Artemether-Lumefantrine and organic larviciding of breeding pools.")
    )

    fun getFacilityDrugRequirements(): List<FacilityDrugRequirementEntity> {
        val list = mutableListOf<FacilityDrugRequirementEntity>()
        var idCounter = 1L
        val facilities = getFacilities()

        val seasonalIllnessMappings = listOf(
            Triple("Malaria (Plasmodium falciparum)", "Rainy Season", "Artemether-Lumefantrine 80/480mg"),
            Triple("Cholera & Acute Diarrhea", "Rainy Season", "ORS (Oral Rehydration Salts)"),
            Triple("Snakebite Envenomation", "Rainy Season", "Polyvalent Snake Antivenom"),
            Triple("Meningitis Outbreak", "Dry Season (Harmattan)", "Ceftriaxone 1g Injectable"),
            Triple("Upper Respiratory Infection", "Dry Season (Harmattan)", "Amoxicillin 500mg"),
            Triple("Lassa Fever Surveillance", "Hot Season", "Ribavirin 200mg (Lassa)"),
            Triple("Typhoid & Waterborne Fever", "Hot Season", "Ciprofloxacin 500mg")
        )

        facilities.forEach { fac ->
            seasonalIllnessMappings.forEach { (illness, season, drug) ->
                val monthlyReq = when (illness) {
                    "Malaria (Plasmodium falciparum)" -> 1200
                    "Cholera & Acute Diarrhea" -> 400
                    "Snakebite Envenomation" -> 150
                    "Meningitis Outbreak" -> 250
                    "Upper Respiratory Infection" -> 600
                    "Lassa Fever Surveillance" -> 180
                    else -> 350
                }
                val stock = (monthlyReq * 0.4).toInt() + (0..150).random()
                val buffer = (monthlyReq * 0.3).toInt()
                val priority = if (stock < buffer) "Critical" else if (stock < monthlyReq) "High" else "Moderate"

                list.add(
                    FacilityDrugRequirementEntity(
                        id = idCounter++,
                        facilityId = fac.id,
                        facilityName = fac.name,
                        lga = fac.lga,
                        illnessName = illness,
                        season = season,
                        requiredDrugName = drug,
                        category = when (illness) {
                            "Malaria (Plasmodium falciparum)" -> "Antimalarial"
                            "Snakebite Envenomation" -> "Antivenom"
                            "Lassa Fever Surveillance" -> "Antiviral"
                            "Cholera & Acute Diarrhea" -> "IV Fluid"
                            else -> "Antibiotic"
                        },
                        monthlyRequiredUnits = monthlyReq,
                        currentStockUnits = stock,
                        bufferStockRequired = buffer,
                        quarterlyQuotaAllocated = monthlyReq * 3,
                        priorityLevel = priority
                    )
                )
            }
        }
        return list
    }

    fun getFacilityMedicalSupplies(): List<FacilityMedicalSupplyEntity> {
        val list = mutableListOf<FacilityMedicalSupplyEntity>()
        var idCounter = 1L
        val facilities = getFacilities()

        val itemTypes = listOf(
            Triple("Malaria Rapid Diagnostic Test (RDT) Kits", "Diagnostics", "boxes"),
            Triple("Sterile Syringes & Needles 5ml", "Consumables", "boxes"),
            Triple("Full Body PPE Isolation Suits", "PPE", "kits"),
            Triple("Solar Vaccine Cold Chain Carrier", "Cold Chain", "units"),
            Triple("Surgical Sterile Powder-Free Gloves", "Consumables", "boxes"),
            Triple("IV Infusion Sets & Venoclysis", "Consumables", "packs")
        )

        facilities.forEach { fac ->
            itemTypes.forEach { (type, cat, unit) ->
                val onHand = (30..450).random()
                val minThresh = 80
                val cond = if (onHand < 50) "Critical Depletion" else if (onHand < minThresh) "Low Stock" else "Optimal"

                list.add(
                    FacilityMedicalSupplyEntity(
                        id = idCounter++,
                        facilityId = fac.id,
                        facilityName = fac.name,
                        lga = fac.lga,
                        itemType = type,
                        category = cat,
                        quantityOnHand = onHand,
                        minimumThreshold = minThresh,
                        unitOfMeasure = unit,
                        conditionStatus = cond
                    )
                )
            }
        }
        return list
    }

    fun getFacilityRequirements(): List<FacilityRequirementEntity> {
        val list = mutableListOf<FacilityRequirementEntity>()
        var idCounter = 1L
        val facilities = getFacilities()

        val requirementsList = listOf(
            Tuple5("Artemether-Lumefantrine 80/480mg", "Drug", "Antimalarial", "Malaria (Plasmodium falciparum)", "Rainy Season"),
            Tuple5("Polyvalent Snake Antivenom", "Drug", "Antivenom", "Snakebite Envenomation", "Rainy Season"),
            Tuple5("Malaria Rapid Diagnostic Test (RDT) Kits", "Medical Supply", "Diagnostic Supply", "Malaria (Plasmodium falciparum)", "Rainy Season"),
            Tuple5("Oral Rehydration Salts (ORS)", "Drug", "IV Fluid", "Cholera & Acute Watery Diarrhea", "Rainy Season"),
            Tuple5("Ceftriaxone 1g Injectable", "Drug", "Antibiotic", "Meningitis Outbreak", "Dry Season (Harmattan)"),
            Tuple5("Amoxicillin 500mg", "Drug", "Antibiotic", "Upper Respiratory Infection (URI)", "Dry Season (Harmattan)"),
            Tuple5("Ribavirin 200mg (Lassa)", "Drug", "Antiviral", "Lassa Fever Viral Hemorrhagic Watch", "Hot Season"),
            Tuple5("Sterile Syringes & Needles 5ml", "Medical Supply", "Consumable", "Vaccine & Injections", "All Seasons"),
            Tuple5("Full Body PPE Isolation Suits", "Medical Supply", "Protection & Safety", "Lassa Fever & Infection Control", "Hot Season"),
            Tuple5("Solar Vaccine Cold Chain Carrier", "Medical Supply", "Cold Chain Equipment", "Child Immunization Routine", "All Seasons")
        )

        facilities.forEach { fac ->
            requirementsList.forEach { (item, type, cat, illness, season) ->
                val monthlyReq = when (cat) {
                    "Antimalarial" -> 1500
                    "Antivenom" -> 120
                    "Diagnostic Supply" -> 1800
                    "IV Fluid" -> 600
                    "Antibiotic" -> 800
                    "Antiviral" -> 200
                    else -> 1000
                }
                val stock = (monthlyReq * 0.35).toInt() + (10..100).random()
                val deficit = monthlyReq - stock
                val priority = if (stock < monthlyReq * 0.2) "Critical" else if (stock < monthlyReq * 0.6) "High" else "Moderate"

                list.add(
                    FacilityRequirementEntity(
                        id = idCounter++,
                        facilityId = fac.id,
                        facilityName = fac.name,
                        lga = fac.lga,
                        itemName = item,
                        itemType = type,
                        category = cat,
                        monthlyRequirementUnits = monthlyReq,
                        currentStockUnits = stock,
                        requiredDeficitUnits = deficit,
                        seasonalIllnessTargeted = illness,
                        peakSeason = season,
                        priorityLevel = priority
                    )
                )
            }
        }
        return list
    }

    fun getMedicalSupplyInventory(): List<MedicalSupplyInventoryEntity> {
        val list = mutableListOf<MedicalSupplyInventoryEntity>()
        var idCounter = 1L
        val facilities = getFacilities()

        val supplyTypes = listOf(
            Triple("Malaria Rapid Diagnostic Test (RDT) Kits", "Diagnostic Supply", "kits"),
            Triple("Sterile Syringes & Needles 5ml", "Consumable", "boxes"),
            Triple("Full Body PPE Hazard Isolation Suits", "Protection & Safety", "packs"),
            Triple("Solar Cold Chain Vaccine Carrier Pack", "Cold Chain Equipment", "units"),
            Triple("Sterile Powder-Free Surgical Gloves", "Consumable", "boxes"),
            Triple("IV Venoclysis Infusion Drip Sets", "Consumable", "packs"),
            Triple("Digital Infrared Forehead Thermometer", "Diagnostic Supply", "units")
        )

        facilities.forEach { fac ->
            supplyTypes.forEach { (name, cat, unit) ->
                val qty = (25..500).random()
                val reorder = 100
                val status = if (qty < 50) "Critical Low" else if (qty < reorder) "Low Stock" else "Sufficient"

                list.add(
                    MedicalSupplyInventoryEntity(
                        id = idCounter++,
                        facilityId = fac.id,
                        facilityName = fac.name,
                        lga = fac.lga,
                        supplyName = name,
                        category = cat,
                        quantityInStock = qty,
                        unitOfMeasure = unit,
                        reorderThreshold = reorder,
                        status = status,
                        lastInspectionDate = "2026-07-20"
                    )
                )
            }
        }
        return list
    }

    fun getSeasonalIllnessMappings(): List<SeasonalIllnessMappingEntity> = listOf(
        SeasonalIllnessMappingEntity(
            id = 1,
            illnessName = "Malaria (Plasmodium falciparum)",
            season = "Rainy Season",
            riskLevel = "Critical",
            primaryDrugsRequired = "Artemether-Lumefantrine 80/480mg, Injectable Artesunate 60mg",
            essentialSuppliesRequired = "Malaria Rapid Diagnostic Test (RDT) Kits, Mosquito Bed Nets, IV Infusion Sets",
            recommendedDosage = "AL 1 tablet BD x 3 days; Artesunate IV 2.4mg/kg at 0, 12, 24 hrs",
            averageMonthlyDemandStatewide = 142000,
            expectedSurgeMultiplier = 3.8,
            priorityLgas = "Statewide (All 17 LGAs - Jos North, Shendam, Pankshin, Mangu)",
            riskFactorNotes = "Standing rain pools across Plateau basin provide vector breeding grounds."
        ),
        SeasonalIllnessMappingEntity(
            id = 2,
            illnessName = "Snakebite Envenomation (Carpet Viper)",
            season = "Rainy Season",
            riskLevel = "Critical",
            primaryDrugsRequired = "Polyvalent Snake Antivenom (EchiTox), Tetanus Toxoid, IV Normal Saline",
            essentialSuppliesRequired = "Antivenom Cold Transport Box, IV Cannulas, Bandages",
            recommendedDosage = "1-2 vials Polyvalent Antivenom IV in 250ml Normal Saline over 1 hour",
            averageMonthlyDemandStatewide = 9400,
            expectedSurgeMultiplier = 2.9,
            priorityLgas = "Shendam, Langtang North, Langtang South, Mikang, Qua'an Pan",
            riskFactorNotes = "Heightened human-snake contact during agricultural land clearing."
        ),
        SeasonalIllnessMappingEntity(
            id = 3,
            illnessName = "Meningitis (Neisseria meningitidis)",
            season = "Dry Season (Harmattan)",
            riskLevel = "Critical",
            primaryDrugsRequired = "Ceftriaxone 1g Injectable, Oily Chloramphenicol, Paracetamol",
            essentialSuppliesRequired = "Meningococcal ACWY Vaccine Vials, Sterile Syringes 5ml, Lumbar Puncture Needles",
            recommendedDosage = "Ceftriaxone 2g IV daily for 7 days or single dose oily Chloramphenicol",
            averageMonthlyDemandStatewide = 18200,
            expectedSurgeMultiplier = 4.2,
            priorityLgas = "Wase, Kanam, Kanke, Pankshin, Shendam",
            riskFactorNotes = "Hot dry Harmattan dust weakens nasal mucosa, accelerating bacterial invasion."
        ),
        SeasonalIllnessMappingEntity(
            id = 4,
            illnessName = "Lassa Fever (Viral Hemorrhagic Fever)",
            season = "Hot Season",
            riskLevel = "Critical",
            primaryDrugsRequired = "Ribavirin 200mg Tablets & IV Infusion, Paracetamol",
            essentialSuppliesRequired = "Full Body PPE Hazard Suits, N95 Masks, Biohazard Waste Bags",
            recommendedDosage = "Ribavirin IV loading 30mg/kg then 16mg/kg q6h x 4 days, then 8mg/kg q8h x 6 days",
            averageMonthlyDemandStatewide = 12800,
            expectedSurgeMultiplier = 3.1,
            priorityLgas = "Shendam, Kanam, Pankshin, Langtang North",
            riskFactorNotes = "Mastomys natalensis rodents invade homes during intense heat and bush burning."
        ),
        SeasonalIllnessMappingEntity(
            id = 5,
            illnessName = "Cholera & Acute Watery Diarrhea",
            season = "Rainy Season",
            riskLevel = "Critical",
            primaryDrugsRequired = "Oral Rehydration Salts (ORS), Zinc Sulfate 20mg, Doxycycline 100mg",
            essentialSuppliesRequired = "Ringer's Lactate IV Infusion Bags, IV Giving Sets, Water Purification Tablets",
            recommendedDosage = "Rapid rehydration with ORS / Ringer's Lactate + single dose Doxycycline 300mg",
            averageMonthlyDemandStatewide = 28600,
            expectedSurgeMultiplier = 3.5,
            priorityLgas = "Jos North (Laranto), Mangu (Panyam), Shendam, Barkin Ladi",
            riskFactorNotes = "Heavy rainfall causes flood run-off contaminating shallow wells and open rivers."
        )
    )

    fun getHealthcarePersonnel(): List<HealthcarePersonnelEntity> {
        val list = mutableListOf<HealthcarePersonnelEntity>()
        val facilities = getFacilities()

        val doctorSpecializations = listOf(
            Pair("General Practice", "MBBS"),
            Pair("Obstetrics & Gynecology", "FWACS"),
            Pair("Pediatric Care", "FWACP"),
            Pair("Epidemiology & Infectious Diseases", "MPH, MBBS"),
            Pair("Anesthesia & Critical Care", "MBBS, DA")
        )

        val nurseSpecializations = listOf(
            Pair("Public Health Nursing", "B.NSc, RPHN"),
            Pair("Maternal & Child Health Nursing", "RN/RM"),
            Pair("Pediatric & Neonatal Nursing", "RN, RPON"),
            Pair("Community Health Nursing", "RN, CHO"),
            Pair("Infection Control & Surveillance Nursing", "RN, CIC")
        )

        var idCounter = 1L
        facilities.forEachIndexed { index, fac ->
            // Doctor for facility
            val (docSpec, docQual) = doctorSpecializations[index % doctorSpecializations.size]
            val docName = when (index % 5) {
                0 -> "Dr. Nanlop Pam"
                1 -> "Dr. Emmanuel Gyang"
                2 -> "Dr. Fatima Usman"
                3 -> "Dr. Dachollom Kim"
                else -> "Dr. Patience Dalo"
            }
            list.add(
                HealthcarePersonnelEntity(
                    id = idCounter++,
                    fullName = docName,
                    cadre = "Doctor",
                    licenseNumber = "MDCN/2024/${1000 + index}",
                    specialization = docSpec,
                    qualification = docQual,
                    phone = "+234 803 456 ${1000 + index}",
                    email = docName.lowercase().replace("dr. ", "").replace(" ", ".") + "@plateauphc.gov.ng",
                    assignedFacilityId = fac.id,
                    assignedFacilityName = fac.name,
                    lga = fac.lga,
                    registrationDate = "2025-06-10",
                    assignmentDate = "2025-07-01",
                    employmentType = if (index % 3 == 0) "NYSC Doctor" else "Permanent",
                    dutyShift = if (index % 2 == 0) "Morning" else "24hr On-Call",
                    activeStatus = true
                )
            )

            // Nurse 1 for facility
            val (nurseSpec1, nurseQual1) = nurseSpecializations[index % nurseSpecializations.size]
            val nurseName1 = when (index % 5) {
                0 -> "Nurse Blessing Choji"
                1 -> "Nurse Zainab Haruna"
                2 -> "Nurse Maryam Gowon"
                3 -> "Nurse Comfort Lar"
                else -> "Nurse Solomon Bot"
            }
            list.add(
                HealthcarePersonnelEntity(
                    id = idCounter++,
                    fullName = nurseName1,
                    cadre = "Nurse",
                    licenseNumber = "NMCN/2023/${3000 + index}",
                    specialization = nurseSpec1,
                    qualification = nurseQual1,
                    phone = "+234 802 111 ${2000 + index}",
                    email = nurseName1.lowercase().replace("nurse ", "").replace(" ", ".") + "@plateauphc.gov.ng",
                    assignedFacilityId = fac.id,
                    assignedFacilityName = fac.name,
                    lga = fac.lga,
                    registrationDate = "2024-03-15",
                    assignmentDate = "2024-04-01",
                    employmentType = "Permanent",
                    dutyShift = "Morning",
                    activeStatus = true
                )
            )

            // Nurse 2 for facility
            val (nurseSpec2, nurseQual2) = nurseSpecializations[(index + 1) % nurseSpecializations.size]
            val nurseName2 = when ((index + 2) % 5) {
                0 -> "Nurse Racheal Jang"
                1 -> "Nurse Abigail Longjan"
                2 -> "Nurse David Tapgun"
                3 -> "Nurse Esther Plateau"
                else -> "Nurse Joshua Dariye"
            }
            list.add(
                HealthcarePersonnelEntity(
                    id = idCounter++,
                    fullName = nurseName2,
                    cadre = "Nurse",
                    licenseNumber = "NMCN/2024/${4000 + index}",
                    specialization = nurseSpec2,
                    qualification = nurseQual2,
                    phone = "+234 805 777 ${3000 + index}",
                    email = nurseName2.lowercase().replace("nurse ", "").replace(" ", ".") + "@plateauphc.gov.ng",
                    assignedFacilityId = fac.id,
                    assignedFacilityName = fac.name,
                    lga = fac.lga,
                    registrationDate = "2025-01-20",
                    assignmentDate = "2025-02-01",
                    employmentType = "Contract Staff",
                    dutyShift = "Night",
                    activeStatus = true
                )
            )
        }
        return list
    }

    fun getFacilityIssueReports(): List<FacilityIssueReportEntity> = listOf(
        FacilityIssueReportEntity(
            id = 1,
            facilityId = 1,
            facilityName = "Jos University Teaching Hospital PHC Annex",
            lga = "Jos North",
            category = "Cold Chain / Solar Refrigerator",
            issueTitle = "Solar Vaccine Fridge Temperature Fluctuation (+8.5°C Alarm)",
            description = "The main solar-powered vaccine refrigerator in the immunization room triggered an over-temperature alarm (+8.5°C). Inverter battery voltage drops under cloud cover. Inspection needed to prevent vaccine spoilage.",
            urgencyLevel = "URGENT_CRITICAL",
            reportedByStaffName = "Dr. Danladi Gyang",
            reportedByRole = "Chief Medical Officer",
            contactPhone = "+234 803 123 4567",
            departmentOrWard = "Immunization & Cold Chain Unit",
            dateReported = "2026-07-25 09:30",
            status = "Work Order Issued",
            workOrderTicketNumber = "WO-2026-PHC-042",
            resolutionNotes = "Solar technician dispatched from Jos Ministry of Health workshop."
        ),
        FacilityIssueReportEntity(
            id = 2,
            facilityId = 2,
            facilityName = "Tudun Wada PHC",
            lga = "Jos North",
            category = "Equipment Failure",
            issueTitle = "Laboratory Centrifuge Motor Bearing Noise & Excessive Vibration",
            description = "Main blood sample centrifuge unit in Lab Room 1 produces severe grinding noise during spin cycles. Rotor imbalance detected. Replacement bearings or benchtop replacement unit requested.",
            urgencyLevel = "MEDIUM",
            reportedByStaffName = "Nurse Mary Pam",
            reportedByRole = "Senior Laboratory Technologist",
            contactPhone = "+234 802 987 6543",
            departmentOrWard = "Diagnostic Laboratory",
            dateReported = "2026-07-24 14:15",
            status = "Pending Review",
            workOrderTicketNumber = "WO-2026-PHC-088",
            resolutionNotes = null
        ),
        FacilityIssueReportEntity(
            id = 3,
            facilityId = 11,
            facilityName = "Shendam General Cottage Hospital",
            lga = "Shendam",
            category = "Power / Generator Fault",
            issueTitle = "15kVA Diesel Backup Generator Starter Battery Blown",
            description = "During recent grid blackout, the 15kVA standby generator failed to auto-start due to dead starter battery and faulty alternator belt. Emergency theater relying on solar backup.",
            urgencyLevel = "URGENT_CRITICAL",
            reportedByStaffName = "Pharm. Moses Dusu",
            reportedByRole = "Head of Medical Services",
            contactPhone = "+234 803 555 1011",
            departmentOrWard = "Emergency & Operating Theater",
            dateReported = "2026-07-26 08:00",
            status = "Under Repair",
            workOrderTicketNumber = "WO-2026-PHC-011",
            resolutionNotes = "Replacement 12V 100Ah heavy-duty battery en route from Shendam Zonal Office."
        ),
        FacilityIssueReportEntity(
            id = 4,
            facilityId = 14,
            facilityName = "Pankshin General Hospital PHC",
            lga = "Pankshin",
            category = "Water & Sanitation Outage",
            issueTitle = "Submersible Borehole Pump Pressure Drop in Maternity Delivery Ward",
            description = "Submersible water pump feeding overhead storage tanks in Delivery Ward 2 has low water pressure. Water supply intermittent for handwashing and sanitation.",
            urgencyLevel = "HIGH",
            reportedByStaffName = "Esther Longjan",
            reportedByRole = "Officer in Charge (OIC)",
            contactPhone = "+234 802 888 2014",
            departmentOrWard = "Maternity & Labor Delivery Ward",
            dateReported = "2026-07-23 11:45",
            status = "Work Order Issued",
            workOrderTicketNumber = "WO-2026-PHC-059",
            resolutionNotes = "Plumbing team assigned for pressure control valve replacement."
        )
    )

    fun getFacilityAssessments(): List<FacilityAssessmentEntity> = listOf(
        FacilityAssessmentEntity(
            id = 1,
            facilityId = 1,
            facilityName = "Jos University Teaching Hospital PHC Annex",
            lga = "Jos North",
            inspectorName = "Insp. Solomon Choji",
            inspectorBadgeId = "INS-PL-2026-042",
            assessmentDate = "2026-07-26 09:30",
            overallScorePct = 88,
            cleanlinessRating = "Excellent",
            coldChainStatus = "Functional Solar Refrigerator",
            waterSanitationRating = "Borehole Operational",
            staffingAdequacy = "Fully Staffed",
            drugStockRating = "Sufficient Antimalarials & Antivenom",
            buildingStructureCondition = "Intact Roof & Walls",
            inspectorComments = "Facility is operating at high standards. Solar cold chain maintains steady 4°C for vaccines. Recommended for annual commendation.",
            recommendedAction = "Compliance Approved",
            syncStatus = "BUFFERED_OFFLINE",
            isBufferedOffline = true
        ),
        FacilityAssessmentEntity(
            id = 2,
            facilityId = 8,
            facilityName = "Shendam Cottage Hospital",
            lga = "Shendam",
            inspectorName = "Insp. Solomon Choji",
            inspectorBadgeId = "INS-PL-2026-042",
            assessmentDate = "2026-07-25 14:10",
            overallScorePct = 62,
            cleanlinessRating = "Fair",
            coldChainStatus = "Battery Backup Low",
            waterSanitationRating = "Water Shortage",
            staffingAdequacy = "Moderate Deficit",
            drugStockRating = "Low Stocks",
            buildingStructureCondition = "Minor Repairs Needed",
            inspectorComments = "Lassa fever surveillance area needs immediate restocking of Ribavirin and battery replacement for backup solar inverter.",
            recommendedAction = "Immediate Emergency Support",
            syncStatus = "BUFFERED_OFFLINE",
            isBufferedOffline = true
        ),
        FacilityAssessmentEntity(
            id = 3,
            facilityId = 12,
            facilityName = "Bokkos PHC",
            lga = "Bokkos",
            inspectorName = "Insp. Victoria Gyang",
            inspectorBadgeId = "INS-PL-2026-089",
            assessmentDate = "2026-07-24 11:20",
            overallScorePct = 78,
            cleanlinessRating = "Good",
            coldChainStatus = "Functional Solar Refrigerator",
            waterSanitationRating = "Borehole Operational",
            staffingAdequacy = "Severe Doctor Shortage",
            drugStockRating = "Sufficient Antimalarials & Antivenom",
            buildingStructureCondition = "Intact Roof & Walls",
            inspectorComments = "Clean environment and good drug inventory. Requesting 1 additional resident doctor assignment to handle Harmattan respiratory surge.",
            recommendedAction = "Routine Quarterly Restock",
            syncStatus = "BUFFERED_OFFLINE",
            isBufferedOffline = true
        )
    )
}



private data class Tuple5<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E
)

