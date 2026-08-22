package com.utselva.supermercadoturno.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Forest = Color(0xFF12372A)
val Leaf = Color(0xFF2D6A4F)
val Cream = Color(0xFFFFF8F0)
val Mango = Color(0xFFF6BD60)
val Clay = Color(0xFFD96C3D)
val Ink = Color(0xFF1D2A24)

private val MercadoColors = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F3DC),
    onPrimaryContainer = Forest,
    secondary = Clay,
    secondaryContainer = Color(0xFFFFE2D5),
    background = Cream,
    surface = Color.White,
    onSurface = Ink,
    outline = Color(0xFF718078)
)

@Composable
fun MercadoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MercadoColors, content = content)
}

