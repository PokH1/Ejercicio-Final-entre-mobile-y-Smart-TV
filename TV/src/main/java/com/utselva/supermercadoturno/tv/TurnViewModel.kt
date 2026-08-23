package com.utselva.supermercadoturno.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utselva.supermercadoturno.protocol.OrderRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.java_websocket.WebSocket

data class QueuedOrder(val ticket: String, val order: OrderRequest)
data class TvUiState(
    val serverStatus: String = "Iniciando servidor…",
    val queue: List<QueuedOrder> = emptyList(),
    val nowServing: QueuedOrder? = null,
    val secondsPerTurn: Int = 15
)

class TurnViewModel : ViewModel() {
    private val _state = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()
    private var nextTicket = 1
    private val server = TurnServer(onOrder = ::enqueue, onStatus = ::setStatus)

    init {
        server.isReuseAddr = true
        server.start()
        viewModelScope.launch {
            while (isActive) {
                if (_state.value.queue.isEmpty()) {
                    delay(250)
                } else {
                    delay(15_000)
                    callNext()
                }
            }
        }
    }

    @Synchronized
    private fun enqueue(order: OrderRequest, connection: WebSocket) {
        if (_state.value.queue.any { it.order.orderId == order.orderId }) return
        val queued = QueuedOrder(ticket = "Cliente #${nextTicket++}", order = order)
        _state.update { it.copy(queue = it.queue + queued) }
        server.acknowledge(order.orderId, queued.ticket, _state.value.queue.size)
    }

    @Synchronized
    private fun callNext() {
        val next = _state.value.queue.firstOrNull() ?: return
        _state.update { it.copy(nowServing = next, queue = it.queue.drop(1)) }
        server.call(next.order.orderId, next.ticket)
    }

    private fun setStatus(message: String) = _state.update { it.copy(serverStatus = message) }

    override fun onCleared() {
        runCatching { server.stop(1_000) }
        super.onCleared()
    }
}
