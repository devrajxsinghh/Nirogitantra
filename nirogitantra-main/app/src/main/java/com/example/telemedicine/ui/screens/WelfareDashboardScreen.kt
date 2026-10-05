package com.example.telemedicine.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.telemedicine.data.*
import kotlin.math.absoluteValue
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelfareDashboardScreen(
    viewModel: WelfareViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null
) {
    val ui = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welfare Dashboard") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    if (onExport != null) {
                        IconButton(onClick = onExport) {
                            Icon(Icons.Default.IosShare, contentDescription = "Export")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Area selector + last updated
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var expanded by remember { mutableStateOf(false) }
                val selected = ui.selectedArea

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selected?.name ?: "Select Area",
                        onValueChange = {},
                        label = { Text("Area") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ui.areas.forEach {
                            DropdownMenuItem(
                                text = { Text(it.name) },
                                onClick = {
                                    expanded = false
                                    viewModel.selectArea(it)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                val last = ui.snapshot?.lastUpdated
                Text(
                    text = last?.let {
                        "Updated " + DateUtils.getRelativeTimeSpanString(it)
                    } ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            if (ui.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            ui.snapshot?.let { snap ->
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.animateContentSize()
                ) {
                    item {
                        KpiRow(snap.kpi)
                    }

                    item {
                        SectionHeader("Diseases spreading in ${snap.area.name}", Icons.Default.MedicalServices)
                        DiseaseBars(snap.topDiseases)
                    }

                    item {
                        SectionHeader("Improvements to raise health quality", Icons.Default.TipsAndUpdates)
                        ActionsList(snap.actions)
                    }

                    item {
                        SectionHeader("Doctors needed by hospital", Icons.Default.Group)
                        NeedsList(snap.doctorNeeds)
                    }

                    item {
                        SectionHeader("Unmet medicine demand", Icons.Default.LocalPharmacy)
                        MedicineDemandList(snap.medicineDemands)
                    }

                    item {
                        SectionHeader("Doctor & Patient feedback", Icons.Default.Chat)
                    }

                    items(snap.feedback) { fb ->
                        FeedbackCard(fb)
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

/* -------------------  UI building blocks  ------------------- */

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun KpiRow(kpi: WelfareKpi) {
    KpiCard("Hospitals Reporting", kpi.hospitalsReporting.toString(), Icons.Default.Apartment, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun KpiCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun DiseaseBars(list: List<DiseaseStat>) {
    val maxActive = max(1, list.maxOf { it.active })
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        list.forEach { d ->
            ElevatedCard {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            d.disease,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.weight(1f)
                        )
                        TrendBadge(d.trend7d)
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Bar(
                            value = d.active.toFloat() / maxActive.toFloat(),
                            height = 10.dp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("${d.active} active • +${d.newToday} today", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun Bar(value: Float, height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(value.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun TrendBadge(trend: Float) {
    val isUp = trend >= 0f
    val bg = if (isUp) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
    val fg = if (isUp) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer
    val icon = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown
    AssistChip(
        onClick = {},
        label = { Text("${if (isUp) "+" else "-"}${trend.absoluteValue}%", color = fg) },
        leadingIcon = { Icon(icon, null, tint = fg, modifier = Modifier.size(16.dp)) },
        colors = AssistChipDefaults.assistChipColors(containerColor = bg)
    )
}

@Composable
private fun ActionsList(actions: List<ImprovementAction>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        actions.forEach { a ->
            ElevatedCard {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        PriorityDot(a.priority)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(a.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                            Spacer(Modifier.height(4.dp))
                            Text(a.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityDot(priority: Priority) {
    val color = when (priority) {
        Priority.CRITICAL -> MaterialTheme.colorScheme.error
        Priority.HIGH -> MaterialTheme.colorScheme.primary
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        Priority.LOW -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun NeedsList(needs: List<HospitalNeed>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        needs.forEach { n ->
            val gap = (n.needed - n.available).coerceAtLeast(0)
            ElevatedCard {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(n.hospital, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                        Text(n.specialty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Need +$gap", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun MedicineDemandList(items: List<MedicineDemand>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { m ->
            ElevatedCard {
                Column(Modifier.padding(12.dp)) {
                    Text(m.drugName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val fill = if (m.demanded == 0) 0f else m.fulfilled.toFloat() / m.demanded.toFloat()
                        Bar(value = fill, height = 8.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("${m.fulfilled}/${m.demanded}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "Requested at: ${m.hospital}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackCard(fb: Feedback) {
    ElevatedCard {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = if (fb.authorType == AuthorType.DOCTOR) Icons.Default.Badge else Icons.Default.Person
                Icon(icon, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    if (fb.authorType == AuthorType.DOCTOR) "Doctor feedback" else "Patient feedback",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.weight(1f))
                RatingStars(fb.rating)
            }
            Text("${fb.hospital}${fb.department?.let { " • $it" } ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                fb.comment,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RatingStars(rating: Int) {
    Row {
        repeat(5) { idx ->
            Icon(
                imageVector = if (idx < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (idx < rating) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
            )
        }
    }
}
