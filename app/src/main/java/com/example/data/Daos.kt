package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeOSDao {

    // Preferences
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreferences(): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(prefs: UserPreferences)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, isPriorityTop3 DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)

    // Habits
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY id DESC")
    fun getAllHabits(): Flow<List<HabitItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitItem): Long

    @Update
    suspend fun updateHabit(habit: HabitItem)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: Long)

    // Habit Logs
    @Query("SELECT * FROM habit_logs WHERE dateString = :dateString")
    fun getHabitLogsForDate(dateString: String): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs ORDER BY id DESC")
    fun getAllHabitLogs(): Flow<List<HabitLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteHabitLog(habitId: Long, dateString: String)

    // Goals
    @Query("SELECT * FROM goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<GoalItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalItem): Long

    @Update
    suspend fun updateGoal(goal: GoalItem)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: Long)

    // Milestones
    @Query("SELECT * FROM milestones WHERE goalId = :goalId")
    fun getMilestonesForGoal(goalId: Long): Flow<List<MilestoneItem>>

    @Query("SELECT * FROM milestones ORDER BY id DESC")
    fun getAllMilestones(): Flow<List<MilestoneItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneItem)

    @Update
    suspend fun updateMilestone(milestone: MilestoneItem)

    @Query("DELETE FROM milestones WHERE id = :id")
    suspend fun deleteMilestone(id: Long)

    // Projects
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectItem)

    // Study
    @Query("SELECT * FROM study_subjects ORDER BY name ASC")
    fun getAllStudySubjects(): Flow<List<StudySubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySubject(subject: StudySubject)

    @Query("SELECT * FROM study_sessions ORDER BY dateTimestamp DESC")
    fun getAllStudySessions(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySession)

    // Focus
    @Query("SELECT * FROM focus_sessions ORDER BY completedTimestamp DESC")
    fun getAllFocusSessions(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession)

    // Notes
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteItem)

    @Update
    suspend fun updateNote(note: NoteItem)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    // Books
    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAllBooks(): Flow<List<BookItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookItem)

    @Update
    suspend fun updateBook(book: BookItem)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBook(id: Long)

    // Ideas
    @Query("SELECT * FROM ideas ORDER BY createdAt DESC")
    fun getAllIdeas(): Flow<List<IdeaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: IdeaItem)

    @Update
    suspend fun updateIdea(idea: IdeaItem)

    @Query("DELETE FROM ideas WHERE id = :id")
    suspend fun deleteIdea(id: Long)

    // Finance
    @Query("SELECT * FROM finance_transactions ORDER BY dateTimestamp DESC")
    fun getAllFinanceTransactions(): Flow<List<FinanceTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinanceTransaction(transaction: FinanceTransaction)

    @Query("DELETE FROM finance_transactions WHERE id = :id")
    suspend fun deleteFinanceTransaction(id: Long)

    // Alarms
    @Query("SELECT * FROM alarms ORDER BY timeHour ASC, timeMinute ASC")
    fun getAllAlarms(): Flow<List<AlarmItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmItem): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmItem)

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarm(id: Long)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // Routines
    @Query("SELECT * FROM routines ORDER BY time ASC")
    fun getAllRoutines(): Flow<List<RoutineItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineItem)

    @Update
    suspend fun updateRoutine(routine: RoutineItem)

    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutine(id: Long)

    // Timeline
    @Query("SELECT * FROM timeline_events ORDER BY id DESC")
    fun getAllTimelineEvents(): Flow<List<TimelineEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineEvent(event: TimelineEvent)
}
