package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.sound.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class LifeScoreBreakdown(
    val scoreTotal: Int,
    val taskContribution: Int,
    val habitContribution: Int,
    val studyContribution: Int,
    val goalContribution: Int,
    val focusContribution: Int,
    val explanationText: String
)

data class SearchResultItem(
    val title: String,
    val subtitle: String,
    val category: String,
    val routeDestination: String
)

class LifeOSViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = LifeOSRepository(db.dao())
    val soundManager = SoundManager(application)

    val preferences: StateFlow<UserPreferences> = repository.preferences
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val tasks: StateFlow<List<TaskItem>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitItem>> = repository.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalItem>> = repository.goals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestones: StateFlow<List<MilestoneItem>> = repository.milestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectItem>> = repository.projects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studySubjects: StateFlow<List<StudySubject>> = repository.studySubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studySessions: StateFlow<List<StudySession>> = repository.studySessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSession>> = repository.focusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteItem>> = repository.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val books: StateFlow<List<BookItem>> = repository.books
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ideas: StateFlow<List<IdeaItem>> = repository.ideas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financeTransactions: StateFlow<List<FinanceTransaction>> = repository.financeTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<AlarmItem>> = repository.alarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routines: StateFlow<List<RoutineItem>> = repository.routines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timelineEvents: StateFlow<List<TimelineEvent>> = repository.timelineEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habitLogsToday: StateFlow<List<HabitLog>> = repository.getHabitLogsForDate(getTodayDateString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Focus Timer State
    private val _focusSecondsRemaining = MutableStateFlow(25 * 60)
    val focusSecondsRemaining: StateFlow<Int> = _focusSecondsRemaining.asStateFlow()

    private val _focusInitialSeconds = MutableStateFlow(25 * 60)
    val focusInitialSeconds: StateFlow<Int> = _focusInitialSeconds.asStateFlow()

    private val _isFocusRunning = MutableStateFlow(false)
    val isFocusRunning: StateFlow<Boolean> = _isFocusRunning.asStateFlow()

    private val _focusMode = MutableStateFlow("Pomodoro")
    val focusMode: StateFlow<String> = _focusMode.asStateFlow()

    private var focusTimerJob: Job? = null

    // Ringing Alarm State
    private val _ringingAlarm = MutableStateFlow<AlarmItem?>(null)
    val ringingAlarm: StateFlow<AlarmItem?> = _ringingAlarm.asStateFlow()

    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // Transparent Rule-Based Life Score Calculation
    @Suppress("UNCHECKED_CAST")
    val lifeScoreBreakdown: StateFlow<LifeScoreBreakdown> = combine(
        tasks, habits, habitLogsToday, studySessions, goals, focusSessions
    ) { flows: Array<Any> ->
        val currentTasks = flows[0] as List<TaskItem>
        val currentHabits = flows[1] as List<HabitItem>
        val currentHabitLogs = flows[2] as List<HabitLog>
        val currentStudySessions = flows[3] as List<StudySession>
        val currentGoals = flows[4] as List<GoalItem>
        val currentFocusSessions = flows[5] as List<FocusSession>

        val todayStr = getTodayDateString()

        // 1. Task Score (Max 25 points)
        val todayTasks = currentTasks.filter { it.dueDate.isEmpty() || it.dueDate == todayStr }
        val taskRatio = if (todayTasks.isNotEmpty()) {
            todayTasks.count { it.isCompleted }.toDouble() / todayTasks.size
        } else 1.0
        val taskPts = (taskRatio * 25).toInt()

        // 2. Habit Score (Max 20 points)
        val activeHabits = currentHabits.filter { !it.isPaused }
        val habitRatio = if (activeHabits.isNotEmpty()) {
            currentHabitLogs.size.toDouble() / activeHabits.size
        } else 1.0
        val habitPts = (habitRatio * 20).toInt().coerceAtMost(20)

        // 3. Study Score (Max 20 points)
        val todayStudyMin = currentStudySessions.filter { isToday(it.dateTimestamp) }.sumOf { it.durationMinutes }
        val studyPts = ((todayStudyMin.toDouble() / 120.0).coerceAtMost(1.0) * 20).toInt()

        // 4. Goal Score (Max 20 points)
        val goalAvg = if (currentGoals.isNotEmpty()) {
            currentGoals.map { it.progressPercent }.average() / 100.0
        } else 0.5
        val goalPts = (goalAvg * 20).toInt()

        // 5. Focus Score (Max 15 points)
        val todayFocusMin = currentFocusSessions.filter { isToday(it.completedTimestamp) }.sumOf { it.durationMinutes }
        val focusPts = ((todayFocusMin.toDouble() / 50.0).coerceAtMost(1.0) * 15).toInt()

        val total = (taskPts + habitPts + studyPts + goalPts + focusPts).coerceIn(0, 100)

        val explanation = "Calculated deterministically: Tasks ($taskPts/25) + Habits ($habitPts/20) + Study ($studyPts/20) + Goals ($goalPts/20) + Focus ($focusPts/15) = $total/100. No AI or subjective weighting is used."

        LifeScoreBreakdown(
            scoreTotal = total,
            taskContribution = taskPts,
            habitContribution = habitPts,
            studyContribution = studyPts,
            goalContribution = goalPts,
            focusContribution = focusPts,
            explanationText = explanation
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LifeScoreBreakdown(82, 20, 18, 16, 15, 13, "Calculating transparent life score...")
    )

    private fun isToday(timestamp: Long): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp)) == getTodayDateString()
    }

    // Search Results across all entities
    @Suppress("UNCHECKED_CAST")
    val searchResults: StateFlow<List<SearchResultItem>> = combine(
        searchQuery, tasks, goals, habits, notes, books, ideas
    ) { flows: Array<Any> ->
        val query = flows[0] as String
        val tList = flows[1] as List<TaskItem>
        val gList = flows[2] as List<GoalItem>
        val hList = flows[3] as List<HabitItem>
        val nList = flows[4] as List<NoteItem>
        val bList = flows[5] as List<BookItem>
        val iList = flows[6] as List<IdeaItem>

        if (query.isBlank()) return@combine emptyList()
        val q = query.trim().lowercase()
        val results = mutableListOf<SearchResultItem>()

        tList.filter { it.title.lowercase().contains(q) || it.category.lowercase().contains(q) }.forEach {
            results.add(SearchResultItem(it.title, "Task • Due ${it.dueDate} ${it.dueTime}", "Task", "today"))
        }
        gList.filter { it.title.lowercase().contains(q) || it.category.lowercase().contains(q) }.forEach {
            results.add(SearchResultItem(it.title, "Goal • ${it.progressPercent}% progress", "Goal", "goals"))
        }
        hList.filter { it.name.lowercase().contains(q) }.forEach {
            results.add(SearchResultItem(it.name, "Habit • Streak: ${it.streakCount} days", "Habit", "habits"))
        }
        nList.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) }.forEach {
            results.add(SearchResultItem(it.title, "Note • ${it.folder}", "Note", "notes"))
        }
        bList.filter { it.title.lowercase().contains(q) || it.author.lowercase().contains(q) }.forEach {
            results.add(SearchResultItem(it.title, "Book • ${it.author}", "Book", "books"))
        }
        iList.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }.forEach {
            results.add(SearchResultItem(it.title, "Idea • ${it.category}", "Idea", "ideas"))
        }

        results
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Focus Timer Logic
    fun setFocusTimerMode(mode: String, minutes: Int) {
        _focusMode.value = mode
        _focusInitialSeconds.value = minutes * 60
        _focusSecondsRemaining.value = minutes * 60
        stopFocusTimer()
    }

    fun startFocusTimer() {
        if (_isFocusRunning.value) return
        _isFocusRunning.value = true
        focusTimerJob = viewModelScope.launch {
            while (_focusSecondsRemaining.value > 0 && _isFocusRunning.value) {
                delay(1000)
                _focusSecondsRemaining.value -= 1
            }
            if (_focusSecondsRemaining.value <= 0 && _isFocusRunning.value) {
                _isFocusRunning.value = false
                soundManager.playSound(preferences.value.defaultSound)
                soundManager.vibrate(800)

                val duration = _focusInitialSeconds.value / 60
                repository.insertFocusSession(FocusSession(mode = _focusMode.value, durationMinutes = duration))
                repository.insertNotification(
                    NotificationItem(
                        title = "Focus Session Completed! 🎉",
                        description = "Completed $duration minutes of ${_focusMode.value} session.",
                        category = "Focus",
                        priority = "High"
                    )
                )
            }
        }
    }

    fun pauseFocusTimer() {
        _isFocusRunning.value = false
        focusTimerJob?.cancel()
    }

    fun stopFocusTimer() {
        _isFocusRunning.value = false
        focusTimerJob?.cancel()
        _focusSecondsRemaining.value = _focusInitialSeconds.value
    }

    // Alarm Trigger Test / Ringing Logic
    fun triggerAlarm(alarm: AlarmItem) {
        _ringingAlarm.value = alarm
        if (preferences.value.soundEnabled) {
            soundManager.playSound(alarm.soundName)
        }
        if (alarm.isVibration) {
            soundManager.vibrate(1000)
        }
    }

    fun dismissRingingAlarm() {
        _ringingAlarm.value = null
    }

    fun snoozeRingingAlarm() {
        val alarm = _ringingAlarm.value
        _ringingAlarm.value = null
        if (alarm != null) {
            viewModelScope.launch {
                repository.insertNotification(
                    NotificationItem(
                        title = "Alarm Snoozed",
                        description = "Snoozed '${alarm.title}' for ${alarm.snoozeDurationMinutes} minutes.",
                        category = "Alarm",
                        priority = "Normal"
                    )
                )
            }
        }
    }

    // Helper functions for user actions
    fun toggleTaskCompleted(task: TaskItem) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updated)
            if (updated.isCompleted) {
                soundManager.playSound("Soft")
            }
        }
    }

    fun toggleHabitToday(habit: HabitItem, isLoggedToday: Boolean) {
        viewModelScope.launch {
            val today = getTodayDateString()
            repository.toggleHabitLog(habit.id, today, !isLoggedToday)
            if (!isLoggedToday) {
                repository.updateHabit(habit.copy(streakCount = habit.streakCount + 1, lastCompletedDate = today))
                soundManager.playSound("Soft")
            } else {
                repository.updateHabit(habit.copy(streakCount = (habit.streakCount - 1).coerceAtLeast(0)))
            }
        }
    }

    fun savePreferences(prefs: UserPreferences) {
        viewModelScope.launch {
            repository.savePreferences(prefs)
        }
    }

    // ViewModel Coroutine Helper methods
    fun addTask(task: TaskItem) = viewModelScope.launch { repository.insertTask(task) }
    fun addHabit(habit: HabitItem) = viewModelScope.launch { repository.insertHabit(habit) }
    fun addAlarm(alarm: AlarmItem) = viewModelScope.launch { repository.insertAlarm(alarm) }
    fun updateAlarmItem(alarm: AlarmItem) = viewModelScope.launch { repository.updateAlarm(alarm) }
    fun deleteAlarmItem(id: Long) = viewModelScope.launch { repository.deleteAlarm(id) }
    fun addNote(note: NoteItem) = viewModelScope.launch { repository.insertNote(note) }
    fun updateNoteItem(note: NoteItem) = viewModelScope.launch { repository.updateNote(note) }
    fun deleteNoteItem(id: Long) = viewModelScope.launch { repository.deleteNote(id) }
    fun addBook(book: BookItem) = viewModelScope.launch { repository.insertBook(book) }
    fun addIdea(idea: IdeaItem) = viewModelScope.launch { repository.insertIdea(idea) }
    fun addFinance(tx: FinanceTransaction) = viewModelScope.launch { repository.insertFinanceTransaction(tx) }
    fun updateMilestoneItem(milestone: MilestoneItem) = viewModelScope.launch { repository.updateMilestone(milestone) }
    fun updateHabitItem(habit: HabitItem) = viewModelScope.launch { repository.updateHabit(habit) }
    fun clearNotifications() = viewModelScope.launch { repository.clearAllNotifications() }
    fun updateRoutineItem(routine: RoutineItem) = viewModelScope.launch { repository.updateRoutine(routine) }
    fun addStudySubjectItem(subject: StudySubject) = viewModelScope.launch { repository.insertStudySubject(subject) }
    fun addStudySessionItem(session: StudySession) = viewModelScope.launch { repository.insertStudySession(session) }

    // Deterministic Next Alarm calculation string
    fun getNextAlarmText(alarmList: List<AlarmItem>): String {
        val activeAlarms = alarmList.filter { it.isEnabled }
        if (activeAlarms.isEmpty()) return "No active alarms scheduled."

        val cal = Calendar.getInstance()
        val nowMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        var minDiff = Int.MAX_VALUE
        var nextAlarm: AlarmItem? = null

        for (alarm in activeAlarms) {
            val alarmMin = alarm.timeHour * 60 + alarm.timeMinute
            var diff = alarmMin - nowMin
            if (diff <= 0) {
                diff += 24 * 60 // next day
            }
            if (diff < minDiff) {
                minDiff = diff
                nextAlarm = alarm
            }
        }

        if (nextAlarm == null) return "No active alarms."

        val hrs = minDiff / 60
        val mins = minDiff % 60
        return if (hrs > 0) "Next alarm '${nextAlarm.title}' in ${hrs}h ${mins}m" else "Next alarm '${nextAlarm.title}' in ${mins} min"
    }
}
