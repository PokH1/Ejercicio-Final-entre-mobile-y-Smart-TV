package com.utselva.supermercadoturno.ui

import com.utselva.supermercadoturno.data.ProductCatalog
import com.utselva.supermercadoturno.domain.ProductCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogUiStateTest {
    @Test
    fun `cart totals selected quantities`() {
        val first = ProductCatalog.products[0]
        val second = ProductCatalog.products[1]
        val state = CatalogUiState(quantities = mapOf(first.id to 2, second.id to 1))

        assertEquals(3, state.itemCount)
        assertEquals(first.price * 2 + second.price, state.total, 0.001)
        assertEquals(2, state.cartLines.size)
    }

    @Test
    fun `category filter only returns matching products`() {
        val state = CatalogUiState(selectedCategory = ProductCategory.DAIRY)

        assert(state.visibleProducts.isNotEmpty())
        assert(state.visibleProducts.all { it.category == ProductCategory.DAIRY })
    }
}
