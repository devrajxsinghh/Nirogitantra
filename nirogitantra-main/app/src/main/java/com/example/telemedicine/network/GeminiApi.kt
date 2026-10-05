package com.example.telemedicine.network

import com.example.telemedicine.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


interface GeminiApi {
    // Switched to v1beta to support structured output and system_instruction
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    @Headers("Content-Type: application/json")
    suspend fun generate(
        @Query("key") key: String = BuildConfig.GEMINI_API_KEY,
        @Body body: JsonObject
    ): Response<GeminiResp>
}

@Serializable data class GeminiResp(val candidates: List<Candidate> = emptyList())
@Serializable data class Candidate(val content: Content? = null)
@Serializable data class Content(val parts: List<Part> = emptyList())
@Serializable data class Part(val text: String? = null)

object GeminiFactory {
    fun create(): GeminiApi {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiApi::class.java)
    }
}
