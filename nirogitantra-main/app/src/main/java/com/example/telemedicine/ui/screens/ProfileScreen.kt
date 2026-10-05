package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.TopBarActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(localizedText(language, "Profile", "Profile")) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(localizedText(language, "Manage your personal details and settings.", "Apni jankari aur settings yahan badlein."))
            Text(localizedText(language, "This demo shows where profile options will appear.", "Yahaan profile ke vikalp dikhaye jayenge."), fontWeight = FontWeight.Medium)
        }
    }
}
