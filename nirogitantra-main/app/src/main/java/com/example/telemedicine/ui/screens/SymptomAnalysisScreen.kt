package com.example.telemedicine.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.data.SeverityLevel
import com.example.telemedicine.data.SymptomAiResult
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.TopBarActions
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomAnalysisScreen(
    language: AppLanguage,
    onConsult: () -> Unit = {},
    onLanguageChange: (AppLanguage) -> Unit = {},
    onLogout: () -> Unit = {},
    vm: SymptomAnalysisViewModel = viewModel(factory = SymptomAnalysisViewModel.Factory)
) {
    val state by vm.state.collectAsState()
    val audioPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) vm.startRecording()
    }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedText(language, "Symptom Analysis", "Lakshan vishleshan")) },
                actions = {
                    TopBarActions(
                        language = language,
                        onLanguageChange = onLanguageChange,
                        onLogout = onLogout
                    )
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(localizedText(language, "Type symptoms (optional)", "Lakshan likhen (chahe to)"), fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = state.manualInput,
                onValueChange = vm::updateManualInput,
                placeholder = {
                    Text(
                        localizedText(
                            language,
                            "e.g., Fever 101 F, sore throat for 2 days, mild cough",
                            "Udaharan: 101 F bukhar, 2 din se gala dard, halka khansi"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = vm::analyzeManual,
                    enabled = state.manualInput.isNotBlank() && !state.isAnalyzing
                ) {
                    Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        localizedText(
                            language,
                            if (state.isAnalyzing) "Analyzing..." else "Analyze Text",
                            if (state.isAnalyzing) "Visleshan ho raha hai..." else "Text ka visleshan"
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!state.isRecording) {
                    Button(onClick = { audioPerm.launch(Manifest.permission.RECORD_AUDIO) }) {
                        Icon(Icons.Outlined.Mic, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(localizedText(language, "Record", "Record karein"))
                    }
                } else {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        onClick = vm::stopAndTranscribe
                    ) {
                        Icon(Icons.Outlined.StopCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(localizedText(language, "Stop", "Rokein"))
                    }
                }

                Spacer(Modifier.weight(1f))

                OutlinedButton(
                    onClick = vm::analyze,
                    enabled = state.transcript.isNotBlank() && !state.isAnalyzing
                ) {
                    Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        localizedText(
                            language,
                            if (state.isAnalyzing) "Analyzing..." else "Analyze",
                            if (state.isAnalyzing) "Visleshan ho raha hai..." else "Visleshan karein"
                        )
                    )
                }
            }

            if (state.isTranscribing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (state.isAnalyzing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (state.transcript.isNotBlank()) {
                Text(localizedText(language, "Transcript", "Transcript"), fontWeight = FontWeight.SemiBold)
                Text(state.transcript, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            state.result?.let { res ->
                SeverityCard(language, res)

                SectionList(localizedText(language, "Summary", "Saar"), listOf(res.summary))
                SectionList(localizedText(language, "Common causes", "Samany karan"), res.commonCauses)
                SectionList(localizedText(language, "Immediate relief", "Turant rahat"), res.immediateRelief)
                SectionList(localizedText(language, "Red flags", "Chintajanak sanket"), res.seriousOrPriority.redFlags)
                SectionList(localizedText(language, "Next steps", "Agle kadam"), res.nextSteps)

                Text(localizedText(language, "Recommended doctor", "Suparishit doctor"), fontWeight = FontWeight.SemiBold)
                Text(res.recommendedDoctor, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(12.dp))
                Button(onClick = onConsult, modifier = Modifier.fillMaxWidth()) {
                    Text(localizedText(language, "Consult a doctor", "Doctor se salah lein"))
                }

                AssistChip(
                    onClick = {},
                    label = { Text(res.disclaimer) },
                    leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                )

                Button(onClick = vm::speak, modifier = Modifier.fillMaxWidth()) {
                    Text(localizedText(language, "Speak", "Sunen"))
                }
            }

            state.error?.let { msg ->
                Text(msg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun SectionList(title: String, items: List<String>) {
    Spacer(Modifier.height(6.dp))
    Text(title, fontWeight = FontWeight.SemiBold)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEach { Text("- $it") }
    }
}

@Composable
private fun SeverityCard(language: AppLanguage, result: SymptomAiResult) {
    val level = result.seriousOrPriority.level
    val (color, progress) = when (level) {
        SeverityLevel.LOW -> Color(0xFF16A34A) to 0.20f
        SeverityLevel.MODERATE -> Color(0xFFF59E0B) to 0.50f
        SeverityLevel.HIGH -> Color(0xFFEA580C) to 0.75f
        SeverityLevel.EMERGENCY -> Color(0xFFDC2626) to 1.0f
    }

    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(level.name) })
                Spacer(Modifier.width(12.dp))
                Text(
                    result.seriousOrPriority.why,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(), color = color)
            if (level == SeverityLevel.EMERGENCY) {
                Text(
                    localizedText(language, "Seek urgent care.", "Turant doctor se milen"),
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
