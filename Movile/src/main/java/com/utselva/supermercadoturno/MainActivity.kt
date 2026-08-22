package com.utselva.supermercadoturno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utselva.supermercadoturno.ui.CatalogScreen
import com.utselva.supermercadoturno.ui.CatalogViewModel
import com.utselva.supermercadoturno.ui.AppScreen
import com.utselva.supermercadoturno.ui.CartScreen
import com.utselva.supermercadoturno.ui.TurnScreen
import com.utselva.supermercadoturno.ui.WaitingScreen
import com.utselva.supermercadoturno.ui.theme.MercadoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MercadoTurnoApp() }
    }
}

@Composable
fun MercadoTurnoApp() {
    val viewModel: CatalogViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
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
                onEndpointChanged = viewModel::updateTvEndpoint,
                onSubmit = viewModel::submitOrder
            )
            AppScreen.WAITING -> WaitingScreen(state.statusMessage, viewModel::backToCatalog)
            AppScreen.TURN -> TurnScreen(state, viewModel::startAnotherOrder)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    MercadoTurnoApp()
}
