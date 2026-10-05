package com.example.telemedicine.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SymptomPredictorService {
    @POST("v1/predict")
    suspend fun predict(
        @Body request: SymptomPredictorRequest,
        @Header("X-Api-Key") apiKey: String? = null
    ): SymptomPredictorResponse
}
