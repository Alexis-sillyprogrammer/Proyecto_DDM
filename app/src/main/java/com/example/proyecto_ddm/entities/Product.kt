package com.example.proyecto_ddm.entities

import com.example.proyecto_ddm.entities.Category

data class Product (
    val id: Int,
    val name: String,
    val category: Category,
    val description: String,
    val price: Float,
    val img: String? = null
)