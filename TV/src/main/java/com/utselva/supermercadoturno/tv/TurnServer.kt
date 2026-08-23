package com.utselva.supermercadoturno.tv

import com.utselva.supermercadoturno.protocol.MessageEnvelope
import com.utselva.supermercadoturno.protocol.OrderRequest
import com.utselva.supermercadoturno.protocol.TurnCalledResponse
import com.utselva.supermercadoturno.protocol.TvErrorResponse
import com.utselva.supermercadoturno.protocol.TvTurnResponse
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

class TurnServer(
    port: Int = 8080,
    private val onOrder: (OrderRequest, WebSocket) -> Unit,
    private val onStatus: (String) -> Unit
) : WebSocketServer(InetSocketAddress("0.0.0.0", port)) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val orderConnections = ConcurrentHashMap<String, WebSocket>()

    override fun onStart() = onStatus("Servidor activo en el puerto $port")

    override fun onOpen(connection: WebSocket, handshake: ClientHandshake) {
        if (handshake.resourceDescriptor != "/orders") {
            connection.close(1008, "Ruta no válida; usa /orders")
        }
    }

    override fun onMessage(connection: WebSocket, message: String) {
        try {
            if (json.decodeFromString(MessageEnvelope.serializer(), message).type != "order.create") {
                sendError(connection, null, "Tipo de mensaje no compatible")
                return
            }
            val order = json.decodeFromString(OrderRequest.serializer(), message)
            val error = validate(order)
            if (error != null) sendError(connection, order.orderId, error)
            else {
                orderConnections[order.orderId] = connection
                onOrder(order, connection)
            }
        } catch (_: SerializationException) {
            sendError(connection, null, "El pedido contiene JSON inválido")
        }
    }

    fun acknowledge(orderId: String, ticket: String, position: Int) {
        orderConnections[orderId]?.send(json.encodeToString(TvTurnResponse.serializer(), TvTurnResponse(
            orderId = orderId,
            ticketNumber = ticket,
            queuePosition = position,
            message = "$ticket registrado. Mantén abierta la aplicación"
        )))
    }

    fun call(orderId: String, ticket: String) {
        orderConnections.remove(orderId)?.send(json.encodeToString(TurnCalledResponse.serializer(), TurnCalledResponse(
            orderId = orderId,
            ticketNumber = ticket
        )))
    }

    override fun onClose(connection: WebSocket, code: Int, reason: String, remote: Boolean) {
        orderConnections.entries.removeIf { it.value == connection }
    }

    override fun onError(connection: WebSocket?, exception: Exception) {
        onStatus("Error de red: ${exception.message ?: "desconocido"}")
    }

    private fun sendError(connection: WebSocket, orderId: String?, message: String) {
        connection.send(json.encodeToString(TvErrorResponse.serializer(), TvErrorResponse(orderId = orderId, message = message)))
    }

    private fun validate(order: OrderRequest): String? = when {
        order.protocolVersion != 1 -> "Versión de protocolo no compatible"
        order.orderId.isBlank() -> "Falta el identificador del pedido"
        order.customerName.isBlank() -> "Falta el nombre del cliente"
        order.items.isEmpty() -> "El pedido debe incluir productos"
        order.items.any { it.quantity <= 0 || it.name.isBlank() } -> "Hay productos no válidos"
        else -> null
    }
}
