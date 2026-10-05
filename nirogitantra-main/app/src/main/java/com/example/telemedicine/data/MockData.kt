package com.example.telemedicine.data

import com.example.telemedicine.LocalizedText

enum class ConsultationType {
    VIDEO,
    AUDIO,
    IN_PERSON
}

data class HealthRecord(
    val id: Int,
    val title: LocalizedText,
    val value: String,
    val unit: LocalizedText,
    val recordedOn: String,
    val note: LocalizedText
)

data class SymptomOption(
    val id: Int,
    val title: LocalizedText,
    val description: LocalizedText
)

data class MedicineStock(
    val id: Int,
    val name: LocalizedText,
    val description: LocalizedText,
    val form: String,
    val dosage: String,
    val availableUnits: Int,
    val expiry: String,
    val status: LocalizedText,
    val pricePerUnit: Double
)

data class PatientAppointment(
    val id: Int,
    val doctorName: String,
    val specialization: LocalizedText,
    val timeLabel: String,
    val durationMinutes: Int,
    val type: ConsultationType,
    val symptoms: String,
    val status: LocalizedText
)

data class MedicineScheduleEntry(
    val id: Int,
    val medicineName: String,
    val dosage: String,
    val nextDoseLabel: String,
    val timeOfDay: String,
    val instructions: LocalizedText
)

data class RecommendedDoctor(
    val id: Int,
    val name: String,
    val specialty: LocalizedText,
    val rating: Double,
    val totalReviews: Int,
    val isFavourite: Boolean = false
)

data class PatientActivity(
    val id: Int,
    val icon: String,
    val title: LocalizedText,
    val subtitle: LocalizedText,
    val timestampLabel: String
)

data class PatientVisitSummary(
    val date: String,
    val concern: LocalizedText,
    val outcome: LocalizedText
)

data class PatientCaseSummary(
    val name: String,
    val age: Int,
    val gender: String,
    val chronicConditions: List<LocalizedText>,
    val medications: List<LocalizedText>,
    val aiSummary: LocalizedText,
    val recentVisits: List<PatientVisitSummary>
)

data class DoctorSchedule(
    val id: Int,
    val patientName: String,
    val time: String,
    val notes: LocalizedText
)

data class CallRequest(
    val id: Int,
    val patientName: String,
    val concern: LocalizedText,
    val priority: LocalizedText
)

data class MedicineOrderItem(
    val id: Int,
    val name: LocalizedText,
    val requestedQuantity: Int,
    val availableQuantity: Int,
    val note: LocalizedText? = null
)

data class MedicineOrder(
    val id: Int,
    val customerName: String,
    val medicine: LocalizedText,
    val quantity: Int,
    val status: LocalizedText,
    val items: List<MedicineOrderItem>
)

private fun text(english: String, hindi: String = english) = LocalizedText(english, english)

val healthRecords = listOf(
    HealthRecord(
        id = 1,
        title = text("Blood Pressure", "???????"),
        value = "118/76",
        unit = text("mmHg", "???????? ????"),
        recordedOn = "2025-09-16",
        note = text("Stable reading during morning check.", "???? ?? ???? ??? ?????? ????? ????")
    ),
    HealthRecord(
        id = 2,
        title = text("Fasting Blood Sugar", "????? ???? ??????"),
        value = "99",
        unit = text("mg/dL", "????????? ????? ????????"),
        recordedOn = "2025-09-13",
        note = text("Continue light walks after dinner.", "??? ?? ???? ?? ??? ????? ??? ???? ?????")
    ),
    HealthRecord(
        id = 3,
        title = text("Heart Rate", "???? ???"),
        value = "76",
        unit = text("bpm", "????? ????? ????"),
        recordedOn = "2025-09-10",
        note = text("Within the healthy range.", "?????? ???? ????")
    )
)

val symptomOptions = listOf(
    SymptomOption(1, text("Fever", "?????"), text("Body temperature above 99 F.", "???? ?? ?????? 99 F ?? ????")),
    SymptomOption(2, text("Cough", "?????"), text("Persistent dry or wet cough.", "?????? ???? ?? ?? ???? ??????")),
    SymptomOption(3, text("Fatigue", "????"), text("Feeling unusually tired.", "???????? ???? ????? ?????")),
    SymptomOption(4, text("Shortness of Breath", "???? ?????"), text("Breathing difficulty during normal activity.", "??????? ?????????? ??? ???? ???? ??? ???????"))
)

val pharmacyStock = listOf(
    MedicineStock(
        id = 1,
        name = text("Paracetamol 500 mg", "??????????? 500 ??.????."),
        description = text("Strip of 10 tablets for fever relief.", "????? ?? ???? ?? ??? 10 ?????? ?? ????????"),
        form = "Tablet",
        dosage = "500 mg",
        availableUnits = 124,
        expiry = "2026-01-15",
        status = text("Healthy stock", "???????? ?????"),
        pricePerUnit = 22.0
    ),
    MedicineStock(
        id = 2,
        name = text("Amoxicillin 250 mg", "???????????? 250 ??.????."),
        description = text("Capsule pack for bacterial infections.", "?????????? ??????? ?? ??? ??????? ????"),
        form = "Capsule",
        dosage = "250 mg",
        availableUnits = 48,
        expiry = "2025-12-05",
        status = text("Restock soon", "???? ????? ????"),
        pricePerUnit = 68.0
    ),
    MedicineStock(
        id = 3,
        name = text("ORS Sachet", "????? ????"),
        description = text("Single-serve sachet to prevent dehydration.", "?????????? ????? ?? ??? ??? ?????"),
        form = "Sachet",
        dosage = "5 g",
        availableUnits = 210,
        expiry = "2027-03-01",
        status = text("Healthy stock", "???????? ?????"),
        pricePerUnit = 12.0
    )
)

val patientAppointments = listOf(
    PatientAppointment(
        id = 1,
        doctorName = "Dr. Ananya Singh",
        specialization = text("General Physician", "??????? ????????"),
        timeLabel = "Today - 11:30 AM",
        durationMinutes = 25,
        type = ConsultationType.VIDEO,
        symptoms = "Follow-up on diabetes medication",
        status = text("Scheduled", "?????????")
    ),
    PatientAppointment(
        id = 2,
        doctorName = "Dr. Raghav Nair",
        specialization = text("Pulmonologist", "???? ??? ????????"),
        timeLabel = "Tomorrow - 09:15 AM",
        durationMinutes = 20,
        type = ConsultationType.AUDIO,
        symptoms = "Persistent cough and cold",
        status = text("Scheduled", "?????????")
    ),
    PatientAppointment(
        id = 3,
        doctorName = "Dr. Isha Verma",
        specialization = text("Nutritionist", "???? ????????"),
        timeLabel = "Fri, 20 Sep - 04:30 PM",
        durationMinutes = 30,
        type = ConsultationType.IN_PERSON,
        symptoms = "Diet review for anemia",
        status = text("Waiting list", "????????? ????")
    )
)

val medicineSchedule = listOf(
    MedicineScheduleEntry(
        id = 1,
        medicineName = "Metformin 500 mg",
        dosage = "1 tablet",
        nextDoseLabel = "Today - 08:00 AM",
        timeOfDay = "Morning",
        instructions = text("Take after breakfast.", "?????? ?? ??? ????")
    ),
    MedicineScheduleEntry(
        id = 2,
        medicineName = "Vitamin D Sachet",
        dosage = "1 sachet",
        nextDoseLabel = "Today - 02:00 PM",
        timeOfDay = "Afternoon",
        instructions = text("Mix with water and drink slowly.", "???? ??? ?????? ????-???? ?????")
    ),
    MedicineScheduleEntry(
        id = 3,
        medicineName = "Telmisartan 40 mg",
        dosage = "1 tablet",
        nextDoseLabel = "Tonight - 09:00 PM",
        timeOfDay = "Night",
        instructions = text("Take at the same time daily.", "???????? ?? ?? ??? ?? ????")
    )
)

val recommendedDoctors = listOf(
    RecommendedDoctor(1, "Dr. Rakesh Mehta", text("Cardiologist", "???? ??? ????????"), 4.9, 214),
    RecommendedDoctor(2, "Dr. Sana Qureshi", text("Endocrinologist", "?????????? ??? ????????"), 4.7, 189, isFavourite = true),
    RecommendedDoctor(3, "Dr. Mohit Kulkarni", text("Pulmonologist", "???? ??? ????????"), 4.6, 136)
)

val patientActivities = listOf(
    PatientActivity(1, "check", text("Consultation completed", "??????? ????? ???"), text("Dr. Kavya Jain shared follow-up notes.", "??. ????? ??? ?? ????-?? ????? ???? ????"), "2 hours ago"),
    PatientActivity(2, "calendar", text("New appointment booked", "?? ?????????? ??? ???"), text("Pulmonology visit confirmed for tomorrow.", "???? ??? ???????? ?? ??????? ?? ?? ??? ?????? ????"), "4 hours ago"),
    PatientActivity(3, "prescription", text("Prescription updated", "?????????????? ????? ???"), text("Metformin refill sent to Jan Aushadi worker.", "?????????? ????? ?? ???? ????? ?? ???? ????"), "Yesterday")
)

val doctorPatientProfiles: Map<String, PatientCaseSummary> = listOf(
    PatientCaseSummary(
        name = "Neha Sharma",
        age = 32,
        gender = "Female",
        chronicConditions = listOf(
            LocalizedText("Type 2 diabetes (well managed)", "Type 2 diabetes (niyantrit)"),
            LocalizedText("Seasonal asthma", "Mausami asthma")
        ),
        medications = listOf(
            LocalizedText("Metformin 500 mg - evening", "Metformin 500 mg - shaam"),
            LocalizedText("Telmisartan 40 mg - morning", "Telmisartan 40 mg - subah")
        ),
        aiSummary = LocalizedText("Glucose trend stable with occasional post-meal spikes; monitor hydration and continue walk routine. Recent cough episodes linked to seasonal triggers.", "Shakar star sthir hai, kabhi kabhi khane ke baad badh jata hai; paani aur rozana walk jaari rakhein. Hal hi ki khansi mausami karnon se judi hai."),
        recentVisits = listOf(
            PatientVisitSummary(
                date = "12 Sep 2025",
                concern = LocalizedText("Video follow-up", "Video follow-up"),
                outcome = LocalizedText("Adjusted Metformin timing and added salt restriction checklist.", "Metformin ka samay badla aur namak niyantran suchi di gayi.")
            ),
            PatientVisitSummary(
                date = "28 Aug 2025",
                concern = LocalizedText("Lab review", "Lab review"),
                outcome = LocalizedText("HbA1c improved to 6.3; advised to continue current plan.", "HbA1c 6.3 par aaya; vartaman yojana jaari rakhne ko kaha.")
            ),
            PatientVisitSummary(
                date = "05 Aug 2025",
                concern = LocalizedText("Asthma flare consult", "Asthma flare consult"),
                outcome = LocalizedText("Provided inhaler spacer technique refresher and steam schedule.", "Inhaler spacer ka upyog phir se samjhaya aur bhaap schedule diya.")
            )
        )
    ),
    PatientCaseSummary(
        name = "Default",
        age = 40,
        gender = "Female",
        chronicConditions = listOf(LocalizedText("No chronic conditions recorded", "Koi chronic sthiti darj nahin")),
        medications = emptyList(),
        aiSummary = LocalizedText("No historical data found for this patient.", "Is patient ke liye itihas uplabdh nahin hai."),
        recentVisits = emptyList()
    )
).associateBy { it.name }

fun patientCaseSummaryFor(name: String): PatientCaseSummary =
    doctorPatientProfiles[name] ?: doctorPatientProfiles.getValue("Default")

val doctorSchedules = listOf(
    DoctorSchedule(1, "Neha Sharma", "09:00 AM", text("Review blood pressure medication.", "??????? ??? ?? ???????")),
    DoctorSchedule(2, "Ravi Patel", "09:45 AM", text("Discuss lab test results.", "??????? ?? ?????")),
    DoctorSchedule(3, "Anita Singh", "10:30 AM", text("Diet plan check-in.", "???? ????? ?? ???????"))
)

val doctorCallRequests = listOf(
    CallRequest(1, "Sunil Rao", text("Recurring migraine episodes.", "???-??? ????????"), text("High", "????")),
    CallRequest(2, "Leena Verma", text("Child with fever for two days.", "?? ????? ?? ????? ?? ?????"), text("Medium", "?????")),
    CallRequest(3, "Rahul Jain", text("Requesting prescription refill.", "?????????????? ????? ?? ??????"), text("Low", "?????"))
)

val workerOrders = listOf(
    MedicineOrder(
        id = 1,
        customerName = "Suman Devi",
        medicine = text("Metformin 500 mg", "?????????? 500 ??.????."),
        quantity = 3,
        status = text("Waiting for confirmation", "?????? ?? ?????????"),
        items = listOf(
            MedicineOrderItem(1, text("Metformin 500 mg", "?????????? 500 ??.????."), 2, 2, null),
            MedicineOrderItem(2, text("Vitamin D Sachet", "??????? ?? ????"), 1, 0, text("Low stock in pharmacy", "???????? ??? ????? ?? ??"))
        )
    ),
    MedicineOrder(
        id = 2,
        customerName = "Rakesh Kumar",
        medicine = text("Losartan 50 mg", "??????? 50 ??.????."),
        quantity = 4,
        status = text("Ready for pickup", "????? ?? ??? ?????"),
        items = listOf(
            MedicineOrderItem(1, text("Losartan 50 mg", "??????? 50 ??.????."), 2, 2, null),
            MedicineOrderItem(2, text("Atorvastatin 20 mg", "????????????? 20 ??.????."), 1, 1, null),
            MedicineOrderItem(3, text("Aspirin 75 mg", "???????? 75 ??.????."), 1, 1, null)
        )
    ),
    MedicineOrder(
        id = 3,
        customerName = "Priya Gupta",
        medicine = text("Iron Syrup", "???? ????"),
        quantity = 5,
        status = text("Out for delivery", "??????? ?? ??? ?????"),
        items = listOf(
            MedicineOrderItem(1, text("Iron Syrup", "???? ????"), 2, 2, null),
            MedicineOrderItem(2, text("Folic Acid 5 mg", "????? ???? 5 ??.????."), 2, 1, text("Remaining unit arriving this evening", "??? ????? ??? ?? ????")),
            MedicineOrderItem(3, text("Calcium Tablet", "???????? ??????"), 1, 1, null)
        )
    )
)


