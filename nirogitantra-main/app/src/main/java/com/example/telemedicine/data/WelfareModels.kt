package com.example.telemedicine.data

enum class Priority { LOW, MEDIUM, HIGH, CRITICAL }
enum class AuthorType { DOCTOR, PATIENT }

data class Area(
    val id: String,
    val name: String,
    val pincode: String? = null,
    val state: String? = null
)

data class DiseaseStat(
    val disease: String,
    val active: Int,
    val newToday: Int,
    /** % change in last 7 days; e.g. +12.5 means rising. */
    val trend7d: Float
)

data class ImprovementAction(
    val id: String,
    val title: String,
    val description: String,
    val priority: Priority
)

data class Feedback(
    val authorType: AuthorType,
    val hospital: String,
    val department: String?,
    val rating: Int,      // 1..5
    val comment: String,
    val date: Long        // epoch millis
)

data class HospitalNeed(
    val hospital: String,
    val specialty: String,
    val needed: Int,
    val available: Int
)

data class HospitalShortage(
    val hospital: String,
    val item: String,
    val shortage: Int,
    val unit: String = "units"
)

data class MedicineDemand(
    val drugName: String,
    val demanded: Int,
    val fulfilled: Int,
    val hospital: String
)

data class WelfareKpi(
    val hospitalsReporting: Int,
    val totalActiveCases: Int,
    val bedOccupancy: Int,     // %
    val medicineFillRate: Int  // %
)

data class WelfareSnapshot(
    val area: Area,
    val lastUpdated: Long,
    val kpi: WelfareKpi,
    val topDiseases: List<DiseaseStat>,
    val actions: List<ImprovementAction>,
    val doctorNeeds: List<HospitalNeed>,
    val shortages: List<HospitalShortage>,
    val medicineDemands: List<MedicineDemand>,
    val feedback: List<Feedback>
)
