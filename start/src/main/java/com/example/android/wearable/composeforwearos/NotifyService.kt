package com.example.android.wearable.composeforwearos

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Interface para la API
interface NotifyService {
    @GET("api/notify")
    suspend fun obtenerNotificaciones(): List<Notificacion>
}

// Función para crear una instancia de Retrofit
fun crearNotifyService(): NotifyService {
    return Retrofit.Builder()
        .baseUrl("https://paulofraireback.onrender.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NotifyService::class.java)
}
