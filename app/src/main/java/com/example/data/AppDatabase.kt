package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserPreferences::class,
        TaskItem::class,
        HabitItem::class,
        HabitLog::class,
        GoalItem::class,
        MilestoneItem::class,
        ProjectItem::class,
        StudySubject::class,
        StudySession::class,
        FocusSession::class,
        NoteItem::class,
        BookItem::class,
        IdeaItem::class,
        FinanceTransaction::class,
        AlarmItem::class,
        NotificationItem::class,
        RoutineItem::class,
        TimelineEvent::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): LifeOSDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifeos_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
