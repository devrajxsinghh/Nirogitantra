package com.example.telemedicine.network

import retrofit2.converter.gson.GsonConverterFactory
import com.example.telemedicine.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

class SymptomPredictorRepository private constructor(
    private val interceptorFactory: () -> List<Interceptor>,
    baseUrl: String,
    apiKey: String?
) {

    private val defaultBaseUrl: String = sanitizeBaseUrl(BuildConfig.SYMPTOM_PREDICTOR_BASE_URL)
    private val defaultApiKey: String? = BuildConfig.SYMPTOM_PREDICTOR_API_KEY.takeIf { it.isNotBlank() }

    @Volatile
    private var currentBaseUrl: String = sanitizeBaseUrl(baseUrl).ifBlank { defaultBaseUrl }

    @Volatile
    private var currentApiKey: String? = apiKey?.takeIf { it.isNotBlank() } ?: defaultApiKey

    @Volatile
    private var currentService: SymptomPredictorService = createService(currentBaseUrl)

    suspend fun predict(symptomText: String): SymptomPredictorResponse = withContext(Dispatchers.IO) {
        if (symptomText.isBlank()) {
            SymptomPredictorResponse(emptyList())
        } else {
            delay(450)
            val service = currentService
            val apiKeyHeader = currentApiKey
            service.predict(
                request = SymptomPredictorRequest(symptoms = symptomText),
                apiKey = apiKeyHeader
            )
        }
    }

    fun updateConfiguration(baseUrl: String?, apiKey: String?) {
        val sanitisedBase = sanitizeBaseUrl(baseUrl).ifBlank { defaultBaseUrl }
        val sanitisedKey = apiKey?.takeIf { it.isNotBlank() } ?: defaultApiKey

        synchronized(this) {
            if (sanitisedBase != currentBaseUrl) {
                currentBaseUrl = sanitisedBase
                currentService = createService(currentBaseUrl)
            } else {
                currentBaseUrl = sanitisedBase
            }
            currentApiKey = sanitisedKey
        }
    }

    fun getConfiguration(): SymptomPredictorConfiguration = SymptomPredictorConfiguration(
        baseUrl = currentBaseUrl,
        apiKey = currentApiKey
    )

    private fun createService(baseUrl: String): SymptomPredictorService {
        val clientBuilder = OkHttpClient.Builder()
        interceptorFactory().forEach { clientBuilder.addInterceptor(it) }

        val retrofit = Retrofit.Builder()
            .baseUrl(sanitizeBaseUrl(baseUrl).ifBlank { defaultBaseUrl })
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        return retrofit.create(SymptomPredictorService::class.java)
    }

    companion object {
        fun create(): SymptomPredictorRepository {
            val defaultBase = sanitizeBaseUrl(BuildConfig.SYMPTOM_PREDICTOR_BASE_URL)
            val defaultKey = BuildConfig.SYMPTOM_PREDICTOR_API_KEY.takeIf { it.isNotBlank() }
            return SymptomPredictorRepository(
                interceptorFactory = { listOf(MockSymptomPredictorInterceptor()) },
                baseUrl = defaultBase,
                apiKey = defaultKey
            )
        }

        private fun sanitizeBaseUrl(raw: String?): String {
            if (raw.isNullOrBlank()) return ""
            val trimmed = raw.trim()
            return if (trimmed.endsWith('/')) trimmed else "$trimmed/"
        }
    }
}

data class SymptomPredictorConfiguration(
    val baseUrl: String,
    val apiKey: String?
)

private class MockSymptomPredictorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath.endsWith("/v1/predict")) {
            val mockJson = """
                {
                  "conditions": [
                    {
                      "name": "Seasonal Influenza",
                      "probability": 0.74,
                      "description": "Fever with chills, body ache, and sore throat typically caused by a seasonal virus.",
                      "advice": "Rest, hydrate, and consider an over-the-counter paracetamol tablet if fever crosses 100 F."
                    },
                    {
                      "name": "Viral Fever",
                      "probability": 0.61,
                      "description": "Low grade fever with mild fatigue; monitor temperature twice a day.",
                      "advice": "Keep a log of fever readings and seek in-person care if it persists beyond 72 hours."
                    },
                    {
                      "name": "Migraine",
                      "probability": 0.38,
                      "description": "Recurring headache with sound or light sensitivity.",
                      "advice": "Avoid loud spaces, stay hydrated, and review triggers (missed meals, sleep loss)."
                    }
                  ],
                  "disclaimer": "Mock data for hackathon demo. Replace interceptor with live API integration."
                }
            """.trimIndent()

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(mockJson.toResponseBody("application/json".toMediaType()))
                .build()
        }
        return chain.proceed(request)
    }
}




