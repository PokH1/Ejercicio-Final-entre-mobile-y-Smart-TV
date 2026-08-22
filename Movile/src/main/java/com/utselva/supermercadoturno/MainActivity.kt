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
        CatalogScreen(
            state = state,
            onCategorySelected = viewModel::selectCategory,
            onAdd = viewModel::add,
            onRemove = viewModel::remove,
            onOpenCart = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    MercadoTurnoApp()
}
