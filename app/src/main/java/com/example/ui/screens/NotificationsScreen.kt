package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.LifeOSViewModel

@Composable
fun NotificationsScreen(viewModel: LifeOSViewModel) {
    val notifications by viewModel.notifications.collectAsState()
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Today, 2: Priority

    val filteredList = when (selectedFilterTab) {
        1 -> notifications.take(5)
        2 -> notifications.filter { it.priority == "High" || it.priority == "Critical" }
        else -> notifications
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("notifications_screen"),
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
                        text = "Notification Center",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Deterministic reminders and system alerts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {
                    viewModel.clearNotifications()
                }) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear All")
                }
            }
        }

        item {
            TabRow(selectedTabIndex = selectedFilterTab) {
                Tab(selected = selectedFilterTab == 0, onClick = { selectedFilterTab = 0 }, text = { Text("All (${notifications.size})") })
                Tab(selected = selectedFilterTab == 1, onClick = { selectedFilterTab = 1 }, text = { Text("Today") })
                Tab(selected = selectedFilterTab == 2, onClick = { selectedFilterTab = 2 }, text = { Text("High Priority") })
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No notifications found.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredList) { notif ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (notif.priority == "High" || notif.priority == "Critical") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(notif.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Badge(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                            Text(notif.category)
                        }
                    }
                }
            }
        }
    }
}
