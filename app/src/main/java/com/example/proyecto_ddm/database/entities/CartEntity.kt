package com.example.proyecto_ddm.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Carts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StateEntity::class,
            parentColumns = ["state_id"],
            childColumns = ["state_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("user_id"), Index("state_id")]
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val cart_id: Int = 0,
    val user_id: Int,
    val state_id: Int,
    val creation_date: String,
    val completed_date: String? = null
)
