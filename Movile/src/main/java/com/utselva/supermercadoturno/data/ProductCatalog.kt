package com.utselva.supermercadoturno.data

import com.utselva.supermercadoturno.domain.Product
import com.utselva.supermercadoturno.domain.ProductCategory

object ProductCatalog {
    val products = listOf(
        Product("fruit-banana", "Plátano", "Manojo fresco de Chiapas", 28.90, "kg", ProductCategory.FRUITS, "🍌"),
        Product("fruit-apple", "Manzana roja", "Crujiente y dulce", 54.50, "kg", ProductCategory.FRUITS, "🍎"),
        Product("fruit-avocado", "Aguacate Hass", "Listo para consumir", 76.00, "kg", ProductCategory.FRUITS, "🥑"),
        Product("bakery-bread", "Pan integral", "Horneado del día, 680 g", 49.90, "pieza", ProductCategory.BAKERY, "🍞"),
        Product("bakery-tortilla", "Tortilla de maíz", "Paquete de 1 kg", 24.00, "paquete", ProductCategory.BAKERY, "🌽"),
        Product("dairy-milk", "Leche entera", "Envase de 1 litro", 29.50, "pieza", ProductCategory.DAIRY, "🥛"),
        Product("dairy-yogurt", "Yogur natural", "Sin azúcar, 900 g", 58.00, "pieza", ProductCategory.DAIRY, "🥣"),
        Product("dairy-cheese", "Queso panela", "Paquete de 400 g", 72.90, "pieza", ProductCategory.DAIRY, "🧀"),
        Product("pantry-rice", "Arroz", "Grano largo, bolsa de 1 kg", 36.50, "bolsa", ProductCategory.PANTRY, "🍚"),
        Product("pantry-beans", "Frijol negro", "Bolsa de 900 g", 42.00, "bolsa", ProductCategory.PANTRY, "🫘"),
        Product("drink-water", "Agua mineral", "Botella de 1 litro", 21.00, "pieza", ProductCategory.DRINKS, "💧"),
        Product("drink-juice", "Jugo de naranja", "Sin azúcar añadida, 1 litro", 43.50, "pieza", ProductCategory.DRINKS, "🍊")
    )
}

