package com.example.proyecto_ddm.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Roles")
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    val rol_id: Int = 0,
    val name: String
)