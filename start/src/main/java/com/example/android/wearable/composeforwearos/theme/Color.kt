package com.example.android.wearable.composeforwearos.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme

val GreenDark = Color(0xFF254C34)     // Verde oscuro
val GreenStrong = Color(0xFF159648)   // Verde principal
val YellowVibrant = Color(0xFFF9D813) // Amarillo fuerte
val YellowLight = Color(0xFFFFF212)   // Amarillo claro
val ErrorRed = Color(0xFFED3237)      // Rojo para errores

val WearAppColorPalette: ColorScheme = ColorScheme(
    primary = GreenStrong,
    primaryDim = GreenDark,
    secondary = YellowVibrant,
    secondaryDim = YellowLight,
    error = ErrorRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onError = Color.Black
)
