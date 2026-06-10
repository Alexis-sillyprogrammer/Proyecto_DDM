package com.example.proyecto_ddm.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Cart_Detail",
    foreignKeys = [
        ForeignKey(
            entity = CartEntity::class,
            parentColumns = ["cart_id"],
            childColumns = ["cart_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cart_id"), Index("product_id")]
)
data class CartDetailEntity(
    @PrimaryKey(autoGenerate = true)
    val cart_detail_id: Int = 0,
    val cart_id: Int,
    val product_id: Int,
    val quantity: Int,
    val price_captured: Float
)
