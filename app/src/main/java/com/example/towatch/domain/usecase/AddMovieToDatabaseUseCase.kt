package com.example.towatch.domain.usecase

import com.example.towatch.domain.model.Movie
import com.example.towatch.infrastructure.remote.FirebaseInstance
import com.example.towatch.repository.movie.RealFirebaseMovieRepository

class AddMovieToDatabaseUseCase {
    val repository = RealFirebaseMovieRepository(firebaseDataSource = FirebaseInstance.api)

    operator fun invoke(movie: Movie) = repository.addMovieToDatabase(movie)
}