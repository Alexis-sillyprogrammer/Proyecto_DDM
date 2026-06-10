package com.example.proyecto_ddm.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyecto_ddm.database.entities.StateEntity

@Dao
interface StateDao {
    @Insert
    suspend fun insert(state: StateEntity): Long

    @Query("SELECT * FROM States WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): StateEntity?

    @Query("SELECT * FROM States WHERE state_id = :id LIMIT 1")
    suspend fun getById(id: Int): StateEntity?
}