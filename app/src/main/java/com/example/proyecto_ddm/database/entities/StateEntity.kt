package com.example.proyecto_ddm.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "States")
data class StateEntity(
    @PrimaryKey(autoGenerate = true)
    val state_id: Int = 0,
    val name: String
)
