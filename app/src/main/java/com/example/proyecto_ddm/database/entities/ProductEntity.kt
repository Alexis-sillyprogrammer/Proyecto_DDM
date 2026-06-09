package com.example.proyecto_ddm.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["id_admin"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("category_id"), Index("id_admin")]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val product_id: Int = 0,
    val name: String,
    val category_id: Int,
    val description: String,
    val price: Float,
    val stock: Int,
    val img_path: String?,
    val id_admin: Int?
)