package com.example.proyecto_ddm.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val category_id: Int = 0,
    val name: String
)