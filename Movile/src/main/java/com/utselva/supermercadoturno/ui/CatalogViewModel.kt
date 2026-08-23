package com.utselva.supermercadoturno.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utselva.supermercadoturno.BuildConfig
import com.utselva.supermercadoturno.data.ProductCatalog
import com.utselva.supermercadoturno.domain.CartLine
import com.utselva.supermercadoturno.domain.Product
import com.utselva.supermercadoturno.domain.ProductCategory
import com.utselva.supermercadoturno.domain.OrderRequest
import com.utselva.supermercadoturno.domain.TvTurnResponse
import com.utselva.supermercadoturno.domain.toOrderRequest
import com.utselva.supermercadoturno.network.OkHttpTvConnection
import com.utselva.supermercadoturno.network.TvConnection
import com.utselva.supermercadoturno.network.TvConnectionEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen { CATALOG, CART, WAITING, TURN }
enum class ConnectionStatus { DISCONNECTED, CONNECTING, CONNECTED, ERROR }

data class CatalogUiState(
    val products: List<Product> = ProductCatalog.products,
    val selectedCategory: ProductCategory? = null,
    val quantities: Map<String, Int> = emptyMap(),
    val screen: AppScreen = AppScreen.CATALOG,
    val customerName: String = "",
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val statusMessage: String? = null,
    val turn: TvTurnResponse? = null
) {
    val visibleProducts: List<Product>
        get() = products.filter { selectedCategory == null || it.category == selectedCategory }

    val cartLines: List<CartLine>
        get() = products.mapNotNull { product ->
            quantities[product.id]?.takeIf { it > 0 }?.let { CartLine(product, it) }
        }

    val itemCount: Int get() = quantities.values.sum()
    val total: Double get() = cartLines.sumOf(CartLine::subtotal)
}

class CatalogViewModel(
    private val tvConnection: TvConnection = OkHttpTvConnection(),
    private val orderServiceUrl: String = BuildConfig.ORDER_SERVICE_URL
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()
    private var pendingOrder: OrderRequest? = null

    init {
        viewModelScope.launch {
            tvConnection.events.collect(::handleConnectionEvent)
        }
    }

    fun selectCategory(category: ProductCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun add(productId: String) {
        _uiState.update { state ->
            val current = state.quantities[productId] ?: 0
            state.copy(quantities = state.quantities + (productId to current + 1))
        }
    }

    fun remove(productId: String) {
        _uiState.update { state ->
            val next = ((state.quantities[productId] ?: 0) - 1).coerceAtLeast(0)
            state.copy(quantities = state.quantities + (productId to next))
        }
    }

    fun openCart() {
        if (_uiState.value.itemCount > 0) _uiState.update { it.copy(screen = AppScreen.CART) }
    }

    fun backToCatalog() {
        tvConnection.disconnect()
        pendingOrder = null
        _uiState.update {
            it.copy(screen = AppScreen.CATALOG, connectionStatus = ConnectionStatus.DISCONNECTED, statusMessage = null)
        }
    }

    fun updateCustomerName(value: String) {
        _uiState.update { it.copy(customerName = value.take(60), statusMessage = null) }
    }

    fun submitOrder() {
        val state = _uiState.value
        when {
            state.customerName.isBlank() -> _uiState.update { it.copy(statusMessage = "Escribe el nombre de quien recogerá el pedido") }
            state.cartLines.isEmpty() -> _uiState.update { it.copy(statusMessage = "Agrega al menos un producto") }
            else -> {
                pendingOrder = state.cartLines.toOrderRequest(
                    orderId = "MT-${UUID.randomUUID().toString().take(8).uppercase()}",
                    customerName = state.customerName,
                    createdAtEpochMillis = System.currentTimeMillis()
                )
                _uiState.update {
                    it.copy(screen = AppScreen.WAITING, connectionStatus = ConnectionStatus.CONNECTING, statusMessage = "Registrando tu pedido…")
                }
                tvConnection.connect(orderServiceUrl)
            }
        }
    }

    fun startAnotherOrder() {
        tvConnection.disconnect()
        pendingOrder = null
        _uiState.value = CatalogUiState()
    }

    private fun handleConnectionEvent(event: TvConnectionEvent) {
        when (event) {
            TvConnectionEvent.Connecting -> _uiState.update { it.copy(connectionStatus = ConnectionStatus.CONNECTING) }
            TvConnectionEvent.Connected -> {
                val sent = pendingOrder?.let(tvConnection::send) == true
                _uiState.update {
                    it.copy(
                        connectionStatus = ConnectionStatus.CONNECTED,
                        statusMessage = if (sent) "Pedido registrado. Estamos asignando tu turno…" else "No pudimos registrar tu pedido"
                    )
                }
            }
            is TvConnectionEvent.TurnReceived -> _uiState.update {
                it.copy(screen = AppScreen.TURN, turn = event.response, quantities = emptyMap(), statusMessage = null)
            }
            is TvConnectionEvent.Disconnected -> _uiState.update {
                if (it.screen == AppScreen.TURN) it else it.copy(connectionStatus = ConnectionStatus.DISCONNECTED, statusMessage = "El servicio no está disponible. Inténtalo nuevamente.")
            }
            is TvConnectionEvent.Error -> _uiState.update {
                it.copy(screen = AppScreen.CART, connectionStatus = ConnectionStatus.ERROR, statusMessage = "No pudimos registrar tu pedido. Revisa tu conexión e inténtalo nuevamente.")
            }
        }
    }

    override fun onCleared() {
        tvConnection.disconnect()
        super.onCleared()
    }
}
