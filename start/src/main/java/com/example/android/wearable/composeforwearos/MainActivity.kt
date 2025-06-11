package com.example.android.wearable.composeforwearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.*
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.example.android.wearable.composeforwearos.theme.WearAppTheme
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

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
        var isLoggedIn by remember { mutableStateOf(false) }
        var code by remember { mutableStateOf("123456") } // Código simulado
        var error by remember { mutableStateOf(false) }

        if (!isLoggedIn) {
            ScreenScaffold {
                ScalingLazyColumn {
                    item {
                        Text(
                            text = "Ingresa el codigo",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    item {
                        Text(
                            text = "Código: $code",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    item {
                        Button(
                            onClick = {
                                if (code.length == 6 && code.all { it.isDigit() }) {
                                    isLoggedIn = true
                                    error = false
                                } else {
                                    error = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("              Login")
                        }
                    }
                    if (error) {
                        item {
                            Text(
                                text = "Código inválido",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            // 🔌 Instancia del servicio
            val notifyService = remember { crearNotifyService() }

            // 📦 Estado para cargar datos desde la API
            val notificaciones by produceState<List<Notificacion>?>(initialValue = null) {
                value = try {
                    notifyService.obtenerNotificaciones()
                } catch (e: Exception) {
                    emptyList() // o null si quieres mostrar error
                }
            }

            AppScaffold {
                val listState = rememberTransformingLazyColumnState()
                val transformationSpec = rememberTransformationSpec()

                ScreenScaffold(
                    scrollState = listState,
                    contentPadding = rememberResponsiveColumnPadding(
                        first = ColumnItemType.IconButton,
                        last = ColumnItemType.Button,
                    )
                ) { contentPadding ->

                    TransformingLazyColumn(
                        state = listState,
                        contentPadding = contentPadding,
                    ) {
                        if (notificaciones == null) {
                            // ⏳ Mientras carga
                            item {
                                Text(
                                    text = "Cargando notificaciones...",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (notificaciones!!.isEmpty()) {
                            // ⚠️ Si no hay notificaciones o falló
                            item {
                                Text(
                                    text = "No hay notificaciones.",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // ✅ Mostrar cada notificación como Card
                            notificaciones!!.forEach { noti ->
                                item {
                                    Card(
                                        onClick = { /* Acción opcional */ },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(noti.titulo, style = MaterialTheme.typography.titleMedium)
                                        Text(noti.resumen, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
