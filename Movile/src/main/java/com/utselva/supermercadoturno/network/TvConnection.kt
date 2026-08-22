package com.utselva.supermercadoturno.network

import com.utselva.supermercadoturno.domain.OrderRequest
import com.utselva.supermercadoturno.domain.TvTurnResponse
import kotlinx.coroutines.flow.SharedFlow

sealed interface TvConnectionEvent {
    data object Connecting : TvConnectionEvent
    data object Connected : TvConnectionEvent
    data class TurnReceived(val response: TvTurnResponse) : TvConnectionEvent
    data class Disconnected(val reason: String) : TvConnectionEvent
    data class Error(val message: String) : TvConnectionEvent
}

interface TvConnection {
    val events: SharedFlow<TvConnectionEvent>
    fun connect(endpoint: String)
    fun send(order: OrderRequest): Boolean
    fun disconnect()
}

