package com.example.proyecto_ddm.models

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val imageUrl: String = "" // Lo dejamos vacío por ahora hasta que integren imágenes reales
)