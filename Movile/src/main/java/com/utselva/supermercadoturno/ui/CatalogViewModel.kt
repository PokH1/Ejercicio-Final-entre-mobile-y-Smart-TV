package com.utselva.supermercadoturno.ui

import androidx.lifecycle.ViewModel
import com.utselva.supermercadoturno.data.ProductCatalog
import com.utselva.supermercadoturno.domain.CartLine
import com.utselva.supermercadoturno.domain.Product
import com.utselva.supermercadoturno.domain.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CatalogUiState(
    val products: List<Product> = ProductCatalog.products,
    val selectedCategory: ProductCategory? = null,
    val quantities: Map<String, Int> = emptyMap()
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

class CatalogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

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
}

