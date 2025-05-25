package com.example.android.wearable.composeforwearos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.*
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.android.wearable.composeforwearos.theme.WearAppTheme
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun IconButtonExample(modifier: Modifier = Modifier) {
    FilledIconButton(
        onClick = { /* Acción futura */ },
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Rounded.Notifications,
            contentDescription = "Ver notificaciones",
        )
    }
}

@Composable
fun TextExample(modifier: Modifier = Modifier, transformation: SurfaceTransformation) {
    ListHeader(
        modifier = modifier,
        transformation = transformation,
    ) {
        Text(
            modifier = modifier,
            textAlign = TextAlign.Center,
            text = "Bienvenido al sistema de notificaciones del CRESPF",
        )
    }
}

@Composable
fun CardExample(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    transformation: SurfaceTransformation,
) {
    AppCard(
        modifier = modifier,
        transformation = transformation,
        appImage = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Message,
                contentDescription = "Abrir mensaje",
                modifier = iconModifier,
            )
        },
        appName = { Text("CRESPF") },
        time = { Text("Hace 5 min") },
        title = { Text("Secretaría Académica") },
        onClick = { /* Detalles */ },
    ) {
        Text("Nueva convocatoria para becas 2025 disponible.")
    }
}

@Composable
fun ChipExample(modifier: Modifier = Modifier, transformation: SurfaceTransformation) {
    Button(
        modifier = modifier,
        transformation = transformation,
        onClick = { /* limpiar alertas */ },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Limpiar alertas",
            )
        },
    ) {
        Text(
            text = "Limpiar alertas",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SwitchChipExample(modifier: Modifier = Modifier, transformation: SurfaceTransformation) {
    var checked by remember { mutableStateOf(true) }
    SwitchButton(
        modifier = modifier,
        label = {
            Text(
                "Recibir alertas",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics {
                    this.contentDescription = if (checked) "Activadas" else "Desactivadas"
                },
            )
        },
        checked = checked,
        onCheckedChange = { checked = it },
        enabled = true,
    )
}

@Composable
fun HighPrioritySwitchExample(modifier: Modifier = Modifier, transformation: SurfaceTransformation) {
    var highPriorityOnly by remember { mutableStateOf(false) }
    SwitchButton(
        modifier = modifier,
        label = {
            Text(
                "Solo de alta prioridad",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics {
                    this.contentDescription =
                        if (highPriorityOnly) "Activado" else "Desactivado"
                },
            )
        },
        checked = highPriorityOnly,
        onCheckedChange = { highPriorityOnly = it },
        enabled = true,
    )
}

@Composable
fun StartOnlyTextComposables() {
    Text(
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        text = "Notificaciones CRESPF",
    )
}
