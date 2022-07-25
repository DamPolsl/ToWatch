package com.example.towatch.domain.usecase

import com.example.towatch.infrastructure.remote.RetrofitInstance
import com.example.towatch.mapper.domain.MovieMapper
import com.example.towatch.repository.movie.ApiMovieRepository
import com.example.towatch.repository.movie.RealApiMovieRepository

class GetMoviesByTitleUseCase {
    private val repository: ApiMovieRepository = RealApiMovieRepository(
        retrofitDS = RetrofitInstance.api,
    )

    suspend operator fun invoke(searchTitle: String) = repository.getMoviesByTitle(searchTitle)
}