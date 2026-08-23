package com.utselva.supermercadoturno

import android.os.Bundle
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utselva.supermercadoturno.ui.CatalogScreen
import com.utselva.supermercadoturno.ui.CatalogViewModel
import com.utselva.supermercadoturno.ui.AppScreen
import com.utselva.supermercadoturno.ui.CartScreen
import com.utselva.supermercadoturno.ui.CalledScreen
import com.utselva.supermercadoturno.ui.TurnScreen
import com.utselva.supermercadoturno.ui.WaitingScreen
import com.utselva.supermercadoturno.ui.theme.MercadoTheme

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTurnChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        setContent { MercadoTurnoApp(::showTurnNotification) }
    }

    private fun createTurnChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("turns", "Avisos de turno", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Avisa cuando el cliente debe pasar a caja"
                    enableVibration(true)
                }
            )
        }
    }

    private fun showTurnNotification(ticket: String, checkout: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        val notification = NotificationCompat.Builder(this, "turns")
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle("¡Es tu turno! $ticket")
            .setContentText("Pasa ahora a $checkout")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(ticket.hashCode(), notification)
    }
}

@Composable
fun MercadoTurnoApp(onTurnCalled: (String, String) -> Unit = { _, _ -> }) {
    val viewModel: CatalogViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.calledTurn?.orderId) {
        state.calledTurn?.let { onTurnCalled(it.ticketNumber, it.checkout) }
    }
    MercadoTheme {
        when (state.screen) {
            AppScreen.CATALOG -> CatalogScreen(
                state = state,
                onCategorySelected = viewModel::selectCategory,
                onAdd = viewModel::add,
                onRemove = viewModel::remove,
                onOpenCart = viewModel::openCart
            )
            AppScreen.CART -> CartScreen(
                state = state,
                onBack = viewModel::backToCatalog,
                onNameChanged = viewModel::updateCustomerName,
                onSubmit = viewModel::submitOrder
            )
            AppScreen.WAITING -> WaitingScreen(state.statusMessage, viewModel::backToCatalog)
            AppScreen.TURN -> TurnScreen(state)
            AppScreen.CALLED -> CalledScreen(state, viewModel::startAnotherOrder)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    MercadoTurnoApp()
}
