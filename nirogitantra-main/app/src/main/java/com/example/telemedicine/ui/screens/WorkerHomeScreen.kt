package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.LocalizedText
import com.example.telemedicine.data.MedicineOrder
import com.example.telemedicine.data.MedicineOrderItem
import com.example.telemedicine.data.workerOrders
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.LanguageToggle
import com.example.telemedicine.ui.components.LabeledValueRow
import com.example.telemedicine.ui.components.SectionCard
import com.example.telemedicine.ui.components.SectionHeader
import com.example.telemedicine.ui.components.StatusBadge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerHomeScreen(
    language: AppLanguage,
    displayName: String,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    val statusOptions = listOf(
        LocalizedText("Received", "???????"),
        LocalizedText("Ready for pickup", "????? ?? ??? ?????"),
        LocalizedText("Out for delivery", "??????? ?? ??? ?????"),
        LocalizedText("Delivered", "?????? ???? ???")
    )

    val orderStatuses = remember {
        mutableStateMapOf<Int, Int>().apply {
            workerOrders.forEach { order ->
                val index = statusOptions.indexOfFirst { it.english == order.status.english }
                this[order.id] = if (index >= 0) index else 0
            }
        }
    }

    val orderFulfilment = remember {
        mutableStateMapOf<Pair<Int, Int>, Int>().apply {
            workerOrders.forEach { order ->
                order.items.forEach { item ->
                    val defaultReady = item.availableQuantity.coerceAtMost(item.requestedQuantity)
                    this[order.id to item.id] = defaultReady
                }
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val topBarColors = TopAppBarDefaults.largeTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = Color.White,
        navigationIconContentColor = Color.White,
        actionIconContentColor = Color.White
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val workerName = if (displayName.isBlank()) {
                localizedText(language, "Health worker", "स्वास्थ्य कार्यकर्ता")
            } else displayName

            LargeTopAppBar(
                colors = topBarColors,
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = localizedText(language, "Hello, $workerName", "नमस्ते, $workerName"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = localizedText(language, "Jan Aushadi desk", "जन औषधि डेस्क"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    StatusBadge(
                        text = localizedText(language, "On duty", "कर्तव्य पर"),
                        color = Color.White,
                        backgroundColor = Color.White.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(onClick = onLogout) {
                        Text(text = if (language == AppLanguage.HINDI) "लॉगआउट" else "Logout", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->
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
                SummaryCard(
                    language = language,
                    orderStatuses = orderStatuses,
                    orderFulfilment = orderFulfilment
                )
            }

            item {
                SectionHeader(title = localizedText(language, "Today's orders", "आज के ऑर्डर"))
            }

            items(workerOrders) { order ->
                OrderCard(
                    language = language,
                    order = order,
                    statusOptions = statusOptions,
                    currentStatusIndex = orderStatuses[order.id] ?: 0,
                    onStatusChange = { orderStatuses[order.id] = it },
                    orderFulfilment = orderFulfilment,
                    onUpdateItem = { itemId, newQuantity ->
                        val key = order.id to itemId
                        orderFulfilment[key] = newQuantity
                    },
                    onSendUpdate = { ready, requested ->
                        val message = localizedText(
                            language,
                            "Sent update to ${order.customerName}: $ready of $requested units ready",
                            "${order.customerName} ?? ????? ????: $ready ??? ?? $requested ????? ?????"
                        )
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OrderCard(
    language: AppLanguage,
    order: MedicineOrder,
    statusOptions: List<LocalizedText>,
    currentStatusIndex: Int,
    onStatusChange: (Int) -> Unit,
    orderFulfilment: MutableMap<Pair<Int, Int>, Int>,
    onUpdateItem: (Int, Int) -> Unit,
    onSendUpdate: (Int, Int) -> Unit
) {
    val requestedUnits = order.items.sumOf { it.requestedQuantity }
    val readyUnits = order.items.sumOf { item ->
        val key = order.id to item.id
        orderFulfilment[key] ?: item.availableQuantity.coerceAtMost(item.requestedQuantity)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CardDefaults.shape,
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = order.customerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = localizedText(language, "Order #${order.id.toString().padStart(3, '0')}", "ऑर्डर #${order.id.toString().padStart(3, '0')}"), // <<< CORRECTED: Removed extra parenthesis
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(
                    text = statusOptions[currentStatusIndex].get(language),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            LabeledValueRow(
                label = localizedText(language, "Ready units", "तैयार इकाइयाँ"),
                value = "$readyUnits / $requestedUnits"
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                order.items.forEach { item ->
                    val key = order.id to item.id
                    val currentReady = (orderFulfilment[key]
                        ?: item.availableQuantity.coerceAtMost(item.requestedQuantity))
                        .coerceIn(0, item.requestedQuantity)
                    orderFulfilment[key] = currentReady

                    OrderItemRow(
                        language = language,
                        item = item,
                        readyQuantity = currentReady,
                        onQuantityChange = { newValue ->
                            val safeValue = newValue.coerceIn(0, item.requestedQuantity)
                            onUpdateItem(item.id, safeValue)
                        }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = localizedText(language, "Update order status", "ऑर्डर की स्थिति अपडेट करें"), style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEachIndexed { index, option ->
                        val isSelected = index == currentStatusIndex
                        OutlinedButton(
                            onClick = { onStatusChange(index) },
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(option.get(language))
                        }
                    }
                }
            }

            Button(onClick = { onSendUpdate(readyUnits, requestedUnits) }, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Outlined.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(localizedText(language, "Send update to patient", "रोगी को अपडेट भेजें"))
            }
        }
    }
}

@Composable
private fun OrderItemRow(
    language: AppLanguage,
    item: MedicineOrderItem,
    readyQuantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = item.name.get(language), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(
            text = localizedText(language, "Requested: ${item.requestedQuantity}", "अनुरोधित: ${item.requestedQuantity}"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = localizedText(language, "Ready now: $readyQuantity", "अब तैयार: $readyQuantity"),
                    style = MaterialTheme.typography.bodyMedium
                )
                item.note?.let { note ->
                    Text(
                        text = note.get(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { onQuantityChange(readyQuantity - 1) }, enabled = readyQuantity > 0) {
                    Text("-")
                }
                Text(text = readyQuantity.toString(), style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { onQuantityChange(readyQuantity + 1) }, enabled = readyQuantity < item.requestedQuantity) {
                    Text("+")
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    language: AppLanguage,
    orderStatuses: Map<Int, Int>,
    orderFulfilment: Map<Pair<Int, Int>, Int>
) {
    val totalOrders = workerOrders.size
    val totalRequestedUnits = workerOrders.sumOf { order -> order.items.sumOf { it.requestedQuantity } }
    val totalReadyUnits = workerOrders.sumOf { order ->
        order.items.sumOf { item ->
            orderFulfilment[order.id to item.id] ?: item.availableQuantity.coerceAtMost(item.requestedQuantity)
        }
    }
    val fullyReadyOrders = workerOrders.count { order ->
        order.items.all { item ->
            val ready = orderFulfilment[order.id to item.id] ?: item.availableQuantity.coerceAtMost(item.requestedQuantity)
            ready >= item.requestedQuantity
        }
    }
    val enRouteOrders = workerOrders.count { order -> (orderStatuses[order.id] ?: 0) == 2 }

    SectionCard(title = localizedText(language, "Community orders overview", "समुदाय ऑर्डर अवलोकन")) {
        Text(
            text = localizedText(
                language,
                "Keep orders updated so citizens know when to collect medicines.",
                "????? ????? ???? ???? ?????? ????? ??? ?? ?? ?????"
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OverviewChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Inventory2,
                tint = MaterialTheme.colorScheme.primary,
                title = localizedText(language, "$totalOrders active", "$totalOrders सक्रिय"),
                subtitle = localizedText(language, "orders today", "आज के ऑर्डर")
            )
            OverviewChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.LocalShipping,
                tint = MaterialTheme.colorScheme.tertiary,
                title = localizedText(language, "$enRouteOrders en route", "$enRouteOrders रास्ते में"),
                subtitle = localizedText(language, "out for delivery", "डिलीवरी के लिए बाहर")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OverviewChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Verified,
                tint = MaterialTheme.colorScheme.secondary,
                title = localizedText(language, "Units ready $totalReadyUnits", "तैयार इकाइयाँ $totalReadyUnits"),
                subtitle = localizedText(language, "of $totalRequestedUnits requested", "$totalRequestedUnits अनुरोधों में से")
            )
            OverviewChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Inventory2,
                tint = MaterialTheme.colorScheme.primaryContainer,
                title = localizedText(language, "$fullyReadyOrders ready", "$fullyReadyOrders तैयार"),
                subtitle = localizedText(language, "fully packed orders", "पूर्ण रूप से पैक ऑर्डर")
            )
        }

        TextButton(onClick = { /* TODO: export manifest */ }) {
            Text(localizedText(language, "Export delivery sheet", "डिलीवरी शीट निर्यात करें"))
        }
    }
}

@Composable
private fun OverviewChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = modifier.padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = tint.copy(alpha = 0.12f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}
