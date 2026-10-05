package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.LocalizedText
import com.example.telemedicine.data.doctorCallRequests
import com.example.telemedicine.data.doctorSchedules
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.LanguageToggle
import com.example.telemedicine.ui.components.LabeledValueRow
import com.example.telemedicine.ui.components.SectionCard
import com.example.telemedicine.ui.components.SectionHeader
import com.example.telemedicine.ui.components.StatusBadge
import androidx.compose.foundation.clickable

private enum class DoctorSection(val labelEn: String, val labelHi: String) {
    SCHEDULE("Schedule", "अनुसूची"),
    REQUESTS("Call Requests", "कॉल अनुरोध"),
    QUICK_CALL("Quick Call", "त्वरित कॉल")
}

private data class DoctorAppointmentUi(
    val patientName: String,
    val time: String,
    val duration: String,
    val consultationType: LocalizedText,
    val symptoms: LocalizedText,
    val status: LocalizedText
)

private data class DoctorActivityItem(
    val icon: ImageVector,
    val tint: Color,
    val title: String,
    val subtitle: String
)

private enum class CallDisposition {
    PENDING,
    ACCEPTED,
    REJECTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreen(
    language: AppLanguage,
    displayName: String,
    onBack: () -> Unit,
    onStartVideoCall: () -> Unit,
    onUploadPrescription: (String) -> Unit,
    onRecordVoiceNote: (String) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
    onOpenPatientDetail: (String) -> Unit
) {
    var currentSection by rememberSaveable { mutableStateOf(DoctorSection.SCHEDULE) }
    val requestStatuses = remember {
        mutableStateMapOf<Int, CallDisposition>().apply {
            doctorCallRequests.forEach { put(it.id, CallDisposition.PENDING) }
        }
    }

    Scaffold(
        topBar = {
            val name = if (displayName.isBlank()) localizedText(language, "Doctor", "डॉक्टर") else displayName
            LargeTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = localizedText(language, "Good morning, $name", "सुप्रभात, $name"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = localizedText(language, "General Medicine", "सामान्य चिकित्सा"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text(text = localizedText(language, "Logout", "लॉगआउट"), color = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                DoctorSection.values().forEach { section ->
                    NavigationBarItem(
                        selected = currentSection == section,
                        onClick = { currentSection = section },
                        icon = {
                            val icon = when (section) {
                                DoctorSection.SCHEDULE -> Icons.Outlined.EventAvailable
                                DoctorSection.REQUESTS -> Icons.Outlined.ListAlt
                                DoctorSection.QUICK_CALL -> Icons.Outlined.PlayCircle
                            }
                            Icon(imageVector = icon, contentDescription = null)
                        },
                        label = { Text(localizedText(language, section.labelEn, section.labelHi)) }
                    )
                }
            }
        }
    ) { padding ->
        when (currentSection) {
            DoctorSection.SCHEDULE -> DoctorScheduleContent(
                language = language,
                onLanguageChange = onLanguageChange,
                padding = padding,
                onStartVideoCall = onStartVideoCall,
                onOpenPatientDetail = onOpenPatientDetail
            )
            DoctorSection.REQUESTS -> DoctorRequestsContent(
                language = language,
                onLanguageChange = onLanguageChange,
                padding = padding,
                requestStatuses = requestStatuses,
                onStartVideoCall = onStartVideoCall,
                onUploadPrescription = onUploadPrescription,
                onRecordVoiceNote = onRecordVoiceNote
            )
            DoctorSection.QUICK_CALL -> DoctorQuickCallContent(language, onLanguageChange, padding, displayName, onStartVideoCall)
        }
    }
}

@Composable
private fun DoctorScheduleContent(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    padding: PaddingValues,
    onStartVideoCall: () -> Unit,
    onOpenPatientDetail: (String) -> Unit
) {
    val appointmentCards = doctorSchedules.mapIndexed { index, schedule ->
        val consultation = if (index % 2 == 0) {
            LocalizedText("Video consultation", "?????? ???????")
        } else {
            LocalizedText("Audio consultation", "????? ???????")
        }
        val symptoms = doctorCallRequests.getOrNull(index)?.concern ?: LocalizedText("General follow-up", "????-??")
        DoctorAppointmentUi(
            patientName = schedule.patientName,
            time = schedule.time,
            duration = "30 min",
            consultationType = consultation,
            symptoms = symptoms,
            status = LocalizedText("Scheduled", "?????????")
        )
    }

    val activityFeed = listOf(
        DoctorActivityItem(
            icon = Icons.Outlined.VideoCall,
            tint = MaterialTheme.colorScheme.primary,
            title = "Completed consultation",
            subtitle = "Patient #1234 - 2 hours ago"
        ),
        DoctorActivityItem(
            icon = Icons.Outlined.EventAvailable,
            tint = MaterialTheme.colorScheme.secondary,
            title = "New appointment booked",
            subtitle = "Patient #5678 - 4 hours ago"
        ),
        DoctorActivityItem(
            icon = Icons.Outlined.ListAlt,
            tint = MaterialTheme.colorScheme.tertiary,
            title = "Prescription sent",
            subtitle = "Patient #9012 - 6 hours ago"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            LanguageToggle(language = language, onLanguageChange = onLanguageChange)
        }

        item {
            SectionHeader(
                title = localizedText(language, "Today's appointments", "आज की अपॉइंटमेंट्स"),
                actionLabel = localizedText(language, "View all", "सभी देखें"),
                onAction = {}
            )
        }

        items(appointmentCards) { appointment ->
            DoctorAppointmentCard(
                language = language,
                appointment = appointment,
                onStartVideoCall = onStartVideoCall,
                onOpenPatientDetail = onOpenPatientDetail
            )
        }

        item {
            SectionCard(title = localizedText(language, "Recent activity", "हाल की गतिविधि")) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    activityFeed.forEach { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = entry.tint.copy(alpha = 0.15f),
                                tonalElevation = 0.dp
                            ) {
                                Icon(
                                    imageVector = entry.icon,
                                    contentDescription = null,
                                    tint = entry.tint,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = entry.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorAppointmentCard(
    language: AppLanguage,
    appointment: DoctorAppointmentUi,
    onStartVideoCall: () -> Unit,
    onOpenPatientDetail: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenPatientDetail(appointment.patientName) },
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = appointment.patientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${appointment.time} - ${appointment.duration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(text = appointment.status.get(language), color = MaterialTheme.colorScheme.secondary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.VideoCall, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(text = appointment.consultationType.get(language), style = MaterialTheme.typography.bodyMedium)
            }

            Text(
                text = localizedText(language, "Symptoms", "लक्षण") + ": " + appointment.symptoms.get(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onStartVideoCall) {
                    Text(localizedText(language, "Start consultation", "परामर्श शुरू करें"))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedIconButton(onClick = { /* message */ }) {
                        Icon(imageVector = Icons.Outlined.Chat, contentDescription = null)
                    }
                    OutlinedIconButton(onClick = { /* call */ }) {
                        Icon(imageVector = Icons.Outlined.Phone, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorRequestsContent(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    padding: PaddingValues,
    requestStatuses: MutableMap<Int, CallDisposition>,
    onStartVideoCall: () -> Unit,
    onUploadPrescription: (String) -> Unit,
    onRecordVoiceNote: (String) -> Unit
) {
    var expandedRequestId by rememberSaveable { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            LanguageToggle(language = language, onLanguageChange = onLanguageChange)
        }

        item {
            SectionHeader(title = localizedText(language, "Waiting patients", "प्रतीक्षारत रोगी"))
        }

        items(doctorCallRequests) { request ->
            val status = requestStatuses[request.id] ?: CallDisposition.PENDING
            val statusLabel = when (status) {
                CallDisposition.PENDING -> localizedText(language, "Pending", "लंबित")
                CallDisposition.ACCEPTED -> localizedText(language, "Accepted", "स्वीकृत")
                CallDisposition.REJECTED -> localizedText(language, "Rejected", "अस्वीकृत")
            }
            val statusColor = when (status) {
                CallDisposition.PENDING -> MaterialTheme.colorScheme.secondary
                CallDisposition.ACCEPTED -> MaterialTheme.colorScheme.primary
                CallDisposition.REJECTED -> MaterialTheme.colorScheme.error
            }
            val expanded = expandedRequestId == request.id

            SectionCard(
                title = request.patientName,
                modifier = Modifier.clickable { expandedRequestId = if (expanded) null else request.id }
            ) {
                StatusBadge(text = statusLabel, color = statusColor)
                LabeledValueRow(
                    label = localizedText(language, "Priority", "प्राथमिकता"),
                    value = request.priority.get(language)
                )
                Text(text = request.concern.get(language), style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            requestStatuses[request.id] = CallDisposition.ACCEPTED
                            onStartVideoCall()
                        },
                        enabled = status != CallDisposition.ACCEPTED
                    ) {
                        Text(localizedText(language, "Accept & call", "स्वीकारें और कॉल करें"))
                    }
                    OutlinedButton(
                        onClick = { requestStatuses[request.id] = CallDisposition.REJECTED },
                        enabled = status != CallDisposition.REJECTED
                    ) {
                        Text(localizedText(language, "Mark busy", "व्यस्त चिह्नित करें"))
                    }
                    if (status != CallDisposition.PENDING) {
                        TextButton(onClick = { requestStatuses[request.id] = CallDisposition.PENDING }) {
                            Text(localizedText(language, "Reset", "रीसेट"))
                        }
                    }
                }
                if (expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { onUploadPrescription(request.patientName) }) {
                            Text(localizedText(language, "Upload prescription", "प्रिस्क्रिप्शन अपलोड करें"))
                        }
                        OutlinedButton(onClick = { onRecordVoiceNote(request.patientName) }) {
                            Text(localizedText(language, "Record voice note", "वॉइस नोट रिकॉर्ड करें"))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorQuickCallContent(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    padding: PaddingValues,
    displayName: String,
    onStartVideoCall: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            LanguageToggle(language = language, onLanguageChange = onLanguageChange)
        }
        item {
            SectionCard(title = localizedText(language, "Live consultation room", "लाइव परामर्श कक्ष")) {
                Text(
                    text = localizedText(
                        language,
                        "You are visible as ${if (displayName.isBlank()) "Doctor" else displayName}. Share this code to invite patients.",
                        "?? ${if (displayName.isBlank()) "??????" else displayName} ?? ??? ??? ????? ?????? ?????? ?? ???????? ???? ?? ??? ?? ??? ???? ?????"
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = "CARE-2025",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(localizedText(language, "Room code", "कक्ष कोड")) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalIconButton(onClick = onStartVideoCall) {
                    Icon(imageVector = Icons.Outlined.PlayCircle, contentDescription = null)
                }
                Text(
                    text = localizedText(language, "Tap to enter the call room", "कॉल कक्ष में प्रवेश करने के लिए टैप करें"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
