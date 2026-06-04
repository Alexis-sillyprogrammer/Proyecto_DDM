package com.example.proyecto_ddm.entities

data class Cart(
    val id: Int,
    val userId: Int,
    val state: State,
    val creationDate: String,
    val completedDate: String? = null,
    val items: List<CartItem> = emptyList()
) {
    val subtotal: Float get() = items.sumOf { it.subtotal.toDouble() }.toFloat()
    val iva: Float get() = subtotal * 0.16f
    val total: Float get() = subtotal + iva
}