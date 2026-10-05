package com.example.telemedicine.network

import com.example.telemedicine.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

object ServiceFactory {

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    /** Base OkHttp client with logging; optional extra auth interceptor. */
    private fun baseClient(extra: Interceptor? = null): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .apply { if (extra != null) addInterceptor(extra) }
            .build()

    /**
     * GEMINI: generativelanguage.googleapis.com
     * We pass the API key via @Query("key") in the Retrofit service (GeminiApi).
     * So no auth header is added here.
     */
    fun gemini(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(baseClient()) // no auth header; key sent as query param
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    /**
     * ELEVENLABS: api.elevenlabs.io
     * Adds xi-api-key header for STT/TTS.
     */
    fun elevenLabs(): Retrofit {
        val auth = Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("xi-api-key", BuildConfig.ELEVENLABS_API_KEY)
                    .build()
            )
        }
        return Retrofit.Builder()
            .baseUrl("https://api.elevenlabs.io/")
            .client(baseClient(auth))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
