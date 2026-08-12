package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LifeOSViewModel

@Composable
fun FocusScreen(viewModel: LifeOSViewModel) {
    val secondsRemaining by viewModel.focusSecondsRemaining.collectAsState()
    val initialSeconds by viewModel.focusInitialSeconds.collectAsState()
    val isRunning by viewModel.isFocusRunning.collectAsState()
    val currentMode by viewModel.focusMode.collectAsState()

    val progress = if (initialSeconds > 0) secondsRemaining.toFloat() / initialSeconds.toFloat() else 0f

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("focus_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Focus Timer",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Eliminate distractions and achieve deep flow.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mode Selector Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = currentMode == "Pomodoro",
                    onClick = { viewModel.setFocusTimerMode("Pomodoro", 25) },
                    label = { Text("Pomodoro (25m)") },
                    modifier = Modifier.weight(1f).testTag("mode_pomodoro")
                )
                FilterChip(
                    selected = currentMode == "Deep Focus",
                    onClick = { viewModel.setFocusTimerMode("Deep Focus", 50) },
                    label = { Text("Deep (50m)") },
                    modifier = Modifier.weight(1f).testTag("mode_deep_focus")
                )
                FilterChip(
                    selected = currentMode == "Short Focus",
                    onClick = { viewModel.setFocusTimerMode("Short Focus", 15) },
                    label = { Text("Short (15m)") },
                    modifier = Modifier.weight(1f).testTag("mode_short_focus")
                )
            }
        }

        // Circular Timer Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(260.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 16.dp.toPx()
                drawCircle(
                    color = trackColor,
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val mins = secondsRemaining / 60
                val secs = secondsRemaining % 60
                val timeStr = String.format("%02d:%02d", mins, secs)

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 54.sp),
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentMode.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
            }
        }

        // Controls (Start / Pause / Reset)
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.stopFocusTimer() },
                modifier = Modifier
                    .size(56.dp)
                    .testTag("reset_focus_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer",
                    modifier = Modifier.size(32.dp)
                )
            }

            FloatingActionButton(
                onClick = {
                    if (isRunning) viewModel.pauseFocusTimer()
                    else viewModel.startFocusTimer()
                },
                modifier = Modifier
                    .size(80.dp)
                    .testTag("toggle_focus_button"),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
