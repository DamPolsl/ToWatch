package com.example.towatch.repository.movie

import androidx.lifecycle.MutableLiveData
import com.example.towatch.domain.model.Movie
import com.example.towatch.infrastructure.remote.RemoteFirebaseDataSource

class RealFirebaseMovieRepository(
    private val firebaseDataSource: RemoteFirebaseDataSource
): FirebaseMovieRepository {
    override fun addMovieToDatabase(movie: Movie) = firebaseDataSource.addMovieToDatabase(movie)

    override fun removeMovieFromDatabase(movie: Movie) {
        firebaseDataSource.removeMovieFromDatabase(movie)
    }

    override fun changeMovieWatchedStatus(movie: Movie, isChecked: Boolean) {
        firebaseDataSource.changeMovieWatchedStatus(movie, isChecked)
    }

    override fun getMoviesFromDatabase(moviesList: MutableLiveData<ArrayList<Movie>>) {
        firebaseDataSource.getMoviesFromDatabase(moviesList)
    }
}