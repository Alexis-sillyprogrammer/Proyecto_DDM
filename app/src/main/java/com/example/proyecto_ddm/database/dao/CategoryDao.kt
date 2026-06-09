package com.example.proyecto_ddm.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyecto_ddm.database.entities.CategoryEntity

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Query("SELECT * FROM Categories")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM Categories WHERE category_id = :id LIMIT 1")
    suspend fun getById(id: Int): CategoryEntity?
}