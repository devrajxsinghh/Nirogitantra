package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.LocalizedText
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.TopBarActions
import androidx.compose.foundation.layout.width

private data class SpecialtyOption(val id: String, val label: LocalizedText)

private val SpecialtyOptions = listOf(
    SpecialtyOption("all", LocalizedText("All", "Sabhi")),
    SpecialtyOption("general", LocalizedText("General Physician", "General chikitsak")),
    SpecialtyOption("pulmonology", LocalizedText("Pulmonology", "Fefde visheshagya")),
    SpecialtyOption("cardiology", LocalizedText("Cardiology", "Hriday rog vibhag")),
    SpecialtyOption("dermatology", LocalizedText("Dermatology", "Twacha vibhag")),
    SpecialtyOption("gynecology", LocalizedText("Gynecology", "Mahila rog vibhag"))
)

private data class DoctorMock(
    val name: String,
    val specialtyId: String,
    val rating: Float,
    val experienceYears: Int,
    val hospital: LocalizedText,
    val nextSlots: List<String>
)

private val Doctors = listOf(
    DoctorMock(
        name = "Dr. Kavya Jain",
        specialtyId = "general",
        rating = 4.8f,
        experienceYears = 10,
        hospital = LocalizedText("City Health Center", "City Health Center"),
        nextSlots = listOf("11:30 AM", "12:00 PM", "5:30 PM")
    ),
    DoctorMock(
        name = "Dr. Raghav Nair",
        specialtyId = "pulmonology",
        rating = 4.6f,
        experienceYears = 12,
        hospital = LocalizedText("Breath Well Clinic", "Breath Well Clinic"),
        nextSlots = listOf("2:00 PM", "3:45 PM")
    ),
    DoctorMock(
        name = "Dr. Ananya Bose",
        specialtyId = "dermatology",
        rating = 4.7f,
        experienceYears = 8,
        hospital = LocalizedText("DermaCare", "DermaCare"),
        nextSlots = listOf("1:15 PM", "4:30 PM")
    ),
    DoctorMock(
        name = "Dr. A. Chatterjee",
        specialtyId = "cardiology",
        rating = 4.5f,
        experienceYears = 15,
        hospital = LocalizedText("Heartline Hospital", "Heartline Hospital"),
        nextSlots = listOf("6:00 PM")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookVideoConsultScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    var selectedSpecialty by remember { mutableStateOf(SpecialtyOptions.first().id) }
    val filteredDoctors = remember(selectedSpecialty) {
        if (selectedSpecialty == "all") Doctors else Doctors.filter { it.specialtyId == selectedSpecialty }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedText(language, "Book Video Consultation", "Video salah book karein")) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(localizedText(language, "Specialties", "Visheshataein"), fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SpecialtyOptions) { option ->
                    FilterChip(
                        selected = selectedSpecialty == option.id,
                        onClick = { selectedSpecialty = option.id },
                        label = { Text(option.label.get(language)) }
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredDoctors) { doctor ->
                    DoctorCard(language = language, doctor = doctor)
                }
            }
        }
    }
}

@Composable
private fun DoctorCard(language: AppLanguage, doctor: DoctorMock) {
    val specialtyLabel = SpecialtyOptions.firstOrNull { it.id == doctor.specialtyId }?.label?.get(language)
        ?: doctor.specialtyId
    val experienceText = if (language == AppLanguage.HINDI) {
        "${doctor.experienceYears} varsh ka anubhav"
    } else {
        "${doctor.experienceYears} yrs exp"
    }
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(doctor.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "$specialtyLabel - $experienceText",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107))
                        Spacer(Modifier.width(4.dp))
                        Text(String.format("%.1f", doctor.rating), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Outlined.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            doctor.hospital.get(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(localizedText(language, "Next available", "Agla uplabdh samay"), fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(doctor.nextSlots) { slot ->
                    AssistChip(onClick = {}, label = { Text(slot) })
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text(localizedText(language, "View Profile", "Profile dekhein"))
                }
                Button(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text(localizedText(language, "Book", "Book karein"))
                }
            }
        }
    }
}

@Preview
@Composable
private fun BookVideoConsultScreenPreview() {
    BookVideoConsultScreen(
        language = AppLanguage.ENGLISH,
        onLanguageChange = {},
        onLogout = {}
    )
}
