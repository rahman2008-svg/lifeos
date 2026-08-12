package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class LifeOSRepository(private val dao: LifeOSDao) {

    val preferences: Flow<UserPreferences?> = dao.getUserPreferences()
    val tasks: Flow<List<TaskItem>> = dao.getAllTasks()
    val habits: Flow<List<HabitItem>> = dao.getAllHabits()
    val goals: Flow<List<GoalItem>> = dao.getAllGoals()
    val milestones: Flow<List<MilestoneItem>> = dao.getAllMilestones()
    val projects: Flow<List<ProjectItem>> = dao.getAllProjects()
    val studySubjects: Flow<List<StudySubject>> = dao.getAllStudySubjects()
    val studySessions: Flow<List<StudySession>> = dao.getAllStudySessions()
    val focusSessions: Flow<List<FocusSession>> = dao.getAllFocusSessions()
    val notes: Flow<List<NoteItem>> = dao.getAllNotes()
    val books: Flow<List<BookItem>> = dao.getAllBooks()
    val ideas: Flow<List<IdeaItem>> = dao.getAllIdeas()
    val financeTransactions: Flow<List<FinanceTransaction>> = dao.getAllFinanceTransactions()
    val alarms: Flow<List<AlarmItem>> = dao.getAllAlarms()
    val notifications: Flow<List<NotificationItem>> = dao.getAllNotifications()
    val routines: Flow<List<RoutineItem>> = dao.getAllRoutines()
    val timelineEvents: Flow<List<TimelineEvent>> = dao.getAllTimelineEvents()

    fun getHabitLogsForDate(dateString: String): Flow<List<HabitLog>> = dao.getHabitLogsForDate(dateString)

    suspend fun savePreferences(prefs: UserPreferences) = dao.savePreferences(prefs)

    // Tasks
    suspend fun insertTask(task: TaskItem) = dao.insertTask(task)
    suspend fun updateTask(task: TaskItem) = dao.updateTask(task)
    suspend fun deleteTask(id: Long) = dao.deleteTask(id)

    // Habits
    suspend fun insertHabit(habit: HabitItem) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitItem) = dao.updateHabit(habit)
    suspend fun deleteHabit(id: Long) = dao.deleteHabit(id)
    suspend fun toggleHabitLog(habitId: Long, dateString: String, isCompleted: Boolean) {
        if (isCompleted) {
            dao.insertHabitLog(HabitLog(habitId = habitId, dateString = dateString, isCompleted = true))
        } else {
            dao.deleteHabitLog(habitId, dateString)
        }
    }

    // Goals & Milestones
    suspend fun insertGoal(goal: GoalItem) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalItem) = dao.updateGoal(goal)
    suspend fun deleteGoal(id: Long) = dao.deleteGoal(id)
    suspend fun insertMilestone(milestone: MilestoneItem) = dao.insertMilestone(milestone)
    suspend fun updateMilestone(milestone: MilestoneItem) = dao.updateMilestone(milestone)
    suspend fun deleteMilestone(id: Long) = dao.deleteMilestone(id)
    suspend fun insertProject(project: ProjectItem) = dao.insertProject(project)

    // Study
    suspend fun insertStudySubject(subject: StudySubject) = dao.insertStudySubject(subject)
    suspend fun insertStudySession(session: StudySession) = dao.insertStudySession(session)

    // Focus
    suspend fun insertFocusSession(session: FocusSession) = dao.insertFocusSession(session)

    // Notes
    suspend fun insertNote(note: NoteItem) = dao.insertNote(note)
    suspend fun updateNote(note: NoteItem) = dao.updateNote(note)
    suspend fun deleteNote(id: Long) = dao.deleteNote(id)

    // Books
    suspend fun insertBook(book: BookItem) = dao.insertBook(book)
    suspend fun updateBook(book: BookItem) = dao.updateBook(book)
    suspend fun deleteBook(id: Long) = dao.deleteBook(id)

    // Ideas
    suspend fun insertIdea(idea: IdeaItem) = dao.insertIdea(idea)
    suspend fun updateIdea(idea: IdeaItem) = dao.updateIdea(idea)
    suspend fun deleteIdea(id: Long) = dao.deleteIdea(id)

    // Finance
    suspend fun insertFinanceTransaction(transaction: FinanceTransaction) = dao.insertFinanceTransaction(transaction)
    suspend fun deleteFinanceTransaction(id: Long) = dao.deleteFinanceTransaction(id)

    // Alarms
    suspend fun insertAlarm(alarm: AlarmItem) = dao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmItem) = dao.updateAlarm(alarm)
    suspend fun deleteAlarm(id: Long) = dao.deleteAlarm(id)

    // Notifications
    suspend fun insertNotification(notification: NotificationItem) = dao.insertNotification(notification)
    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)
    suspend fun deleteNotification(id: Long) = dao.deleteNotification(id)
    suspend fun clearAllNotifications() = dao.clearAllNotifications()

    // Routines
    suspend fun insertRoutine(routine: RoutineItem) = dao.insertRoutine(routine)
    suspend fun updateRoutine(routine: RoutineItem) = dao.updateRoutine(routine)
    suspend fun deleteRoutine(id: Long) = dao.deleteRoutine(id)

    // Timeline
    suspend fun insertTimelineEvent(event: TimelineEvent) = dao.insertTimelineEvent(event)

    // Initial Data Seeding
    suspend fun seedInitialDataIfNeeded() {
        val existingPrefs = dao.getUserPreferences().firstOrNull()
        if (existingPrefs == null) {
            val initialPrefs = UserPreferences(
                userName = "Operator",
                isOnboardingCompleted = false,
                selectedLanguage = "English",
                themeMode = "Dark"
            )
            dao.savePreferences(initialPrefs)

            // Seed Tasks
            dao.insertTask(TaskItem(title = "Review Weekly Goals", category = "Personal", priority = "High", isPriorityTop3 = true, dueDate = getTodayDateString(), dueTime = "18:00"))
            dao.insertTask(TaskItem(title = "Complete Mathematics Problem Set", category = "Study", priority = "Critical", isPriorityTop3 = true, dueDate = getTodayDateString(), dueTime = "20:00"))
            dao.insertTask(TaskItem(title = "Read 20 pages of Atomic Habits", category = "Health", priority = "Normal", isPriorityTop3 = true, dueDate = getTodayDateString(), dueTime = "21:30"))
            dao.insertTask(TaskItem(title = "Organize LifeOS Workspace", category = "Work", priority = "Normal", dueDate = getTodayDateString(), dueTime = "16:00"))

            // Seed Habits
            dao.insertHabit(HabitItem(name = "Morning Water (500ml)", category = "Health", streakCount = 12, reminderTime = "06:30"))
            dao.insertHabit(HabitItem(name = "30-Min Deep Study", category = "Study", streakCount = 8, reminderTime = "19:00"))
            dao.insertHabit(HabitItem(name = "Read 20 Pages", category = "Personal", streakCount = 15, reminderTime = "21:00"))
            dao.insertHabit(HabitItem(name = "Daily Planning", category = "Work", streakCount = 5, reminderTime = "08:00"))

            // Seed Goals & Milestones
            val goalId = dao.insertGoal(GoalItem(title = "Become a Full-Stack Developer", category = "Career", targetDate = "2026-12-31", progressPercent = 65, status = "In Progress"))
            dao.insertMilestone(MilestoneItem(goalId = goalId, title = "Master Kotlin & Jetpack Compose", isCompleted = true, dueDate = "2026-03-31"))
            dao.insertMilestone(MilestoneItem(goalId = goalId, title = "Build LifeOS Offline Application", isCompleted = true, dueDate = "2026-08-31"))
            dao.insertMilestone(MilestoneItem(goalId = goalId, title = "Deploy Production API Service", isCompleted = false, dueDate = "2026-10-30"))

            // Seed Study Subjects
            dao.insertStudySubject(StudySubject(name = "Computer Science", targetHoursPerWeek = 10, colorHex = "#0284C7"))
            dao.insertStudySubject(StudySubject(name = "Mathematics", targetHoursPerWeek = 8, colorHex = "#0D9488"))
            dao.insertStudySubject(StudySubject(name = "Physics", targetHoursPerWeek = 6, colorHex = "#6366F1"))

            // Seed Notes
            dao.insertNote(NoteItem(title = "LifeOS Principles", content = "# Core Principles\n- Local-First Data Storage\n- Privacy First & Zero AI Leaks\n- Smart Deterministic Engines", tags = "LifeOS, Architecture", isPinned = true))
            dao.insertNote(NoteItem(title = "Weekly Review Checklist", content = "1. Clean inbox\n2. Review goals\n3. Log expenses\n4. Update habit streaks", tags = "Routine", isPinned = false))

            // Seed Books
            dao.insertBook(BookItem(title = "Atomic Habits", author = "James Clear", totalPages = 320, currentPage = 260, rating = 5.0f, notes = "Small 1% gains compound exponentially over time."))
            dao.insertBook(BookItem(title = "Deep Work", author = "Cal Newport", totalPages = 300, currentPage = 120, rating = 4.8f, notes = "Focus without distraction is a superpower."))

            // Seed Ideas
            dao.insertIdea(IdeaItem(title = "Offline Life OS App", description = "A privacy-first mobile life dashboard with zero AI dependency.", category = "App", status = "Executing"))
            dao.insertIdea(IdeaItem(title = "Local Study Timer", description = "Pomodoro focus loop with ambient audio tones.", category = "App", status = "Exploring"))

            // Seed Finance
            dao.insertFinanceTransaction(FinanceTransaction(title = "Monthly Allowance", amount = 1500.0, type = "INCOME", category = "Salary"))
            dao.insertFinanceTransaction(FinanceTransaction(title = "Textbooks & Course Materials", amount = 120.0, type = "EXPENSE", category = "Education"))
            dao.insertFinanceTransaction(FinanceTransaction(title = "Grocery & Health Items", amount = 85.0, type = "EXPENSE", category = "Food"))

            // Seed Alarms
            dao.insertAlarm(AlarmItem(title = "Morning Wake Up", timeHour = 6, timeMinute = 30, soundName = "Digital", isEnabled = true))
            dao.insertAlarm(AlarmItem(title = "Evening Study Session", timeHour = 19, timeMinute = 0, soundName = "Bell", isEnabled = true))

            // Seed Routines
            dao.insertRoutine(RoutineItem(routineType = "MORNING", title = "Wake Up & Hydrate", time = "06:30", durationMinutes = 10))
            dao.insertRoutine(RoutineItem(routineType = "MORNING", title = "Morning Exercise / Stretch", time = "06:40", durationMinutes = 20))
            dao.insertRoutine(RoutineItem(routineType = "MORNING", title = "Focused Reading", time = "07:00", durationMinutes = 30))
            dao.insertRoutine(RoutineItem(routineType = "EVENING", title = "Daily Task Review", time = "21:00", durationMinutes = 15))
            dao.insertRoutine(RoutineItem(routineType = "EVENING", title = "Wind Down & Sleep Prep", time = "22:00", durationMinutes = 30))

            // Seed Notifications
            dao.insertNotification(NotificationItem(title = "Study Session Upcoming", description = "Physics study session scheduled in 10 minutes.", category = "Study", priority = "Normal"))
            dao.insertNotification(NotificationItem(title = "Task Due Today", description = "Complete Mathematics Problem Set due at 8:00 PM.", category = "Task", priority = "High"))

            // Seed Timeline
            dao.insertTimelineEvent(TimelineEvent(title = "Started Computer Science Journey", description = "Began self-directed learning program.", dateString = "2026-01-15"))
            dao.insertTimelineEvent(TimelineEvent(title = "Launched LifeOS Project", description = "Initial architectural design & Room persistence setup.", dateString = "2026-08-12"))
        }
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
