package com.utselva.supermercadoturno.network

import com.utselva.supermercadoturno.protocol.MessageEnvelope
import com.utselva.supermercadoturno.protocol.OrderRequest
import com.utselva.supermercadoturno.protocol.TurnCalledResponse
import com.utselva.supermercadoturno.protocol.TvErrorResponse
import com.utselva.supermercadoturno.protocol.TvTurnResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class OkHttpTvConnection(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .connectTimeout(8, TimeUnit.SECONDS)
        .build(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) : TvConnection {
    private val _events = MutableSharedFlow<TvConnectionEvent>(extraBufferCapacity = 16)
    override val events: SharedFlow<TvConnectionEvent> = _events.asSharedFlow()

    private var socket: WebSocket? = null

    override fun connect(endpoint: String) {
        disconnect()
        val normalized = endpoint.trim()
        if (!normalized.startsWith("ws://") && !normalized.startsWith("wss://")) {
            _events.tryEmit(TvConnectionEvent.Error("La dirección debe comenzar con ws:// o wss://"))
            return
        }
        _events.tryEmit(TvConnectionEvent.Connecting)
        val request = Request.Builder()
            .url(normalized)
            .header("X-Mercado-Protocol", "1")
            .build()
        socket = client.newWebSocket(request, listener)
    }

    override fun send(order: OrderRequest): Boolean {
        val payload = json.encodeToString(OrderRequest.serializer(), order)
        return socket?.send(payload) ?: false
    }

    override fun disconnect() {
        socket?.close(1000, "Mobile app closed connection")
        socket = null
    }

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _events.tryEmit(TvConnectionEvent.Connected)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                when (json.decodeFromString(MessageEnvelope.serializer(), text).type) {
                    "order.turn" -> _events.tryEmit(TvConnectionEvent.TurnReceived(json.decodeFromString(TvTurnResponse.serializer(), text)))
                    "order.called" -> _events.tryEmit(TvConnectionEvent.TurnCalled(json.decodeFromString(TurnCalledResponse.serializer(), text)))
                    "order.error" -> _events.tryEmit(TvConnectionEvent.Error(json.decodeFromString(TvErrorResponse.serializer(), text).message))
                    else -> _events.tryEmit(TvConnectionEvent.Error("Respuesta de TV no reconocida"))
                }
            } catch (_: SerializationException) {
                _events.tryEmit(TvConnectionEvent.Error("La TV envió una respuesta inválida"))
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(code, reason)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            socket = null
            _events.tryEmit(TvConnectionEvent.Disconnected(reason.ifBlank { "Conexión cerrada" }))
        }

        override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
            socket = null
            _events.tryEmit(TvConnectionEvent.Error(throwable.message ?: "No fue posible conectar con la TV"))
        }
    }
}

