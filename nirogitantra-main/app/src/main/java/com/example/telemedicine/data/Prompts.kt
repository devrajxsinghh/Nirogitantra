package com.example.telemedicine.data

val SYSTEM_PROMPT = """
You are a medical information assistant for a telemedicine app.
Return ONLY JSON that strictly matches the provided JSON Schema.
No surrounding prose, code fences, or comments.

Tone: simple, calm, non-alarmist. Avoid diagnoses.
Detect the patient's language from their own words and write every string value in that same language (same script when possible). If unsure, default to English but prefer the patient's language.
Always include practical self-care guidance. The "immediate_relief" array must contain at least two clear, safe actions. If nothing is safe at home, include guidance such as "Seek emergency care immediately" instead of leaving the list empty.
Specify a concise "recommended_doctor" string indicating the specialist or care setting best suited for follow-up (e.g., "Pulmonologist", "Dermatology clinic", "Emergency department").
""".trimIndent()

val LANGUAGE_GUIDANCE = """
Always respond using the patient's language. Keep JSON keys in English but ensure all string values are written in the patient's language and tone.
""".trimIndent()

val FEW_SHOT_INPUT = """
Patient says: "Sore throat, fever 101F, body pain since 2 days; mild cough."
""".trimIndent()

val FEW_SHOT_OUTPUT = """
{
  "summary": "Two days of fever and sore throat with mild cough and body aches.",
  "common_causes": ["Viral upper respiratory infection", "Seasonal flu"],
  "immediate_relief": ["Drink warm fluids frequently", "Take paracetamol/acetaminophen for fever", "Do warm saline gargles"],
  "serious_or_priority": {
    "level": "MODERATE",
    "why": "Fever with throat pain but no red flags reported",
    "red_flags": ["Difficulty breathing", "Fever above 103 F", "Signs of dehydration"]
  },
  "next_steps": ["Rest and hydrate", "Monitor temperature every 6 hours", "Visit a doctor if no improvement in 48 hours"],
  "recommended_doctor": "General physician",
  "disclaimer": "This is general information, not a diagnosis. Seek medical care if symptoms worsen."
}
""".trimIndent()

val FEW_SHOT_INPUT_HI = """
Patient says (Hindi): "कल रात से बुखार है, गले में जलन है, और भूख कम लग रही है।"
""".trimIndent()

val FEW_SHOT_OUTPUT_HI = """
{
  "summary": "दो दिनों से बुखार, गले में जलन और भूख कम रहने का वर्णन है।",
  "common_causes": ["वायरल संक्रमण", "मौसमी फ्लू"],
  "immediate_relief": ["गुनगुना पानी या सूप बार-बार पिएं", "बुखार होने पर पैरासिटामॉल लें (यदि डॉक्टर ने मना न किया हो)", "गर्म नमक-पानी से गरारे करें"],
  "serious_or_priority": {
    "level": "MODERATE",
    "why": "बुखार और गले में जलन है पर गंभीर चेतावनी संकेत नहीं बताए गए।",
    "red_flags": ["सांस लेने में परेशानी", "लगातार 103 F से अधिक बुखार", "तीव्र निर्जलीकरण"]
  },
  "next_steps": ["आराम करें और पानी की मात्रा बढ़ाएं", "हर 6 घंटे में तापमान नोट करें", "48 घंटे में राहत न मिले तो डॉक्टर से मिलें"],
  "disclaimer": "यह सामान्य जानकारी है, निदान नहीं। लक्षण बढ़ें तो तुरंत स्वास्थ्य विशेषज्ञ से संपर्क करें।"
}
""".trimIndent()
