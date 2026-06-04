package com.example.proyecto_ddm.entities

data class FlatPurchaseItem (
    val cart: Cart,
    val cartItem: CartItem
) {
    val dateLabel: String get() = cart.completedDate ?: "Fecha no disponible"
    val statusName: String get() = cart.state.name.lowercase()
}