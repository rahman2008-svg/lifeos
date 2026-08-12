package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.StudySession
import com.example.data.StudySubject
import com.example.viewmodel.LifeOSViewModel

@Composable
fun StudyScreen(
    viewModel: LifeOSViewModel,
    onNavigateToFocus: () -> Unit
) {
    val subjects by viewModel.studySubjects.collectAsState()
    val sessions by viewModel.studySessions.collectAsState()

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var subjectNameInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("study_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Study OS",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track subjects, study hours, and review sessions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(onClick = onNavigateToFocus, modifier = Modifier.testTag("start_study_timer_button")) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Study Timer")
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subjects & Weekly Targets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { showAddSubjectDialog = true }) {
                    Text("+ Add Subject")
                }
            }
        }

        items(subjects) { subject ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(subject.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Weekly Goal: ${subject.targetHoursPerWeek} hours", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    OutlinedButton(onClick = {
                        viewModel.addStudySessionItem(
                            StudySession(
                                subjectId = subject.id,
                                subjectName = subject.name,
                                durationMinutes = 45,
                                notes = "45-minute focused review"
                            )
                        )
                    }) {
                        Text("+ Log 45m")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recent Study Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(sessions.take(6)) { session ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(session.subjectName, fontWeight = FontWeight.Bold)
                        Text(session.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text("${session.durationMinutes} min")
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Study Subject") },
            text = {
                OutlinedTextField(
                    value = subjectNameInput,
                    onValueChange = { subjectNameInput = it },
                    label = { Text("Subject Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (subjectNameInput.isNotBlank()) {
                        viewModel.addStudySubjectItem(StudySubject(name = subjectNameInput))
                        subjectNameInput = ""
                        showAddSubjectDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
