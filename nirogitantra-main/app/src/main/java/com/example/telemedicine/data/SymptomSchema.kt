package com.example.telemedicine.data

// JSON Schema as a raw string for response_format.json_schema
val SYMPTOM_JSON_SCHEMA: String = """
{
  "type": "object",
  "properties": {
    "summary": { "type": "string" },
    "common_causes": { "type": "array", "items": { "type": "string" } },
    "immediate_relief": { "type": "array", "items": { "type": "string" } },
    "serious_or_priority": {
      "type": "object",
      "properties": {
        "level": { "type": "string", "enum": ["LOW","MODERATE","HIGH","EMERGENCY"] },
        "why": { "type": "string" },
        "red_flags": { "type": "array", "items": { "type": "string" } }
      },
      "required": ["level","why","red_flags"]
    },
    "next_steps": { "type": "array", "items": { "type": "string" } },
    "recommended_doctor": { "type": "string" },
    "disclaimer": { "type": "string" }
  },
  "required": ["summary","common_causes","immediate_relief","serious_or_priority","next_steps","recommended_doctor","disclaimer"],
  "additionalProperties": false
}
""".trimIndent()
