package com.example.telemedicine.data

import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.collections.buildList

private val responseSchema: JsonObject by lazy {
    buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
            putJsonObject("summary") { put("type", "STRING") }
            putJsonObject("common_causes") {
                put("type", "ARRAY")
                putJsonObject("items") { put("type", "STRING") }
            }
            putJsonObject("immediate_relief") {
                put("type", "ARRAY")
                putJsonObject("items") { put("type", "STRING") }
            }
            putJsonObject("serious_or_priority") {
                put("type", "OBJECT")
                putJsonObject("properties") {
                    putJsonObject("level") {
                        put("type", "STRING")
                        putJsonArray("enum") {
                            add("LOW")
                            add("MODERATE")
                            add("HIGH")
                            add("EMERGENCY")
                        }
                    }
                    putJsonObject("why") { put("type", "STRING") }
                    putJsonObject("red_flags") {
                        put("type", "ARRAY")
                        putJsonObject("items") { put("type", "STRING") }
                    }
                }
                putJsonArray("required") {
                    add("level")
                    add("why")
                    add("red_flags")
                }
            }
            putJsonObject("next_steps") {
                put("type", "ARRAY")
                putJsonObject("items") { put("type", "STRING") }
            }
            putJsonObject("recommended_doctor") { put("type", "STRING") }
            putJsonObject("disclaimer") { put("type", "STRING") }
        }
        putJsonArray("required") {
            add("summary")
            add("common_causes")
            add("immediate_relief")
            add("serious_or_priority")
            add("next_steps")
            add("recommended_doctor")
            add("disclaimer")
        }
    }
}

private val systemInstructionText: String by lazy {
    buildString {
        appendLine(SYSTEM_PROMPT)
        appendLine()
        appendLine("Schema:")
        append(SYMPTOM_JSON_SCHEMA)
    }
}

private fun JsonArrayBuilder.addMessage(role: String, text: String) {
    if (text.isBlank()) return
    addJsonObject {
        put("role", role)
        putJsonArray("parts") {
            addJsonObject { put("text", text) }
        }
    }
}

private fun baseGeminiRequest(conversation: List<Pair<String, String>>): JsonObject =
    buildJsonObject {
        putJsonObject("system_instruction") {
            putJsonArray("parts") {
                addJsonObject { put("text", systemInstructionText) }
            }
        }
        putJsonObject("generation_config") {
            put("temperature", 0.2)
            put("response_mime_type", "application/json")
            put("response_schema", responseSchema)
        }
        putJsonArray("contents") {
            addMessage("user", LANGUAGE_GUIDANCE)
            addMessage("user", FEW_SHOT_INPUT)
            addMessage("model", FEW_SHOT_OUTPUT)
            addMessage("user", FEW_SHOT_INPUT_HI)
            addMessage("model", FEW_SHOT_OUTPUT_HI)
            conversation.forEach { (role, text) -> addMessage(role, text) }
        }
    }

fun buildGeminiBody(transcript: String, languageHint: String? = null): JsonObject =
    baseGeminiRequest(
        conversation = buildList {
            languageHint?.takeIf { it.isNotBlank() }?.let {
                add("user" to "Language hint: $it. Reply entirely in this language and keep the tone calm and reassuring.")
            }
            add("user" to "Return recommended_doctor as the type of specialist or care setting best suited for follow-up (for example, Pulmonologist, Dermatology clinic, Emergency department).")
            add("user" to "Patient says: \"$transcript\"")
        }
    )

fun buildGeminiRetryBody(
    lastError: String,
    transcript: String,
    languageHint: String? = null,
    previousOutput: String? = null
): JsonObject =
    baseGeminiRequest(
        conversation = buildList {
            languageHint?.takeIf { it.isNotBlank() }?.let {
                add("user" to "Language hint: $it. Reply entirely in this language and keep the tone calm and reassuring.")
            }
            add("user" to "Previous JSON issue: $lastError. Fix the JSON to match the schema, ensure immediate_relief has at least two actionable items, and populate recommended_doctor with a clear specialist suggestion.")
            previousOutput?.takeIf { it.isNotBlank() }?.let { add("model" to it) }
            add("user" to "Patient says: \"$transcript\"")
        }
    )
