package com.example.nallanudiapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavedWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val english: String,
    val kannada: String,
    val meaning: String,
    val subject: String   // ✅ important (for your dropdown feature)
)