package com.example.nallanudiapp



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nallanudiapp.SavedWord
import com.example.nallanudiapp.SavedWordDao

import com.example.nallanudiapp.Word
import com.example.nallanudiapp.WordDao
import com.example.nallanudiapp.User
import com.example.nallanudiapp.UserDao

@Database(
    entities = [Word::class, User::class, SavedWord::class, QuizStats::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun userDao(): UserDao
    abstract fun savedWordDao(): SavedWordDao
    abstract fun quizStatsDao(): QuizStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}