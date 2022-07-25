package com.example.towatch.domain.usecase

import com.example.towatch.domain.model.Movie
import com.example.towatch.infrastructure.remote.FirebaseInstance
import com.example.towatch.repository.movie.RealFirebaseMovieRepository

class RemoveMovieFromDatabaseUseCase {
    private val repository =  RealFirebaseMovieRepository(FirebaseInstance.api)

    operator fun invoke(movie: Movie) = repository.removeMovieFromDatabase(movie)
}