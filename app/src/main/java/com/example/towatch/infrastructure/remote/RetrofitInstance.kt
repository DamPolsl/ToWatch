package com.example.towatch.infrastructure.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: RemoteRetrofitDataSource by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteRetrofitDataSource::class.java)
    }
}