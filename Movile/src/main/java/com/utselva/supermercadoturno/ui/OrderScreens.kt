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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utselva.supermercadoturno.ui.theme.Forest
import com.utselva.supermercadoturno.ui.theme.Mango

@Composable
fun CartScreen(
    state: CatalogUiState,
    onBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) { Text("← Seguir comprando") }
            Text("Revisa tu pedido", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Forest)
            Text("Confirma los productos antes de enviarlos a la pantalla de la tienda.")

            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.cartLines.forEachIndexed { index, line ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("${line.product.emoji}  ${line.product.name}", fontWeight = FontWeight.SemiBold)
                                Text("${line.quantity} × ${line.product.price.asCurrency()}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(line.subtotal.asCurrency(), fontWeight = FontWeight.Bold)
                        }
                        if (index < state.cartLines.lastIndex) HorizontalDivider(color = Color(0xFFE7E8E5))
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(state.total.asCurrency(), fontWeight = FontWeight.Black, fontSize = 20.sp, color = Forest)
                    }
                }
            }

            OutlinedTextField(
                value = state.customerName,
                onValueChange = onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre para recoger") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            state.statusMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp)) {
                Text("Confirmar pedido", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WaitingScreen(message: String?, onCancel: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Forest).padding(28.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CircularProgressIndicator(color = Mango, modifier = Modifier.size(56.dp))
            Text("Preparando tu pedido", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(message ?: "Estamos asignando tu turno…", color = Color(0xFFD8F3DC), textAlign = TextAlign.Center)
            TextButton(onClick = onCancel) { Text("Cancelar", color = Mango) }
        }
    }
}

@Composable
fun TurnScreen(state: CatalogUiState) {
    val turn = state.turn ?: return
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(Modifier.size(90.dp).background(Mango, CircleShape), contentAlignment = Alignment.Center) {
                Text("✓", fontSize = 48.sp, color = Forest, fontWeight = FontWeight.Black)
            }
            Text("¡Estás en la fila!", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Forest)
            Text(turn.message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.outline)
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Forest)) {
                Column(Modifier.padding(horizontal = 44.dp, vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CLIENTE", color = Mango, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(turn.ticketNumber, color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(4.dp))
                    Text("Posición ${turn.queuePosition} · espera el aviso", color = Color(0xFFD8F3DC), textAlign = TextAlign.Center)
                }
            }
            Text("Mantén abierta la aplicación para recibir la alerta.", textAlign = TextAlign.Center, color = Forest, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CalledScreen(state: CatalogUiState, onNewOrder: () -> Unit) {
    val called = state.calledTurn ?: return
    Box(Modifier.fillMaxSize().background(Mango).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("🔔", fontSize = 72.sp)
            Text("¡ES TU TURNO!", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Forest, textAlign = TextAlign.Center)
            Text(called.ticketNumber, fontSize = 62.sp, fontWeight = FontWeight.Black, color = Forest)
            Text("Pasa ahora a ${called.checkout}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Forest, textAlign = TextAlign.Center)
            Text(called.message, color = Forest, textAlign = TextAlign.Center)
            Button(onClick = onNewOrder, modifier = Modifier.fillMaxWidth().height(58.dp)) { Text("Entendido") }
        }
    }
}
