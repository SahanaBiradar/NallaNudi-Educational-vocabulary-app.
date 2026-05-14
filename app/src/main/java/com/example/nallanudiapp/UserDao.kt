package com.example.nallanudiapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    // ✅ INSERT USER
    @Insert
    suspend fun insertUser(user: User)

    // ✅ GET USER BY EMAIL
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // ✅ GET USER BY PHONE
    @Query("SELECT * FROM user WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    // ✅ LOGIN USING EMAIL OR PHONE
    @Query(
        "SELECT * FROM user WHERE (email = :input OR phone = :input) AND password = :password LIMIT 1"
    )
    suspend fun login(input: String, password: String): User?

    // ✅ GET ALL USERS
    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    // ✅ UPDATE USER
    @Update
    suspend fun updateUser(user: User)
}