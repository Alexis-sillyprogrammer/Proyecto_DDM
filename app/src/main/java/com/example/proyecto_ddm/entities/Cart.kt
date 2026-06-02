package com.example.proyecto_ddm.entities

data class Cart(
    val id: Int,
    val userId: Int,
    val state: State,
    val creationDate: String,
    val completedDate: String? = null
)