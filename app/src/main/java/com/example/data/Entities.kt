package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val userName: String = "User",
    val isOnboardingCompleted: Boolean = false,
    val selectedLanguage: String = "English", // English, Bengali, Hindi, Spanish, Arabic, French, Portuguese
    val themeMode: String = "System", // System, Light, Dark
    val enabledModules: String = "Tasks,Study,Goals,Habits,Finance,Notes,Books,Ideas,Focus", // CSV
    val quietHoursStart: String = "22:30",
    val quietHoursEnd: String = "06:30",
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val defaultSound: String = "Digital", // Default, Soft, Digital, Bell, None
    val taskRemindersEnabled: Boolean = true,
    val habitRemindersEnabled: Boolean = true,
    val studyRemindersEnabled: Boolean = true,
    val goalRemindersEnabled: Boolean = true
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General", // Work, Study, Personal, Health, Finance
    val priority: String = "Normal", // Low, Normal, High, Critical
    val dueDate: String = "", // YYYY-MM-DD
    val dueTime: String = "", // HH:mm
    val isCompleted: Boolean = false,
    val isPriorityTop3: Boolean = false,
    val reminderMinutesBefore: Int = 15, // 0, 5, 10, 15, 30, 60, 1440
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "Daily",
    val targetDaysMask: Int = 127, // Bitmask for Mon-Sun (1111111 = 127)
    val streakCount: Int = 0,
    val lastCompletedDate: String = "", // YYYY-MM-DD
    val reminderTime: String = "08:00",
    val isPaused: Boolean = false,
    val isArchived: Boolean = false
)

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateString: String, // YYYY-MM-DD
    val isCompleted: Boolean = true
)

@Entity(tableName = "goals")
data class GoalItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Personal",
    val targetDate: String = "",
    val progressPercent: Int = 0,
    val status: String = "In Progress" // Not Started, In Progress, Completed
)

@Entity(tableName = "milestones")
data class MilestoneItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val dueDate: String = ""
)

@Entity(tableName = "projects")
data class ProjectItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val milestoneId: Long = 0,
    val title: String,
    val status: String = "Active"
)

@Entity(tableName = "study_subjects")
data class StudySubject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetHoursPerWeek: Int = 5,
    val colorHex: String = "#0284C7"
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val subjectName: String,
    val durationMinutes: Int,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String = "Pomodoro", // Pomodoro, Deep Focus, Short Focus, Custom
    val durationMinutes: Int = 25,
    val completedTimestamp: Long = System.currentTimeMillis(),
    val tag: String = "General"
)

@Entity(tableName = "notes")
data class NoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val tags: String = "", // CSV
    val folder: String = "Inbox",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "books")
data class BookItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val totalPages: Int = 100,
    val currentPage: Int = 0,
    val rating: Float = 0f,
    val notes: String = "",
    val readingReminderTime: String = "21:00"
)

@Entity(tableName = "ideas")
data class IdeaItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String = "General", // App, Business, Writing, Personal, Random
    val tags: String = "",
    val status: String = "New", // New, Exploring, Executing, Archived
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "finance_transactions")
data class FinanceTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // INCOME, EXPENSE
    val category: String = "General", // Salary, Food, Utilities, Education, Shopping
    val dateTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "alarms")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "Alarm",
    val timeHour: Int,
    val timeMinute: Int,
    val repeatDaysMask: Int = 127, // 127 = Every day
    val soundName: String = "Digital",
    val isVibration: Boolean = true,
    val snoozeDurationMinutes: Int = 5,
    val isEnabled: Boolean = true,
    val lastTriggeredTime: Long = 0
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String = "General", // Task, Habit, Study, Goal, Focus, Alarm
    val priority: String = "Normal", // Low, Normal, High, Critical
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionType: String = "", // START_FOCUS, COMPLETE_TASK, SNOOZE_ALARM
    val relatedEntityId: Long = 0
)

@Entity(tableName = "routines")
data class RoutineItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineType: String = "MORNING", // MORNING, EVENING, CUSTOM
    val title: String,
    val time: String = "07:00",
    val durationMinutes: Int = 15,
    val isCompletedToday: Boolean = false
)

@Entity(tableName = "timeline_events")
data class TimelineEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dateString: String = "",
    val category: String = "Achievement"
)
