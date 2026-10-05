package com.example.telemedicine.data

/**
 * Mock data to make the screen work immediately.
 * Replace with real network calls later.
 */
object WelfareMock {

    private val areas = listOf(
        Area("blr-560001", "Bengaluru Central", "560001", "Karnataka"),
        Area("del-110001", "New Delhi", "110001", "Delhi"),
        Area("mum-400001", "South Mumbai", "400001", "Maharashtra")
    )

    fun defaultAreas(): List<Area> = areas

    fun snapshot(area: Area) = WelfareSnapshot(
        area = area,
        lastUpdated = System.currentTimeMillis(),
        kpi = WelfareKpi(
            hospitalsReporting = 12,
            totalActiveCases = 482,
            bedOccupancy = 71,
            medicineFillRate = 83
        ),
        topDiseases = listOf(
            DiseaseStat("Dengue", active = 168, newToday = 12, trend7d = +14.3f),
            DiseaseStat("Seasonal Flu", active = 121, newToday = 27, trend7d = -8.5f),
            DiseaseStat("Diarrheal Disease", active = 96, newToday = 9, trend7d = +5.0f),
            DiseaseStat("COVID‑19", active = 54, newToday = 3, trend7d = +2.1f)
        ),
        actions = listOf(
            ImprovementAction(
                "1",
                "Fogging & larvae control",
                "Daily fogging near stagnant water; community cleanup drives.",
                Priority.CRITICAL
            ),
            ImprovementAction(
                "2",
                "Safe drinking water",
                "Repair 2 municipal taps; distribute chlorine tablets in slums.",
                Priority.HIGH
            ),
            ImprovementAction(
                "3",
                "OPD triage & signage",
                "Separate fever clinic queue; reduce waiting time.",
                Priority.MEDIUM
            ),
            ImprovementAction(
                "4",
                "Mask & cough etiquette drive",
                "IEC material at schools & markets.",
                Priority.LOW
            )
        ),
        doctorNeeds = listOf(
            HospitalNeed("District Hospital", "Pediatrics", needed = 5, available = 3),
            HospitalNeed("District Hospital", "General Medicine", needed = 6, available = 4),
            HospitalNeed("City Care Clinic", "Dermatology", needed = 2, available = 0),
            HospitalNeed("Metro Hospital", "Anesthesiology", needed = 4, available = 2)
        ),
        shortages = listOf(
            HospitalShortage("District Hospital", "ICU Beds", shortage = 6, unit = "beds"),
            HospitalShortage("District Hospital", "Platelets", shortage = 28, unit = "units"),
            HospitalShortage("Metro Hospital", "Oxygen Cylinders", shortage = 14, unit = "cylinders"),
            HospitalShortage("City Care Clinic", "Rapid Dengue Kits", shortage = 90, unit = "kits")
        ),
        medicineDemands = listOf(
            MedicineDemand("Paracetamol 500mg", demanded = 5000, fulfilled = 3600, hospital = "All Hospitals"),
            MedicineDemand("ORS Sachets", demanded = 3000, fulfilled = 1700, hospital = "All Hospitals"),
            MedicineDemand("Doxycycline 100mg", demanded = 1800, fulfilled = 900, hospital = "District Hospital"),
            MedicineDemand("Azithromycin 500mg", demanded = 1000, fulfilled = 700, hospital = "City Care Clinic")
        ),
        feedback = listOf(
            Feedback(
                authorType = AuthorType.DOCTOR,
                hospital = "District Hospital",
                department = "Medicine",
                rating = 4,
                comment = "Need faster lab turnaround for dengue NS1.",
                date = System.currentTimeMillis() - 86_400_000L
            ),
            Feedback(
                authorType = AuthorType.PATIENT,
                hospital = "District Hospital",
                department = null,
                rating = 2,
                comment = "Queue management poor; waited ~2 hours.",
                date = System.currentTimeMillis() - 7_200_000L
            ),
            Feedback(
                authorType = AuthorType.DOCTOR,
                hospital = "Metro Hospital",
                department = "ICU",
                rating = 3,
                comment = "Shortage of ventilator circuits last weekend.",
                date = System.currentTimeMillis() - 172_800_000L
            ),
            Feedback(
                authorType = AuthorType.PATIENT,
                hospital = "City Care Clinic",
                department = null,
                rating = 5,
                comment = "Doctor attentive; pharmacy out of ORS.",
                date = System.currentTimeMillis() - 3_600_000L
            )
        )
    )
}
