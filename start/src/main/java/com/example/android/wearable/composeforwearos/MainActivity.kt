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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.FilterChipDefaults

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
        var expandedNotificaciones by remember { mutableStateOf<Set<String>>(emptySet()) }
        var filtroPrioridadActivo by remember { mutableStateOf(false) }
        var prioridadSeleccionada by remember { mutableStateOf("ALTA") }

        LaunchedEffect(mostrarError) {
            if (mostrarError) {
                delay(5000)
                mostrarError = false
            }
        }

        if (!mostrarNotificaciones) {
            ScreenScaffold {
                ScalingLazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                                mostrarError = false
                            },
                            placeholder = {
                                Text(
                                    text = "Ej. MSTEA",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(0.9f),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.small,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    }
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
                                if (tipo in listOf("DOCEC", "MSTTA", "MSTIA", "MSTEA")) {
                                    tipoFiltrado = tipo
                                    mostrarNotificaciones = true
                                } else {
                                    mostrarError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.8f)
                                .wrapContentWidth()
                                .padding(horizontal = 4.dp),  // Padding más pequeño
                            contentPadding = PaddingValues(horizontal = 12.dp)  // Padding interno reducido
                        ) {
                            Text("Continuar", maxLines = 1)
                        }
                    }
                }
            }
        } else {
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
                val listState = rememberScalingLazyListState()

                ScreenScaffold {
                    ScalingLazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
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
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2
                                    )
                                }
                            }

                            else -> {
                                // Botones de filtro como items de la lista
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        FilterChip(
                                            selected = !filtroPrioridadActivo,
                                            onClick = { filtroPrioridadActivo = false },
                                            label = { Text("Todas") },
                                            modifier = Modifier.weight(0.48f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                containerColor = Color.Transparent, // Sin color cuando no está seleccionado
                                                selectedContainerColor = Color(0xFF159648), // Verde cuando está seleccionado
                                                labelColor = Color.Black, // Color del texto normal
                                                selectedLabelColor = Color.White // Color del texto cuando está seleccionado
                                            )
                                        )

                                        FilterChip(
                                            selected = filtroPrioridadActivo,
                                            onClick = {
                                                filtroPrioridadActivo = true
                                                prioridadSeleccionada = if (prioridadSeleccionada == "ALTA") "BAJA" else "ALTA"
                                            },
                                            label = {
                                                Text(
                                                    if (filtroPrioridadActivo) {
                                                        if (prioridadSeleccionada == "ALTA") "Alta" else "Baja"
                                                    } else {
                                                        "Filtrar"
                                                    }
                                                )
                                            },
                                            modifier = Modifier.weight(0.48f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                containerColor = Color.Transparent, // Sin color cuando no está seleccionado
                                                selectedContainerColor = Color(0xFF159648), // Verde directo (#159648) cuando está seleccionado
                                                labelColor = Color(0xFF159648), // Texto verde cuando no está seleccionado
                                                selectedLabelColor = Color.White // Texto blanco cuando está seleccionado
                                            )
                                        )
                                    }
                                }

                                val notificacionesFiltradas = notificaciones!!
                                    .filter {
                                        it.estado == "VIGENTE" &&
                                                (it.tipo in listOf("GENERAL", "BECAS", "ACADEMICAS") ||
                                                        (tipoFiltrado.isNotEmpty() && it.tipo == tipoFiltrado))
                                    }
                                    .filter { noti ->
                                        if (filtroPrioridadActivo) {
                                            noti.prioridad == prioridadSeleccionada
                                        } else {
                                            true
                                        }
                                    }

                                if (!mostrarTodasLasNotificaciones && notificacionesFiltradas.isNotEmpty()) {
                                    item {
                                        if (notificacionesFiltradas.isNotEmpty()) {
                                            val primeraNoti = notificacionesFiltradas[0]
                                            val isExpanded = remember {
                                                derivedStateOf { expandedNotificaciones.contains(primeraNoti._id) }
                                            }

                                            Card(
                                                onClick = {
                                                    expandedNotificaciones = if (isExpanded.value) {
                                                        expandedNotificaciones - primeraNoti._id
                                                    } else {
                                                        expandedNotificaciones + primeraNoti._id
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(12.dp)
                                                        .animateContentSize(animationSpec = tween(100))
                                                ) {
                                                    Text(
                                                        primeraNoti.titulo,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )

                                                    Text(
                                                        text = "Prioridad: ${primeraNoti.prioridad}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    )

                                                    if (isExpanded.value) {
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            primeraNoti.resumen,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    notificacionesFiltradas.forEach { noti ->
                                        item {
                                            val isExpanded = remember {
                                                derivedStateOf { expandedNotificaciones.contains(noti._id) }
                                            }

                                            Card(
                                                onClick = {
                                                    expandedNotificaciones = if (isExpanded.value) {
                                                        expandedNotificaciones - noti._id
                                                    } else {
                                                        expandedNotificaciones + noti._id
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .padding(12.dp)
                                                        .animateContentSize(animationSpec = tween(100))
                                                ) {
                                                    Text(
                                                        noti.titulo,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )

                                                    if (isExpanded.value) {
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            noti.resumen,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (notificaciones != null && notificaciones!!.isNotEmpty()) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Botón "Ver todo"
                                            Button(
                                                onClick = { mostrarTodasLasNotificaciones = true },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 4.dp)
                                                    .height(30.dp), // Altura fija compacta
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF159648), // Verde
                                                    contentColor = Color.White // Texto blanco
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp) // Padding interno mínimo
                                            ) {
                                                Text(
                                                    "Ver todo",
                                                    style = MaterialTheme.typography.labelSmall, // Texto pequeño
                                                    maxLines = 1
                                                )
                                            }

                                            // Botón "Limpiar"
                                            Button(
                                                onClick = {
                                                    notificaciones = emptyList()
                                                    mostrarTodasLasNotificaciones = false
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 4.dp)
                                                    .height(30.dp), // Misma altura que "Ver todo"
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF159648), // Verde
                                                    contentColor = Color.White // Texto blanco
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text(
                                                    "Limpiar",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    maxLines = 1
                                                )
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