package com.utselva.supermercadoturno.domain

import com.utselva.supermercadoturno.data.ProductCatalog
import kotlinx.serialization.json.Json
import com.utselva.supermercadoturno.protocol.TvTurnResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderProtocolTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `selected products map to versioned order payload`() {
        val line = CartLine(ProductCatalog.products.first(), 2)
        val order = listOf(line).toOrderRequest("MT-123", "Ana", 1_700_000_000_000)

        assertEquals("order.create", order.type)
        assertEquals(1, order.protocolVersion)
        assertEquals(2, order.items.single().quantity)
        assertEquals(line.subtotal, order.total, 0.001)
    }

    @Test
    fun `tv turn response accepts future extra fields`() {
        val payload = """{
            "type":"order.turn",
            "protocolVersion":1,
            "orderId":"MT-123",
            "ticketNumber":"A-017",
            "queuePosition":2,
            "message":"Pasa en 12 minutos",
            "screen":"checkout-2"
        }""".trimIndent()

        val response = json.decodeFromString(TvTurnResponse.serializer(), payload)

        assertEquals("A-017", response.ticketNumber)
        assertEquals(2, response.queuePosition)
        assertTrue(response.message.contains("12"))
    }
}
