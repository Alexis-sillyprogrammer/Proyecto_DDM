package com.example.proyecto_ddm.models

data class FlatPurchaseItem (
    val cart: Cart,
    val cartItem: CartItem
) {
    val dateLabel: String get() = cart.completedDate ?: "Fecha no disponible"
    val statusName: String get() = cart.state.name.lowercase()
    val displayStatus: String
        get() = when (cart.state.name.lowercase()) {
            "completado" -> "Entregado"
            "proceso" -> "En proceso"
            "pendiente" -> "Pendiente"
            else -> cart.state.name.replaceFirstChar { it.uppercase() }
        }
}