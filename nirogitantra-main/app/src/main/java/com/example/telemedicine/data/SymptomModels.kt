package com.example.telemedicine.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SeverityLevel { LOW, MODERATE, HIGH, EMERGENCY }

@Serializable
data class SeriousOrPriority(
    val level: SeverityLevel,
    val why: String,
    @SerialName("red_flags") val redFlags: List<String>
)

@Serializable
data class SymptomAiResult(
    val summary: String,
    @SerialName("common_causes") val commonCauses: List<String>,
    @SerialName("immediate_relief") val immediateRelief: List<String>,
    @SerialName("serious_or_priority") val seriousOrPriority: SeriousOrPriority,
    @SerialName("next_steps") val nextSteps: List<String>,
    @SerialName("recommended_doctor") val recommendedDoctor: String,
    val disclaimer: String
)
