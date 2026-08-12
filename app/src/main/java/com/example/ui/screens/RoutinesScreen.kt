package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.LifeOSViewModel

@Composable
fun RoutinesScreen(
    viewModel: LifeOSViewModel,
    onOpenQuickAction: () -> Unit
) {
    val routines by viewModel.routines.collectAsState()

    val morningRoutines = routines.filter { it.routineType == "MORNING" }
    val eveningRoutines = routines.filter { it.routineType == "EVENING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("routines_screen"),
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
                        text = "Daily Routines",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Structure morning and evening power routines.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onOpenQuickAction) {
                    Icon(Icons.Default.Add, contentDescription = "Add Routine Item")
                }
            }
        }

        // Morning Routine
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Morning Routine", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        items(morningRoutines) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isCompletedToday,
                        onCheckedChange = {
                            viewModel.updateRoutineItem(item.copy(isCompletedToday = !item.isCompletedToday))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontWeight = FontWeight.Bold)
                        Text("${item.time} • ${item.durationMinutes} min", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Evening Routine
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NightsStay, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Evening Routine", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        items(eveningRoutines) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isCompletedToday,
                        onCheckedChange = {
                            viewModel.updateRoutineItem(item.copy(isCompletedToday = !item.isCompletedToday))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontWeight = FontWeight.Bold)
                        Text("${item.time} • ${item.durationMinutes} min", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
