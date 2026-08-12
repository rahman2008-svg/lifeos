package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TaskItem
import com.example.viewmodel.LifeOSViewModel

@Composable
fun TodayPlannerScreen(
    viewModel: LifeOSViewModel,
    onOpenQuickAction: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val todayStr = viewModel.getTodayDateString()

    val top3Tasks = tasks.filter { it.isPriorityTop3 }
    val regularTasks = tasks.filter { !it.isPriorityTop3 && (it.dueDate.isEmpty() || it.dueDate == todayStr) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("today_planner_screen"),
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
                        text = "Daily Planner",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Date: $todayStr",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onOpenQuickAction, modifier = Modifier.testTag("add_planner_task_button")) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }

        // Top 3 Priorities
        item {
            Text(
                text = "Today's 3 Core Priorities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (top3Tasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No Top 3 Priorities set. Add tasks and check 'Set as Top 3'!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(top3Tasks) { task ->
                TaskPlannerCard(task = task, onToggle = { viewModel.toggleTaskCompleted(task) })
            }
        }

        // Hourly Timeline View
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hourly Agenda Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val hours = (6..22).toList()
        items(hours) { hour ->
            val hourStr = String.format("%02d:00", hour)
            val matchingTasks = regularTasks.filter { it.dueTime.startsWith(String.format("%02d", hour)) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = hourStr,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(60.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    if (matchingTasks.isEmpty()) {
                        Divider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    } else {
                        matchingTasks.forEach { t ->
                            TaskPlannerCard(task = t, onToggle = { viewModel.toggleTaskCompleted(t) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskPlannerCard(task: TaskItem, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${task.category} • Due ${task.dueTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
