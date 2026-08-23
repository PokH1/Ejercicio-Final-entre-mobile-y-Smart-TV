package com.utselva.supermercadoturno.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utselva.supermercadoturno.domain.Product
import com.utselva.supermercadoturno.domain.ProductCategory
import com.utselva.supermercadoturno.ui.theme.Forest
import com.utselva.supermercadoturno.ui.theme.Mango
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CatalogScreen(
    state: CatalogUiState,
    onCategorySelected: (ProductCategory?) -> Unit,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onOpenCart: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (state.itemCount > 0) {
                CartBar(state.itemCount, state.total, onOpenCart)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Header() }
            item {
                CategoryFilters(state.selectedCategory, onCategorySelected)
            }
            items(state.visibleProducts, key = Product::id) { product ->
                ProductCard(
                    product = product,
                    quantity = state.quantities[product.id] ?: 0,
                    onAdd = { onAdd(product.id) },
                    onRemove = { onRemove(product.id) }
                )
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth().background(Forest).padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text("MERCADO LA SELVA", color = Mango, fontWeight = FontWeight.Black, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Text("Tu súper, sin filas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text("Elige tus productos y recibe tu turno al instante.", color = Color(0xFFD8F3DC), fontSize = 15.sp)
    }
}

@Composable
private fun CategoryFilters(selected: ProductCategory?, onSelected: (ProductCategory?) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { FilterChip(selected == null, { onSelected(null) }, { Text("Todo") }) }
        items(ProductCategory.entries) { category ->
            FilterChip(selected == category, { onSelected(category) }, { Text(category.label) })
        }
    }
}

@Composable
private fun ProductCard(product: Product, quantity: Int, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(68.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFFFF0D6)),
                contentAlignment = Alignment.Center
            ) { Text(product.emoji, fontSize = 34.sp) }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(product.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(6.dp))
                Text("${product.price.asCurrency()} / ${product.unit}", color = Forest, fontWeight = FontWeight.Bold)
            }
            if (quantity == 0) {
                Button(onClick = onAdd, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)) {
                    Text("Agregar")
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onRemove, modifier = Modifier.size(38.dp)) { Text("−", fontSize = 22.sp) }
                    Text(quantity.toString(), fontWeight = FontWeight.Bold)
                    TextButton(onClick = onAdd, modifier = Modifier.size(38.dp)) { Text("+", fontSize = 20.sp) }
                }
            }
        }
    }
}

@Composable
private fun CartBar(itemCount: Int, total: Double, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(58.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Forest)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Text("$itemCount", modifier = Modifier.align(Alignment.CenterStart).background(Mango, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp), color = Forest, fontWeight = FontWeight.Bold)
            Text("Ver pedido", modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold)
            Text(total.asCurrency(), modifier = Modifier.align(Alignment.CenterEnd), fontWeight = FontWeight.Bold)
        }
    }
}

fun Double.asCurrency(): String = NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(this)
