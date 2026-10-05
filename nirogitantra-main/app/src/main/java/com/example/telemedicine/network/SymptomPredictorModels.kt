package com.example.telemedicine.network

/**
 * Example request payload:
 * {
 *   "symptoms": "fever, headache, cough",
 *   "language": "en"
 * }
 */
data class SymptomPredictorRequest(
    val symptoms: String,
    val language: String = "en"
)

data class SymptomPredictorCondition(
    val name: String,
    val probability: Double,
    val description: String,
    val advice: String? = null
)

/**
 * Example response payload:
 * {
 *   "conditions": [
 *     { "name": "Seasonal Influenza", "probability": 0.74, "description": "…" }
 *   ],
 *   "disclaimer": ""
 * }
 */
data class SymptomPredictorResponse(
    val conditions: List<SymptomPredictorCondition> = emptyList(),
    val disclaimer: String? = null
)

