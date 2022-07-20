package com.example.towatch

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {

    @GET("/")
    suspend fun getMoviesByTitle(
        @Query("s") searchTitle: String,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
        @Query("type") type: String = "Movie"
    ) : Response<MovieResponse>

    @GET("/")
    suspend fun getMovieWithDetailsById(
        @Query("i") movieId: String,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
        @Query("type") type: String = "Movie"
    ) : Response<Movie>
}