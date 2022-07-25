package com.example.towatch.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.example.towatch.domain.model.Movie
import com.example.towatch.infrastructure.remote.FirebaseInstance
import com.example.towatch.repository.movie.RealFirebaseMovieRepository

class GetMoviesFromDatabaseUseCase {
    private val repository = RealFirebaseMovieRepository(FirebaseInstance.api)
    operator fun invoke(moviesList: MutableLiveData<ArrayList<Movie>>) =
        repository.getMoviesFromDatabase(moviesList)
}