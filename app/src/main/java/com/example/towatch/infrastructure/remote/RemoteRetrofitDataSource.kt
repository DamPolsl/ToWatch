package com.example.towatch.infrastructure.remote

import com.example.towatch.BuildConfig
import com.example.towatch.domain.model.Movie
import com.example.towatch.infrastructure.model.response.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteRetrofitDataSource {

    @GET("/")
    suspend fun getMoviesByTitle(
        @Query("s") searchTitle: String,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
        @Query("type") type: String = "Movie"
    ) : Response<MovieListResponse>

    @GET("/")
    suspend fun getMovieWithDetailsById(
        @Query("i") movieId: String,
        @Query("apikey") apiKey: String = BuildConfig.API_KEY,
        @Query("type") type: String = "Movie"
    ) : Response<Movie>
}