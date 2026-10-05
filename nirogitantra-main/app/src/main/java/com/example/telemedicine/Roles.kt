package com.example.telemedicine

enum class UserRole {
    PATIENT,
    DOCTOR,
    WORKER,
    WELFARE
}

fun UserRole.navRoute(): String = when (this) {
    UserRole.PATIENT -> "patientHome"
    UserRole.DOCTOR -> "doctorHome"
    UserRole.WORKER -> "workerHome"
    UserRole.WELFARE -> "welfareDashboard"
}
