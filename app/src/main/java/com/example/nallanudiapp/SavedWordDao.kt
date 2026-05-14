package com.example.nallanudiapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SavedWordDao {

    @Insert
    suspend fun insertSavedWord(word: SavedWord)

    @Query("SELECT * FROM SavedWord")
    suspend fun getAllSavedWords(): List<SavedWord>

    @Delete
    suspend fun deleteWord(word: SavedWord)
}