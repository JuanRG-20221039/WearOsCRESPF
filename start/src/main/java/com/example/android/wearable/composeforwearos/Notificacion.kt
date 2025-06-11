package com.example.android.wearable.composeforwearos

data class Notificacion(
    val _id: String,
    val titulo: String,
    val resumen: String,
    val descripcion: String,
    val horaEmision: String,
    val tiempoExpiracion: String,
    val estado: String,
    val tipo: String,
    val createdAt: String,
    val updatedAt: String,
    val fechaExpiracion: String
)
