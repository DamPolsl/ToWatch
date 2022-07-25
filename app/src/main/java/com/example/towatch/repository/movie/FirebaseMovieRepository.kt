package com.example.towatch.repository.movie

import androidx.lifecycle.MutableLiveData
import com.example.towatch.domain.model.Movie

interface FirebaseMovieRepository {
    fun addMovieToDatabase(movie: Movie)
    fun removeMovieFromDatabase(movie: Movie)
    fun changeMovieWatchedStatus(movie: Movie, isChecked: Boolean)
    fun getMoviesFromDatabase(moviesList: MutableLiveData<ArrayList<Movie>>)
}