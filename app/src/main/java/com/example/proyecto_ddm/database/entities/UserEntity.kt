package com.example.proyecto_ddm.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Users",
    foreignKeys = [ForeignKey(
        entity = RoleEntity::class,
        parentColumns = ["rol_id"],
        childColumns = ["rol_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("rol_id"), Index("email", unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val user_id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val rol_id: Int,
    val img_path: String? = null
)