package com.example.proyecto_ddm.models

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val subtotal: Float get() = product.price * quantity
}