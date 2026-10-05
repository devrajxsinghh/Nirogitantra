package com.example.telemedicine

import android.icu.text.Transliterator
import java.util.Locale

private val DEVANAGARI_RANGE = Regex("[\u0900-\u097F]")

private fun ensureHindiScript(source: String): String {
    if (source.isBlank()) return source
    if (DEVANAGARI_RANGE.containsMatchIn(source)) return source
    return runCatching {
        Transliterator.getInstance("Latin-Devanagari").transliterate(source)
    }.getOrElse { source }
}

enum class AppLanguage(val displayName: String, val locale: Locale, val mlKitCode: String?) {
    ENGLISH("English", Locale.ENGLISH, "en"),
    HINDI("हिन्दी", Locale("hi", "IN"), "hi"),
    BENGALI("বাংলা", Locale("bn", "IN"), "bn"),
    MARATHI("मराठी", Locale("mr", "IN"), "mr"),
    GUJARATI("ગુજરાતી", Locale("gu", "IN"), "gu")
}

data class LocalizedText(
    val english: String,
    val hindi: String
) {
    fun get(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> english
        AppLanguage.HINDI -> ensureHindiScript(if (hindi.isNotBlank()) hindi else english)
        else -> if (hindi.isNotBlank()) hindi else english
    }
}

fun AppLanguage.toggle(): AppLanguage = if (this == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH

fun localizedText(language: AppLanguage, english: String, hindi: String): String = when (language) {
    AppLanguage.ENGLISH -> english
    AppLanguage.HINDI -> ensureHindiScript(if (hindi.isNotBlank()) hindi else english)
    else -> hindi.ifBlank { english }
}
