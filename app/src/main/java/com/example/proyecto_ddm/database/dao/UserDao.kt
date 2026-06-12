package com.example.proyecto_ddm.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyecto_ddm.database.entities.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM Users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM Users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM Users WHERE user_id = :id LIMIT 1")
    suspend fun getById(id: Int): UserEntity?

    @Query("UPDATE Users SET password = :newPassword WHERE user_id = :id")
    suspend fun updatePasswordById(id: Int, newPassword: String)

    @Query("UPDATE Users SET name = :name, email = :email, img_path = :imgPath WHERE user_id = :id")
    suspend fun updateProfile(id: Int, name: String, email: String, imgPath: String?)
}