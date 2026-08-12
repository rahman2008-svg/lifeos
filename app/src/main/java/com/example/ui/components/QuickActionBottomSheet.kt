package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.viewmodel.LifeOSViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionBottomSheet(
    viewModel: LifeOSViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Task, 1: Habit, 2: Alarm, 3: Note, 4: Book, 5: Idea, 6: Finance

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("quick_action_sheet"),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Quick Creation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 0.dp) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Task") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Habit") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Alarm") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Note") })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Book") })
                Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text("Idea") })
                Tab(selected = selectedTab == 6, onClick = { selectedTab = 6 }, text = { Text("Finance") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> AddTaskForm(viewModel, onDismiss)
                1 -> AddHabitForm(viewModel, onDismiss)
                2 -> AddAlarmForm(viewModel, onDismiss)
                3 -> AddNoteForm(viewModel, onDismiss)
                4 -> AddBookForm(viewModel, onDismiss)
                5 -> AddIdeaForm(viewModel, onDismiss)
                6 -> AddFinanceForm(viewModel, onDismiss)
            }
        }
    }
}

@Composable
private fun AddTaskForm(viewModel: LifeOSViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Work") }
    var priority by remember { mutableStateOf("Normal") }
    var isTop3 by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth().testTag("add_task_title_input")
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isTop3, onCheckedChange = { isTop3 = it }, modifier = Modifier.testTag("top3_checkbox"))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Set as Today's Top 3 Priority")
        }

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addTask(
                        TaskItem(
                            title = title,
                            category = category,
                            priority = priority,
                            isPriorityTop3 = isTop3,
                            dueDate = viewModel.getTodayDateString(),
                            dueTime = "18:00"
                        )
                    )
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("save_task_button"),
            enabled = title.isNotBlank()
        ) {
            Text("Save Task")
        }
    }
}

@Composable
private fun AddHabitForm(viewModel: LifeOSViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("08:00") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit Name") },
            modifier = Modifier.fillMaxWidth().testTag("add_habit_name_input")
        )

        OutlinedTextField(
            value = reminderTime,
            onValueChange = { reminderTime = it },
            label = { Text("Reminder Time (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    viewModel.addHabit(HabitItem(name = name, reminderTime = reminderTime))
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("save_habit_button"),
            enabled = name.isNotBlank()
        ) {
            Text("Save Habit")
        }
    }
}

@Composable
private fun AddAlarmForm(viewModel: LifeOSViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("Alarm") }
    var hour by remember { mutableIntStateOf(7) }
    var minute by remember { mutableIntStateOf(0) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Alarm Label") },
            modifier = Modifier.fillMaxWidth().testTag("add_alarm_title_input")
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = hour.toString(),
                onValueChange = { hour = it.toIntOrNull()?.coerceIn(0, 23) ?: 0 },
                label = { Text("Hour (0-23)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = minute.toString(),
                onValueChange = { minute = it.toIntOrNull()?.coerceIn(0, 59) ?: 0 },
                label = { Text("Minute (0-59)") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                viewModel.addAlarm(AlarmItem(title = title, timeHour = hour, timeMinute = minute))
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth().testTag("save_alarm_button")
        ) {
            Text("Save Alarm")
        }
    }
}

@Composable
private fun AddNoteForm(viewModel: LifeOSViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addNote(NoteItem(title = title, content = content))
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save Note")
        }
    }
}

@Composable
private fun AddBookForm(viewModel: LifeOSViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("200") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Book Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = totalPages, onValueChange = { totalPages = it }, label = { Text("Total Pages") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addBook(BookItem(title = title, author = author, totalPages = totalPages.toIntOrNull() ?: 100))
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save Book")
        }
    }
}

@Composable
private fun AddIdeaForm(viewModel: LifeOSViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Idea Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addIdea(IdeaItem(title = title, description = desc))
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Save Idea")
        }
    }
}

@Composable
private fun AddFinanceForm(viewModel: LifeOSViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Transaction Label") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount ($)") }, modifier = Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = type == "EXPENSE", onClick = { type = "EXPENSE" }, label = { Text("Expense") })
            FilterChip(selected = type == "INCOME", onClick = { type = "INCOME" }, label = { Text("Income") })
        }

        Button(
            onClick = {
                val amt = amount.toDoubleOrNull()
                if (title.isNotBlank() && amt != null) {
                    viewModel.addFinance(FinanceTransaction(title = title, amount = amt, type = type))
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && amount.isNotBlank()
        ) {
            Text("Save Transaction")
        }
    }
}
