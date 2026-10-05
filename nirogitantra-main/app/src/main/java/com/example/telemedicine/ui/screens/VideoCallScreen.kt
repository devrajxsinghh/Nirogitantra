package com.example.telemedicine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.telemedicine.AppLanguage
import com.example.telemedicine.localizedText
import com.example.telemedicine.ui.components.TopBarActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCallScreen(
    language: AppLanguage,
    caller: String,
    onClose: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogout: () -> Unit
) {
    val title = when (caller.lowercase()) {
        "patient" -> localizedText(language, "Patient Call", "रोगी कॉल")
        "doctor" -> localizedText(language, "Doctor Call", "डॉक्टर कॉल")
        else -> localizedText(language, "Telemedicine Call", "टेलीमेडिसिन कॉल")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = localizedText(language, "Live video", "लाइव वीडियो"),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = localizedText(language, "You", "आप"),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                SectionCallInfo(language = language, caller = caller)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(onClick = { /* mute toggled visually */ }) {
                    Icon(imageVector = Icons.Filled.Mic, contentDescription = localizedText(language, "Toggle mute", "म्यूट बदलें"))
                }
                FilledIconButton(
                    onClick = onClose,
                    colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Filled.CallEnd, contentDescription = localizedText(language, "End call", "कॉल समाप्त करें"))
                }
                FilledTonalIconButton(onClick = { /* camera toggle */ }) {
                    Icon(imageVector = Icons.Filled.Videocam, contentDescription = localizedText(language, "Toggle video", "वीडियो बदलें"))
                }
            }
        }
    }
}

@Composable
private fun SectionCallInfo(language: AppLanguage, caller: String) {
    val connectingWith = when (caller.lowercase()) {
        "patient" -> localizedText(language, "Doctor on call", "डॉक्टर ऑनलाइन")
        "doctor" -> localizedText(language, "Patient connected", "रोगी जुड़ा हुआ है")
        else -> localizedText(language, "Tele-health session", "टेली-हेल्थ सत्र")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = connectingWith, style = MaterialTheme.typography.titleSmall)
        Text(
            text = localizedText(
                language,
                "This is a mock call surface for the demo. Add SDK later.",
                "यह डेमो के लिए नकली कॉल सतह है। बाद में एसडीके जोड़ें।"
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = localizedText(language, "Timer: 00:12", "समय: 00:12"),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
