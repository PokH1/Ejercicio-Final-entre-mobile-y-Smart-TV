package com.utselva.supermercadoturno.domain

import com.utselva.supermercadoturno.protocol.OrderLinePayload
import com.utselva.supermercadoturno.protocol.OrderRequest

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
