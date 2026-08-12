package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferences
import com.example.viewmodel.LifeOSViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: LifeOSViewModel,
    onComplete: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) } // 0: Splash/Welcome, 1: Modules, 2: Language, 3: Theme, 4: Notifications

    val availableModules = listOf("Tasks", "Study", "Goals", "Habits", "Finance", "Notes", "Books", "Ideas", "Focus")
    val selectedModules = remember { mutableStateListOf<String>().apply { addAll(availableModules) } }

    var selectedLang by remember { mutableStateOf("English") }
    val languages = listOf("English", "বাংলা", "हिन्दी", "Español", "العربية", "Français", "Português")

    var selectedTheme by remember { mutableStateOf("Dark") }
    val themes = listOf("Dark", "Light", "System")

    var notifsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CompassCalibration,
                            contentDescription = "LifeOS Logo",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "LIFEOS",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Your life, organized.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                0 -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Welcome to your Personal Life Operating System.",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "LifeOS is local-first, privacy-first, and completely AI-free. Everything runs deterministically on your device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                1 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "What would you like to manage?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableModules.forEach { mod ->
                                FilterChip(
                                    selected = selectedModules.contains(mod),
                                    onClick = {
                                        if (selectedModules.contains(mod)) selectedModules.remove(mod)
                                        else selectedModules.add(mod)
                                    },
                                    label = { Text(mod) },
                                    leadingIcon = if (selectedModules.contains(mod)) {
                                        { Icon(Icons.Default.Check, contentDescription = null) }
                                    } else null
                                )
                            }
                        }
                    }
                }
                2 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Choose Language",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            languages.forEach { lang ->
                                OutlinedCard(
                                    onClick = { selectedLang = lang },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (selectedLang == lang) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(lang, fontWeight = FontWeight.SemiBold)
                                        if (selectedLang == lang) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Choose Theme",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            themes.forEach { th ->
                                FilterChip(
                                    selected = selectedTheme == th,
                                    onClick = { selectedTheme = th },
                                    label = { Text(th) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                4 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Notification & Sound Setup",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable Helpful Reminders")
                            Switch(checked = notifsEnabled, onCheckedChange = { notifsEnabled = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable Sound & Tones")
                            Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 0) {
                    OutlinedButton(onClick = { step -= 1 }) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (step < 4) {
                            step += 1
                        } else {
                            val prefs = UserPreferences(
                                userName = "Operator",
                                isOnboardingCompleted = true,
                                selectedLanguage = selectedLang,
                                themeMode = selectedTheme,
                                enabledModules = selectedModules.joinToString(","),
                                notificationsEnabled = notifsEnabled,
                                soundEnabled = soundEnabled
                            )
                            viewModel.savePreferences(prefs)
                            onComplete()
                        }
                    },
                    modifier = Modifier.testTag("onboarding_next_button")
                ) {
                    Text(if (step == 4) "Get Started" else "Continue")
                }
            }
        }
    }
}
