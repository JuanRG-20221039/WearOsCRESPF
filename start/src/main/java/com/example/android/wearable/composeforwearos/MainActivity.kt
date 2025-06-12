package com.example.android.wearable.composeforwearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp


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
        var tipoIngresado by remember { mutableStateOf("") }
        var tipoFiltrado by remember { mutableStateOf("") }
        var mostrarNotificaciones by remember { mutableStateOf(false) }

        if (!mostrarNotificaciones) {
            // Pantalla inicial para capturar el tipo
            ScreenScaffold {
                ScalingLazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    item {
                        Text(
                            text = "Tipo de usuario",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )

                    }
                    item {
                        TextField(
                            value = tipoIngresado,
                            onValueChange = { tipoIngresado = it },
                            label = { Text("Ej. MSTEA") },
                            modifier = Modifier.fillMaxWidth(0.9f),
                            singleLine = true
                        )

                    }
                    item {
                        Button(
                            onClick = {
                                tipoFiltrado = tipoIngresado.trim().uppercase()
                                mostrarNotificaciones = true
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                        ) {
                            Text("Continuar", maxLines = 1)
                        }

                    }
                }
            }
        } else {
            // 🔌 Servicio y datos
            val notifyService = remember { crearNotifyService() }
            val notificaciones by produceState<List<Notificacion>?>(initialValue = null) {
                value = try {
                    notifyService.obtenerNotificaciones()
                } catch (e: Exception) {
                    emptyList()
                }
            }

            AppScaffold {
                val listState = rememberTransformingLazyColumnState()

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
                        when {
                            notificaciones == null -> {
                                item {
                                    Text(
                                        text = "Cargando notificaciones...",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            notificaciones!!.isEmpty() -> {
                                item {
                                    Text(
                                        text = "No hay notificaciones",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            else -> {
                                notificaciones!!
                                    .filter { it.estado == "VIGENTE" && (it.tipo == "GENERAL" || it.tipo == tipoFiltrado) }
                                    .forEach { noti ->
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
}