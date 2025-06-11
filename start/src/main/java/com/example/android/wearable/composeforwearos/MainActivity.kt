package com.example.android.wearable.composeforwearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import com.example.android.wearable.composeforwearos.theme.WearAppTheme
import androidx.wear.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    WearAppTheme {
        WatchFace()
    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}

@Composable
fun WatchFace() {
    // Tamaño del contorno
    val borderThickness = 6.dp
    val circleSize = 180.dp // Ajusta según el reloj

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Círculo con borde degradado
        Canvas(modifier = Modifier.size(circleSize)) {
            // Access toPx() here, within the DrawScope
            val strokeWidthPx = borderThickness.toPx()
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Green, // Replace with your actual GreenStrong
                        Color.DarkGray, // Replace with your actual GreenDark
                        Color.Yellow, // Replace with your actual YellowVibrant
                        Color.White,  // Replace with your actual YellowLight
                        Color.Red,    // Replace with your actual ErrorRed
                        Color.Green  // Replace with your actual GreenStrong
                    )
                ),
                style = Stroke(width = strokeWidthPx)
            )
        }

        // Contenido dentro del círculo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(circleSize - borderThickness * 2) // Reducir para no solapar el borde
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo CRESPF",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            val currentTime = remember { mutableStateOf(getCurrentTime()) }

            LaunchedEffect(Unit) {
                while (true) {
                    currentTime.value = getCurrentTime()
                    delay(1000L) // 1 segundo
                }
            }

            Text(
                text = currentTime.value,
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                fontSize = 20.sp, // Ajusta el tamaño de la fuente según tus preferencias
            )

            Text(
                text = "En esta vida no solo los talentos son los que triunfan, también las voluntades",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 7.sp,
                    lineHeight = 10.sp
                ),
                color = Color.White,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
        }
    }
}

// Define your colors if they are not already defined elsewhere
val GreenStrong = Color(0xFF008000) // Example color
val GreenDark = Color(0xFF006400)   // Example color
val YellowVibrant = Color(0xFFFFFF00) // Example color
val YellowLight = Color(0xFFFFFFE0) // Example color
val ErrorRed = Color(0xFFFF0000)    // Example color