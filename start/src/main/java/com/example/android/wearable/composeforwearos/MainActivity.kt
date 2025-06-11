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
                        item {
                            IconButtonExample()
                        }
                        item {
                            TextExample(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            )
                        }
                        item {
                            CardExample(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            )
                        }
                        item {
                            ChipExample(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            )
                        }
                        item {
                            SwitchChipExample(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            )
                        }
                        item {
                            HighPrioritySwitchExample(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            )
                        }
                    }
                }
            }
        }
    }
}
