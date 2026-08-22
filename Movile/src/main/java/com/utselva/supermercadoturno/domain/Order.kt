package com.utselva.supermercadoturno.domain

import kotlinx.serialization.Serializable

@Serializable
data class OrderLinePayload(
    val productId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)

@Serializable
data class OrderRequest(
    val type: String = "order.create",
    val protocolVersion: Int = 1,
    val orderId: String,
    val customerName: String,
    val createdAtEpochMillis: Long,
    val items: List<OrderLinePayload>,
    val total: Double
)

@Serializable
data class TvTurnResponse(
    val type: String = "order.turn",
    val protocolVersion: Int = 1,
    val orderId: String,
    val ticketNumber: String,
    val estimatedMinutes: Int,
    val message: String = "Pedido recibido"
)

@Serializable
data class TvErrorResponse(
    val type: String = "order.error",
    val orderId: String? = null,
    val message: String
)

fun List<CartLine>.toOrderRequest(
    orderId: String,
    customerName: String,
    createdAtEpochMillis: Long
): OrderRequest = OrderRequest(
    orderId = orderId,
    customerName = customerName.trim(),
    createdAtEpochMillis = createdAtEpochMillis,
    items = map { line ->
        OrderLinePayload(
            productId = line.product.id,
            name = line.product.name,
            unitPrice = line.product.price,
            quantity = line.quantity,
            subtotal = line.subtotal
        )
    },
    total = sumOf(CartLine::subtotal)
)
