package com.example.telemedicine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.LocalizedText
import com.example.telemedicine.UserRole
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.LanguageToggle
import com.example.telemedicine.ui.components.RoleInfo
import com.example.telemedicine.ui.components.RoleSelectionCard
import com.example.telemedicine.ui.components.SectionCard

@Composable
fun LoginScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogin: (UserRole, String) -> Unit
) {
    var selectedRole by rememberSaveable { mutableStateOf(UserRole.PATIENT) }
    var displayName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val roleCards = listOf(
        RoleInfo(
            role = UserRole.PATIENT,
            title = LocalizedText("Patient", "रोगी"),
            description = LocalizedText(
                "Check reports, track symptoms, and connect with care teams.",
                "रिपोर्ट देखें, लक्षण दर्ज करें और देखभाल टीम से जुड़ें।"
            ),
            accent = MaterialTheme.colorScheme.primary
        ),
        RoleInfo(
            role = UserRole.DOCTOR,
            title = LocalizedText("Doctor", "चिकित्सक"),
            description = LocalizedText(
                "Review schedules, manage calls, and keep patient notes handy.",
                "शेड्यूल देखें, कॉल प्रबंधित करें और रोगी नोट्स तैयार रखें।"
            ),
            accent = MaterialTheme.colorScheme.secondary
        ),
        RoleInfo(
            role = UserRole.WORKER,
            title = LocalizedText("Jan Aushadhi Worker", "जन औषधि कर्मचारी"),
            description = LocalizedText(
                "Update medicine stock and order statuses for your community.",
                "समुदाय के लिए दवा स्टॉक और ऑर्डर स्थिति अपडेट करें।"
            ),
            accent = MaterialTheme.colorScheme.tertiary
        ),
        // NEW: Welfare Department entry
        RoleInfo(
            role = UserRole.WELFARE,
            title = LocalizedText("Welfare Department", "कल्याण विभाग"),
            description = LocalizedText(
                "Open the dashboard for area disease trends, hospital needs, and feedback.",
                "क्षेत्रीय बीमारी रुझान, अस्पताल आवश्यकताएँ और फीडबैक डैशबोर्ड खोलें।"
            ),
            accent = MaterialTheme.colorScheme.primaryContainer
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            LanguageToggle(language = language, onLanguageChange = onLanguageChange)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = localizedText(language, "Hello, welcome back", "नमस्ते, आपका स्वागत है"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = localizedText(
                        language,
                        "Pick how you want to explore the demo care hub.",
                        "डेमो देखभाल केंद्र का अनुभव करने के लिए अपनी भूमिका चुनें।"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }

            SectionCard(title = localizedText(language, "Account details", "खाता विवरण")) {
                Text(
                    text = localizedText(
                        language,
                        "Name, username, and password are optional for this demo.",
                        "इस डेमो के लिए नाम, उपयोगकर्ता नाम और पासवर्ड वैकल्पिक हैं।"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(localizedText(language, "Display name", "प्रदर्शन नाम")) },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) }
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(localizedText(language, "Username (optional)", "उपयोगकर्ता नाम (वैकल्पिक)")) },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(localizedText(language, "Password (optional)", "पासवर्ड (वैकल्पिक)")) },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = null) },
                    trailingIcon = {
                        val visibilityIcon = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = visibilityIcon, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(autoCorrect = false)
                )
            }

            SectionCard(title = localizedText(language, "Select your role", "अपनी भूमिका चुनें")) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    roleCards.forEach { info ->
                        RoleSelectionCard(
                            info = info,
                            language = language,
                            selected = info.role == selectedRole,
                            onSelect = { selectedRole = info.role },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { onLogin(selectedRole, displayName) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(text = localizedText(language, "Continue", "आगे बढ़ें"))
        }
    }
}
