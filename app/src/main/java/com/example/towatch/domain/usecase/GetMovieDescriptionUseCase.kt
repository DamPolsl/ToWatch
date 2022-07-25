package com.example.towatch.domain.usecase

import com.example.towatch.domain.model.Movie
import com.example.towatch.infrastructure.remote.RetrofitInstance
import com.example.towatch.repository.movie.ApiMovieRepository
import com.example.towatch.repository.movie.RealApiMovieRepository

class GetMovieDescriptionUseCase() {
    private val repository: ApiMovieRepository = RealApiMovieRepository(
        retrofitDS = RetrofitInstance.api
    )

    suspend operator fun invoke(movie: Movie): String =
        repository.getMovieDescriptionById(movieId = movie.id)
}