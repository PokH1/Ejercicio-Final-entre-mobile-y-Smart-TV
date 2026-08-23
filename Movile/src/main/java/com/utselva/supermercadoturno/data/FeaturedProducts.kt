package com.utselva.supermercadoturno.data

/**
 * Selección inicial para una futura sección de productos destacados.
 * Se mantiene separada del catálogo para poder cambiar la campaña sin alterar productos.
 */
object FeaturedProducts {
    val ids = setOf(
        "fruit-banana",
        "bakery-bread",
        "dairy-milk",
        "pantry-beans"
    )
}

