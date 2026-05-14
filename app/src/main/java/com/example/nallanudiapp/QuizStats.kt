package com.example.nallanudiapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_stats")
data class QuizStats(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val score: Int,

    val totalQuestions: Int,

    val date: Long = System.currentTimeMillis()
)