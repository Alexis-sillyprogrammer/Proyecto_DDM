package com.example.proyecto_ddm.models

data class Product (
    val id: Int,
    val name: String,
    val category: Category,
    val description: String,
    val price: Float,
    val img: String? = null
)