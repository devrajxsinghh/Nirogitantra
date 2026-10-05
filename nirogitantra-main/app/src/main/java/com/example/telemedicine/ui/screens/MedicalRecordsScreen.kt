package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.LocalizedText
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.TopBarActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.width

private enum class RecordType { PRESCRIPTION, LAB_REPORT, DISCHARGE_SUMMARY }

private data class RecordFilterOption(val id: String, val label: LocalizedText)

private val RecordFilters = listOf(
    RecordFilterOption("all", LocalizedText("All", "Sabhi")),
    RecordFilterOption("prescription", LocalizedText("Prescriptions", "Nuskhay")),
    RecordFilterOption("lab", LocalizedText("Lab Reports", "Lab reports")),
    RecordFilterOption("discharge", LocalizedText("Discharge", "Chutti patra"))
)

private data class RecordItem(
    val title: LocalizedText,
    val type: RecordType,
    val date: String,
    val issuedBy: String,
    val sizeKb: Int
)

private val Records = listOf(
    RecordItem(
        title = LocalizedText("Prescription - Diabetes follow up", "Prescription - Diabetes follow up"),
        type = RecordType.PRESCRIPTION,
        date = "2025-09-10",
        issuedBy = "Dr. Kavya Jain",
        sizeKb = 220
    ),
    RecordItem(
        title = LocalizedText("CBC and Lipid Profile", "CBC aur lipid profile"),
        type = RecordType.LAB_REPORT,
        date = "2025-09-08",
        issuedBy = "Green Labs",
        sizeKb = 540
    ),
    RecordItem(
        title = LocalizedText("Chest X-ray report", "Seene ka X-ray report"),
        type = RecordType.LAB_REPORT,
        date = "2025-09-05",
        issuedBy = "City Imaging",
        sizeKb = 820
    ),
    RecordItem(
        title = LocalizedText("Discharge summary - Asthma", "Discharge summary - Asthma"),
        type = RecordType.DISCHARGE_SUMMARY,
        date = "2025-08-20",
        issuedBy = "City Health Center",
        sizeKb = 380
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalRecordsScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(RecordFilters.first().id) }

    val filteredItems = remember(query, selectedFilter) {
        Records.filter { record ->
            val matchesFilter = when (selectedFilter) {
                "all" -> true
                "prescription" -> record.type == RecordType.PRESCRIPTION
                "lab" -> record.type == RecordType.LAB_REPORT
                "discharge" -> record.type == RecordType.DISCHARGE_SUMMARY
                else -> true
            }
            val matchesQuery = query.isBlank() || record.title.get(AppLanguage.ENGLISH).contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedText(language, "Medical Records", "Medical records")) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(localizedText(language, "Search records", "Records khojen")) }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RecordFilters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter.id,
                        onClick = { selectedFilter = filter.id },
                        label = { Text(filter.label.get(language)) }
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredItems) { item ->
                    RecordCard(language = language, item = item)
                }
            }
        }
    }
}

@Composable
private fun RecordCard(language: AppLanguage, item: RecordItem) {
    val (icon, typeLabel) = when (item.type) {
        RecordType.PRESCRIPTION -> Icons.Outlined.Description to localizedText(language, "Prescription", "Prescription")
        RecordType.LAB_REPORT -> Icons.Outlined.PictureAsPdf to localizedText(language, "Lab Report", "Lab report")
        RecordType.DISCHARGE_SUMMARY -> Icons.Outlined.PictureAsPdf to localizedText(language, "Discharge Summary", "Discharge summary")
    }

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title.get(language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${item.issuedBy} - $typeLabel - ${item.date}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                AssistChip(onClick = {}, label = { Text("${item.sizeKb} KB") })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Visibility, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(localizedText(language, "View", "Dekhein"))
                }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.FileDownload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(localizedText(language, "Download", "Download karein"))
                }
                Button(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text(localizedText(language, "Share", "Sanjha karein"))
                }
            }
        }
    }
}

@Preview
@Composable
private fun MedicalRecordsScreenPreview() {
    MedicalRecordsScreen(
        language = AppLanguage.ENGLISH,
        onLanguageChange = {},
        onLogout = {}
    )
}
