package com.example.telemedicine.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.localizedText

// Extended icons (needs material-icons-extended)
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Emergency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    language: AppLanguage,
    displayName: String,
    onBack: () -> Unit,
    onStartVideoCall: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
    onOpenSymptom: () -> Unit,
    onOpenConsult: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenOrder: () -> Unit
) {
    val scroll = rememberScrollState()
    val nextLanguage = if (language == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH

    // --- helpers for Call + Maps ---
    val context = LocalContext.current
    val dial: (String) -> Unit = { number ->
        runCatching {
            val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            context.startActivity(i)
        }
    }
    val openMap: (Double?, Double?, String) -> Unit = { lat, lng, query ->
        runCatching {
            val uri = if (lat != null && lng != null)
                Uri.parse("geo:$lat,$lng?q=${Uri.encode(query)}")
            else
                Uri.parse("geo:0,0?q=${Uri.encode(query)}")
            val i = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(i)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } },
                actions = {
                    TextButton(onClick = { onLanguageChange(nextLanguage) }) { Text(localizedText(language, "Language", "भाषा")) }
                    TextButton(onClick = onLogout) { Text(localizedText(language, "Logout", "लॉगआउट")) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .verticalScroll(scroll)
                .fillMaxWidth()
        ) {
            HeaderCard(
                language = language,
                displayName = displayName,
                locationLine = localizedText(language, "Zjcndsnf, Dhcfis", "Zjcndsnf, Dhcfis"),
                onEmergency = { dial("108") } // Dials Ambulance directly
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = localizedText(language, "Quick Actions", "Quick Actions"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(8.dp))
            QuickActionGrid(
                language = language,
                onOpenSymptom = onOpenSymptom,
                onOpenConsult = onOpenConsult,
                onOpenRecords = onOpenRecords,
                onOpenOrder = onOpenOrder
            )

            Spacer(Modifier.height(16.dp))
            FeaturedHealthTipCard(
                title = "Stay Hydrated",
                category = "General",
                importance = "Important",
                body = "Drink at least 8–10 glasses of clean water daily, especially during hot weather. Boil water if you are unsure about its purity.",
                onViewAll = { /* TODO */ }
            )

            // --- Medicine Schedule (moved up, before Jan Aushadhi + Emergency) ---
            Spacer(Modifier.height(16.dp))
            SectionCard("Medicine Schedule", Icons.Outlined.AccessTime) {
                val entries = listOf(
                    "08:00 AM • Metformin 500mg • After breakfast",
                    "02:00 PM • Vitamin D Sachet • Mix with water",
                    "09:00 PM • Telmisartan 40mg • Before bed"
                )
                entries.forEachIndexed { i, item ->
                    Column {
                        Text(item, style = MaterialTheme.typography.bodyMedium)
                        if (i < entries.lastIndex) { Spacer(Modifier.height(6.dp)); Divider() }
                    }
                }
            }

            // --- Nearest Jan Aushadhi (mock) ---
            Spacer(Modifier.height(16.dp))
            SectionCard(
                title = localizedText(language, "Nearest Jan Aushadhi", "नज़दीकी जन औषधि"),
                icon = Icons.Outlined.LocalPharmacy
            ) {
                val kendras = listOf(
                    Kendra(
                        name = "PMBJP Kendra – Green Park",
                        address = "Shop 12, Main Market, Green Park, New Delhi",
                        distanceKm = 1.2,
                        phone = "9876543210", // mock
                        hours = "8:00 AM – 9:00 PM",
                        lat = 28.553, lng = 77.206
                    ),
                    Kendra(
                        name = "PMBJP Kendra – Lajpat Nagar",
                        address = "E-Block, Central Market, Lajpat Nagar II, New Delhi",
                        distanceKm = 2.4,
                        phone = "9890011223", // mock
                        hours = "9:00 AM – 8:30 PM",
                        lat = 28.568, lng = 77.243
                    ),
                    Kendra(
                        name = "PMBJP Kendra – Sarita Vihar",
                        address = "Shop 7, DDA Complex, Sarita Vihar, New Delhi",
                        distanceKm = 3.1,
                        phone = "9812345678", // mock
                        hours = "8:30 AM – 9:30 PM",
                        lat = 28.532, lng = 77.295
                    )
                )
                JanAushadhiList(
                    kendras = kendras,
                    onCall = dial,
                    onDirections = openMap
                )
            }

            // --- Nearby Free Camps / Drives (mock) ---
            Spacer(Modifier.height(16.dp))
            SectionCard(
                title = localizedText(language, "Nearby Free Camps / Drives", "नज़दीकी नि:शुल्क शिविर / ड्राइव"),
                icon = Icons.Outlined.Event
            ) {
                val camps = listOf(
                    HealthCamp(
                        title = "Free Blood Sugar & BP Check-up",
                        date = "Sat, 27 Sep",
                        time = "9:00 AM – 1:00 PM",
                        location = "Community Centre, Jangpura",
                        organizer = "Delhi Health Dept.",
                        phone = "1800123000", // mock
                        lat = 28.584, lng = 77.242
                    ),
                    HealthCamp(
                        title = "Eye Screening & Spectacle Distribution",
                        date = "Sun, 28 Sep",
                        time = "10:00 AM – 3:00 PM",
                        location = "SDMC Hall, Green Park",
                        organizer = "Govt. Dispensary Program",
                        phone = "1800456000", // mock
                        lat = 28.554, lng = 77.205
                    ),
                    HealthCamp(
                        title = "Blood Donation Drive",
                        date = "Wed, 1 Oct",
                        time = "10:00 AM – 2:00 PM",
                        location = "MCD Community Hall, Lajpat Nagar",
                        organizer = "State Blood Transfusion Council",
                        phone = "1800789000", // mock
                        lat = 28.567, lng = 77.242
                    )
                )
                FreeCampsList(
                    camps = camps,
                    onCall = dial,
                    onDirections = openMap
                )
            }

            // --- Emergency: single red button to dial 108 ---
            Spacer(Modifier.height(16.dp))
            SectionCard(
                title = localizedText(language, "Emergency", "आपातकाल"),
                icon = Icons.Outlined.Emergency
            ) {
                Button(
                    onClick = { dial("108") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Emergency, null)
                    Spacer(Modifier.width(8.dp))
                    Text(localizedText(language, "Call Ambulance (108)", "एंबुलेंस (108) कॉल करें"))
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionCard("Recent Activities", Icons.Outlined.HealthAndSafety) {
                val entries = listOf(
                    "Prescription updated — 2 hours ago",
                    "Video consultation completed — Yesterday",
                    "Lab reports uploaded — 3 days ago"
                )
                entries.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }

            Spacer(Modifier.height(16.dp))
            SectionCard("Generic Health Tips", Icons.Outlined.MedicalServices) {
                val tips = listOf(
                    "Take a short walk after meals.",
                    "Track blood pressure twice a week.",
                    "Sleep 7–8 hours to boost immunity."
                )
                tips.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.WaterDrop, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("• $it", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionCard("Your Care Team", Icons.Outlined.People) {
                CareTeamList(
                    listOf(
                        CareMember("Dr. Kavya Jain", "General Physician", 4.8f),
                        CareMember("Dr. Raghav Nair", "Pulmonologist", 4.6f),
                        CareMember("Nurse Vaishali", "Care Coordinator", 4.9f)
                    )
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* ----------------- Header + Quick Actions + Cards + Care Team ---------------- */

@Composable
private fun HeaderCard(
    language: AppLanguage,
    displayName: String,
    locationLine: String,
    onEmergency: () -> Unit
) {
    val greeting = remember { "Good Evening" }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("$greeting, $displayName", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(locationLine, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            }
            Surface(color = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError, shape = CircleShape) {
                IconButton(onClick = onEmergency, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Emergency, contentDescription = "Emergency")
                }
            }
        }
    }
}

@Composable
private fun QuickActionGrid(
    language: AppLanguage,
    onOpenSymptom: () -> Unit,
    onOpenConsult: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenOrder: () -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionItem(
                title = "लक्षण जाँचें",
                subtitle = "AI-powered analysis",
                icon = Icons.Outlined.Search,
                tint = Color(0xFF16A34A),
                onClick = onOpenSymptom,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                title = "परामर्श बुक करें",
                subtitle = "Talk to a doctor",
                icon = Icons.Outlined.Videocam,
                tint = Color(0xFF2563EB),
                onClick = onOpenConsult,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionItem(
                title = "रिकॉर्ड देखें",
                subtitle = "Your health history",
                icon = Icons.Outlined.Description,
                tint = Color(0xFF7C3AED),
                onClick = onOpenRecords,
                modifier = Modifier.weight(1f)
            )
            QuickActionItem(
                title = "दवा ऑर्डर करें",
                subtitle = "From Jan Aushadhi",
                icon = Icons.Outlined.LocalPharmacy,
                tint = Color(0xFFF59E0B),
                onClick = onOpenOrder,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = tint) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FeaturedHealthTipCard(
    title: String, category: String, importance: String, body: String, onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.WaterDrop, null, tint = MaterialTheme.colorScheme.primary) }
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Pill(importance, MaterialTheme.colorScheme.error.copy(alpha = 0.15f), MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            Pill(category, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onViewAll, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.MenuBook, null); Spacer(Modifier.width(8.dp)); Text("View All Health Tips"); Spacer(Modifier.weight(1f)); Icon(Icons.Outlined.ChevronRight, null)
            }
        }
    }
}

@Composable private fun Pill(text: String, bg: Color, fg: Color) {
    Surface(shape = RoundedCornerShape(50), color = bg, contentColor = fg) {
        Text(text, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

private data class CareMember(val name: String, val specialization: String, val rating: Float)

@Composable private fun CareTeamList(members: List<CareMember>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        members.forEach { m ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.MedicalServices, null, tint = MaterialTheme.colorScheme.primary) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(m.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(m.specialization, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107)); Spacer(Modifier.width(4.dp))
                        Text(String.format("%.1f", m.rating), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Divider()
        }
    }
}

/* ----------------- Jan Aushadhi + Camps UI ---------------- */

private data class Kendra(
    val name: String,
    val address: String,
    val distanceKm: Double,
    val phone: String,
    val hours: String,
    val lat: Double? = null,
    val lng: Double? = null
)

@Composable
private fun JanAushadhiList(
    kendras: List<Kendra>,
    onCall: (String) -> Unit,
    onDirections: (Double?, Double?, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        kendras.forEachIndexed { index, k ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Outlined.LocalPharmacy, null, tint = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(k.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(k.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(
                            String.format("%.1f km away • %s", k.distanceKm, k.hours),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onCall(k.phone) }) {
                        Icon(Icons.Outlined.Phone, null); Spacer(Modifier.width(6.dp)); Text("Call")
                    }
                    TextButton(onClick = { onDirections(k.lat, k.lng, k.address) }) {
                        Icon(Icons.Outlined.Place, null); Spacer(Modifier.width(6.dp)); Text("Directions")
                    }
                }
                if (index < kendras.lastIndex) { Spacer(Modifier.height(12.dp)); Divider() }
            }
        }
    }
}

private data class HealthCamp(
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val organizer: String,
    val phone: String,
    val lat: Double? = null,
    val lng: Double? = null
)

@Composable
private fun FreeCampsList(
    camps: List<HealthCamp>,
    onCall: (String) -> Unit,
    onDirections: (Double?, Double?, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        camps.forEachIndexed { index, c ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Outlined.Event, null, tint = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text("${c.date} • ${c.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(c.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Organizer: ${c.organizer}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onCall(c.phone) }) {
                        Icon(Icons.Outlined.Phone, null); Spacer(Modifier.width(6.dp)); Text("Call")
                    }
                    TextButton(onClick = { onDirections(c.lat, c.lng, c.location) }) {
                        Icon(Icons.Outlined.Place, null); Spacer(Modifier.width(6.dp)); Text("Directions")
                    }
                }
                if (index < camps.lastIndex) { Spacer(Modifier.height(12.dp)); Divider() }
            }
        }
    }
}

/* ----------------- Preview ---------------- */

@Preview(showBackground = true)
@Composable
private fun PatientHomeScreenPreview() {
    PatientHomeScreen(
        language = AppLanguage.ENGLISH,
        displayName = "Snndvnxj",
        onBack = {},
        onStartVideoCall = {},
        onLanguageChange = {},
        onLogout = {},
        onOpenSymptom = {},
        onOpenConsult = {},
        onOpenRecords = {},
        onOpenOrder = {}
    )
}
