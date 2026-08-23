package com.utselva.supermercadoturno.protocol

import kotlinx.serialization.Serializable

const val PROTOCOL_VERSION = 1

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
    val protocolVersion: Int = PROTOCOL_VERSION,
    val orderId: String,
    val customerName: String,
    val createdAtEpochMillis: Long,
    val items: List<OrderLinePayload>,
    val total: Double
)

@Serializable
data class TvTurnResponse(
    val type: String = "order.turn",
    val protocolVersion: Int = PROTOCOL_VERSION,
    val orderId: String,
    val ticketNumber: String,
    val queuePosition: Int,
    val message: String = "Tu pedido está en la fila"
)

@Serializable
data class TurnCalledResponse(
    val type: String = "order.called",
    val protocolVersion: Int = PROTOCOL_VERSION,
    val orderId: String,
    val ticketNumber: String,
    val checkout: String = "Caja 1",
    val message: String = "¡Es tu turno! Pasa a la caja"
)

@Serializable
data class TvErrorResponse(
    val type: String = "order.error",
    val protocolVersion: Int = PROTOCOL_VERSION,
    val orderId: String? = null,
    val message: String
)

@Serializable
data class MessageEnvelope(val type: String)
