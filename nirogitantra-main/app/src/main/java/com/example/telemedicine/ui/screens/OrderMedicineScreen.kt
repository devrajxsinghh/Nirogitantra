package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.LocalizedText
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.TopBarActions
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.width

private data class Medicine(
    val name: String,
    val strength: String,
    val pack: LocalizedText,
    val mrp: Int,
    val price: Int,
    val inStock: Boolean = true,
    val seller: LocalizedText = LocalizedText("Jan Aushadhi Kendra", "Jan Aushadhi Kendra")
)

private val Catalog = listOf(
    Medicine("Metformin", "500 mg", LocalizedText("Strip of 10", "Strip of 10"), mrp = 48, price = 32),
    Medicine("Telmisartan", "40 mg", LocalizedText("Strip of 10", "Strip of 10"), mrp = 110, price = 85),
    Medicine("Vitamin D3 Sachet", "60k IU", LocalizedText("Pack of 4", "Pack of 4"), mrp = 160, price = 130),
    Medicine("Paracetamol", "650 mg", LocalizedText("Strip of 15", "Strip of 15"), mrp = 55, price = 42),
    Medicine("Cetirizine", "10 mg", LocalizedText("Strip of 10", "Strip of 10"), mrp = 22, price = 18)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderMedicineScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var cart by remember { mutableStateOf<Map<Medicine, Int>>(emptyMap()) }

    val filteredCatalog = remember(query) {
        Catalog.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
    }
    val itemCount = cart.values.sum()
    val total = cart.entries.sumOf { (medicine, qty) -> medicine.price * qty }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedText(language, "Order Medicines", "दवाइयाँ मँगवाएँ")) },
                actions = {
                    TopBarActions(
                        language = language,
                        onLanguageChange = onLanguageChange,
                        onLogout = onLogout
                    )
                }
            )
        },
        floatingActionButton = {
            if (itemCount > 0) {
                Button(onClick = {}, modifier = Modifier.padding(16.dp)) {
                    Text(localizedText(language, "Checkout - Rs $total", "चेकआउट - ₹$total"))
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(localizedText(language, "Search medicines", "दवाइयाँ खोजें")) }
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredCatalog) { medicine ->
                    val quantity = cart[medicine] ?: 0
                    MedicineCard(
                        language = language,
                        medicine = medicine,
                        quantity = quantity,
                        onIncrement = {
                            cart = cart.toMutableMap().apply { put(medicine, quantity + 1) }
                        },
                        onDecrement = {
                            cart = cart.toMutableMap().apply {
                                when (quantity) {
                                    0 -> {}
                                    1 -> remove(medicine)
                                    else -> put(medicine, quantity - 1)
                                }
                            }
                        }
                    )
                }
            }

            if (itemCount > 0) {
                CartSummary(language = language, total = total, itemCount = itemCount)
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun MedicineCard(
    language: AppLanguage,
    medicine: Medicine,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("${medicine.name} ${medicine.strength}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "${medicine.pack.get(language)} - ${localizedText(language, "Seller", "विक्रेता")}: ${medicine.seller.get(language)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("MRP Rs ${medicine.mrp}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Rs ${medicine.price}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (medicine.inStock) localizedText(language, "In stock", "स्टॉक में") else localizedText(language, "Out of stock", "स्टॉक खत्म"),
                    color = if (medicine.inStock) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrement, enabled = quantity > 0) {
                        Icon(Icons.Outlined.Remove, contentDescription = null)
                    }
                    Text(quantity.toString(), modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                    IconButton(onClick = onIncrement) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun CartSummary(language: AppLanguage, total: Int, itemCount: Int) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(localizedText(language, "Cart Summary", "कार्ट सारांश"), fontWeight = FontWeight.SemiBold)
            Text(
                localizedText(language, "$itemCount item(s) - Total Rs $total", "$itemCount वस्तुएँ - कुल ₹$total"),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = null)
                Text(localizedText(language, "Mock cart for demo", "डेमो के लिए नकली कार्ट"), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview
@Composable
private fun OrderMedicineScreenPreview() {
    OrderMedicineScreen(
        language = AppLanguage.ENGLISH,
        onLanguageChange = {},
        onLogout = {}
    )
}
