package com.example.towatch.repository.movie

import com.example.towatch.domain.model.Movie

interface ApiMovieRepository {
    suspend fun getMoviesByTitle(title: String): List<Movie>
    suspend fun getMovieDescriptionById(movieId: String): String
}