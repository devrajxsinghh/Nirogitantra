package com.example.telemedicine.translations

import android.content.Context
import com.example.telemedicine.AppLanguage
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslatorManager(private val context: Context) {

    suspend fun translate(text: String, source: AppLanguage, target: AppLanguage): String {
        if (text.isBlank() || source == target) return text
        val sourceCode = source.mlKitCode ?: return text
        val targetCode = target.mlKitCode ?: return text

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceCode) ?: return text)
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetCode) ?: return text)
            .build()

        val translator: Translator = Translation.getClient(options)
        return try {
            translator.downloadModelIfNeeded().await()
            translator.translate(text).await()
        } catch (_: Exception) {
            text
        } finally {
            translator.close()
        }
    }
}