package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.data.healthRecords
import com.example.telemedicine.data.patientCaseSummaryFor
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.SectionCard
import com.example.telemedicine.ui.components.TopBarActions
import androidx.compose.foundation.layout.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPatientDetailScreen(
    language: AppLanguage,
    patientName: String,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    val profile = patientCaseSummaryFor(patientName)
    val scroll = rememberScrollState()
    var prescription by rememberSaveable { mutableStateOf("") }
    var prescriptionSaved by rememberSaveable { mutableStateOf(false) }
    var isRecording by rememberSaveable { mutableStateOf(false) }
    var voiceNoteSaved by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedText(language, "Patient summary", "Patient sankshep")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
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
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = localizedText(language, "Patient snapshot", "Rogi snapshot")) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = localizedText(language, "Age ${profile.age} - ${profile.gender}", "Umar ${profile.age} - ${profile.gender}"),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (profile.chronicConditions.isNotEmpty()) {
                    Text(localizedText(language, "Chronic conditions", "Dirgh rog"), fontWeight = FontWeight.Medium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        profile.chronicConditions.forEach { condition ->
                            Text("- ${condition.get(language)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (profile.medications.isNotEmpty()) {
                    Text(localizedText(language, "Current medications", "Vartaman dawa"), fontWeight = FontWeight.Medium)
                    profile.medications.forEach { med ->
                        Text("- ${med.get(language)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            SectionCard(title = localizedText(language, "AI follow-up summary", "AI ka saar")) {
                Text(
                    text = profile.aiSummary.get(language),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionCard(title = localizedText(language, "Key vitals and labs", "Mukhya vitals aur labs")) {
                healthRecords.take(3).forEach { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(record.title.get(language), fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "${record.value} ${record.unit.get(language)}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = localizedText(language, "Recorded on ${record.recordedOn}", "Darj ${record.recordedOn} ko"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = record.note.get(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (profile.recentVisits.isNotEmpty()) {
                SectionCard(title = localizedText(language, "Recent consultations", "Hal ki salah")) {
                    profile.recentVisits.forEach { visit ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(visit.date, fontWeight = FontWeight.SemiBold)
                            Text(visit.concern.get(language), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(visit.outcome.get(language), style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            SectionCard(title = localizedText(language, "Current call actions", "Vartaman call ke kriya")) {
                Text(localizedText(language, "Add prescription notes", "Prescription note joden"), fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = prescription,
                    onValueChange = {
                        prescription = it
                        prescriptionSaved = false
                    },
                    placeholder = { Text(localizedText(language, "e.g., Continue Metformin at 6 PM after meals", "Udaharan: Metformin shaam 6 baje bhojan ke baad")) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        prescriptionSaved = prescription.isNotBlank()
                    }) {
                        Text(localizedText(language, "Save prescription", "Prescription save karein"))
                    }
                    if (prescriptionSaved) {
                        AssistChip(onClick = {}, label = { Text(localizedText(language, "Saved", "Surakshit")) })
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(localizedText(language, "Personalized voice note", "Vyaktigat voice note"), fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (!isRecording) {
                        OutlinedButton(onClick = {
                            isRecording = true
                            voiceNoteSaved = false
                        }) {
                            Icon(Icons.Outlined.Mic, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(localizedText(language, "Start recording", "Recording shuru karein"))
                        }
                    } else {
                        OutlinedButton(onClick = {
                            isRecording = false
                            voiceNoteSaved = true
                        }) {
                            Icon(Icons.Outlined.StopCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(localizedText(language, "Stop & attach", "Stop karke joden"))
                        }
                    }
                    if (voiceNoteSaved) {
                        AssistChip(onClick = {}, label = { Text(localizedText(language, "Voice note attached", "Voice note jud gaya")) })
                    }
                }
            }
        }
    }
}
