package com.example.nallanudiapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuizStatsDao {

    @Insert
    suspend fun insertQuiz(stats: QuizStats)

    @Query("SELECT * FROM quiz_stats")
    suspend fun getAllQuizStats(): List<QuizStats>

    @Query("SELECT MAX(score) FROM quiz_stats")
    suspend fun getHighestScore(): Int?

    @Query("SELECT COUNT(*) FROM quiz_stats")
    suspend fun getQuizCount(): Int
}