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
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.*


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
        var mostrarError by remember { mutableStateOf(false) }
        var mostrarTodasLasNotificaciones by remember { mutableStateOf(false) }


        // Efecto para ocultar el mensaje de error después de 5 segundos
        LaunchedEffect(mostrarError) {
            if (mostrarError) {
                delay(5000) // Espera 5 segundos
                mostrarError = false
            }
        }

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
                            onValueChange = {
                                tipoIngresado = it
                                mostrarError = false // <-- Añade esta línea
                            },
                            label = { Text("Ej. MSTEA") },
                            modifier = Modifier.fillMaxWidth(0.9f),
                            singleLine = true
                        )
                    }
                    // Mostrar mensaje de error si es necesario
                    if (mostrarError) {
                        item {
                            Text(
                                text = "Por favor ingresa correctamente",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    item {
                        Button(
                            onClick = {
                                val tipo = tipoIngresado.trim().uppercase()
                                // Validar que el tipo sea uno de los permitidos
                                if (tipo in listOf("DOCEC", "MSTTA", "MSTIA", "MSTEA")) {
                                    tipoFiltrado = tipo
                                    mostrarNotificaciones = true
                                }else {
                                    mostrarError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Continuar", maxLines = 1)
                        }

                    }
                }
            }
        } else {
            // 🔌 Servicio y datos
            val notifyService = remember { crearNotifyService() }
            var notificaciones by remember { mutableStateOf<List<Notificacion>?>(null) }

            LaunchedEffect(Unit) {
                notificaciones = try {
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2
                                    )
                                }
                            }

                            else -> {
                                val notificacionesFiltradas = notificaciones!!
                                    .filter { it.estado == "VIGENTE" &&
                                            (it.tipo in listOf("GENERAL", "BECAS", "ACADEMICAS") ||
                                                    (tipoFiltrado.isNotEmpty() && it.tipo == tipoFiltrado))
                                    }
                                // Caso 1: Mostrar solo la primera notificación
// [MODIFICACIÓN PRINCIPAL] Nueva lógica de visualización
                                if (!mostrarTodasLasNotificaciones && notificacionesFiltradas.isNotEmpty()) {
                                    item {
                                        Card(
                                            onClick = { /* Acción opcional */ },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(notificacionesFiltradas[0].titulo,
                                                style = MaterialTheme.typography.titleMedium)
                                            Text(notificacionesFiltradas[0].resumen,
                                                style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }                                // Caso 2: Mostrar TODAS las notificaciones
                                else {
                                    notificacionesFiltradas.forEach { noti ->
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
                                }                        // Mostrar botón SOLO cuando las notificaciones han terminado de cargar y hay contenido
                                // [MODIFICACIÓN PRINCIPAL] Nuevo diseño de botones en fila
                                if (notificaciones != null && notificaciones!!.isNotEmpty()) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Botón "Ver todo" (condicional)
                                            if (notificacionesFiltradas.size > 1 && !mostrarTodasLasNotificaciones) {
                                                Button(
                                                    onClick = { mostrarTodasLasNotificaciones = true },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(end = 4.dp)
                                                ) {
                                                    Text("Ver todo",
                                                        maxLines = 1)
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }

                                            // Botón "Limpiar" (siempre visible)
                                            Button(
                                                onClick = {
                                                    notificaciones = emptyList()
                                                    mostrarTodasLasNotificaciones = false
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 4.dp)
                                            ) {
                                                Text("Limpiar", maxLines = 1)  // [MODIFICACIÓN] Texto más corto
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
}
