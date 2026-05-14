package com.example.nallanudiapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {

    // Insert all words
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)

    // Get single word
    @Query("SELECT * FROM words WHERE english = :word LIMIT 1")
    suspend fun getWord(word: String): Word?

    // Get words by subject
    @Query("SELECT * FROM words WHERE subject = :subject")
    suspend fun getWordsBySubject(subject: String): List<Word>

    // Get all words
    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<Word>
}