package com.utselva.supermercadoturno.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.net.NetworkInterface
import java.text.NumberFormat
import java.util.Locale

private val Forest = Color(0xFF12372A)
private val Mango = Color(0xFFF6BD60)
private val Cream = Color(0xFFFFF8F0)

class TvActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TvApp() }
    }
}

@Composable
fun TvApp(viewModel: TurnViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    MaterialTheme(colorScheme = darkColorScheme(primary = Mango, background = Forest, surface = Color(0xFF1D4A39))) {
        Row(Modifier.fillMaxSize().background(Forest).padding(32.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            NowServingPanel(state, Modifier.weight(0.9f))
            QueuePanel(state, Modifier.weight(1.6f))
        }
    }
}

@Composable
private fun NowServingPanel(state: TvUiState, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("MERCADO LA SELVA", color = Mango, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text("Turno en caja", color = Cream, fontWeight = FontWeight.Bold, fontSize = 34.sp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Mango)
        ) {
            Column(Modifier.fillMaxWidth().padding(vertical = 38.dp, horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                val current = state.nowServing
                Text(current?.ticket ?: "—", color = Forest, fontWeight = FontWeight.Black, fontSize = 48.sp, textAlign = TextAlign.Center)
                Text(current?.order?.customerName ?: "Esperando clientes", color = Forest, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                if (current != null) Text("Pasa a Caja 1", color = Forest, fontSize = 21.sp, modifier = Modifier.padding(top = 12.dp))
            }
        }
        Text("Avance automático cada ${state.secondsPerTurn} segundos", color = Color(0xFFD8F3DC), fontSize = 17.sp, textAlign = TextAlign.Center)
        Text(state.serverStatus, color = Color(0xFFB7D8C7), fontSize = 15.sp, textAlign = TextAlign.Center)
        Text("Conecta el móvil a\nws://${localIpv4()}:8080/orders", color = Mango, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun QueuePanel(state: TvUiState, modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column {
                Text("Pedidos recibidos", color = Cream, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Text("Lista de productos por cliente", color = Color(0xFFB7D8C7), fontSize = 18.sp)
            }
            Box(Modifier.background(Mango, RoundedCornerShape(18.dp)).padding(horizontal = 18.dp, vertical = 9.dp)) {
                Text("${state.queue.size} en espera", color = Forest, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
        }
        if (state.queue.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Los pedidos enviados desde el móvil aparecerán aquí", color = Color(0xFFB7D8C7), fontSize = 22.sp, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.queue, key = { it.order.orderId }) { queued -> OrderCard(queued) }
            }
        }
    }
}

@Composable
private fun OrderCard(queued: QueuedOrder) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF4E9))) {
        Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.spacedBy(22.dp)) {
            Column(Modifier.width(180.dp)) {
                Text(queued.ticket, color = Forest, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Text(queued.order.customerName, color = Color(0xFF345A49), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                queued.order.items.forEach { line ->
                    Text("${line.quantity} × ${line.name}", color = Color(0xFF1D2A24), fontSize = 17.sp)
                }
            }
            Text(currency(queued.order.total), color = Forest, fontSize = 21.sp, fontWeight = FontWeight.Black)
        }
    }
}

private fun currency(value: Double): String = NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(value)

private fun localIpv4(): String = runCatching {
    NetworkInterface.getNetworkInterfaces().toList()
        .flatMap { it.inetAddresses.toList() }
        .firstOrNull { !it.isLoopbackAddress && it.hostAddress?.contains(':') == false }
        ?.hostAddress
}.getOrNull() ?: "IP-DE-LA-TV"
