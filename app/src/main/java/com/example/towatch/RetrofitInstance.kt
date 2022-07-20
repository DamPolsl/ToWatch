package com.example.towatch

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: RetrofitApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitApi::class.java)
    }
}