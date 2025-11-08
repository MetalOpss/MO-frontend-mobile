package com.example.metalops.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // ⚠ Si usas emulador: http://10.0.2.2:8080/
    // ⚠ Si usas celular físico: http://TU_IP_LOCAL:8080/
    private const val BASE_URL = "http://192.168.18.20:8080/"

    val api: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}