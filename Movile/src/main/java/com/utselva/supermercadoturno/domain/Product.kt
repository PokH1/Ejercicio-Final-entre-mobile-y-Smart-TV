package com.utselva.supermercadoturno.domain

import kotlinx.serialization.Serializable

@Serializable
enum class ProductCategory(val label: String) {
    FRUITS("Frutas"),
    BAKERY("Panadería"),
    DAIRY("Lácteos"),
    PANTRY("Despensa"),
    DRINKS("Bebidas")
}

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val unit: String,
    val category: ProductCategory,
    val emoji: String
)

data class CartLine(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Double get() = product.price * quantity
}

