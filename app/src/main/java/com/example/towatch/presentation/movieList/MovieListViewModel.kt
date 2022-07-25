package com.example.towatch.presentation.movieList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.towatch.domain.model.Movie
import com.example.towatch.domain.usecase.ChangeMovieWatchedStatusUseCase
import com.example.towatch.domain.usecase.GetMoviesFromDatabaseUseCase
import com.example.towatch.domain.usecase.RemoveMovieFromDatabaseUseCase

class MovieListViewModel: ViewModel() {
    private val _state = MutableLiveData<MovieListViewState>()
    val state: LiveData<MovieListViewState> = _state

    private val _moviesList = MutableLiveData<ArrayList<Movie>>()
    val moviesList: LiveData<ArrayList<Movie>> = _moviesList

    private val _selectedMovie = MutableLiveData<Movie>()
    val selectedMovie: LiveData<Movie> = _selectedMovie

    // use cases
    private val changeMovieWatchedStatusUseCase = ChangeMovieWatchedStatusUseCase()
    private val removeMovieFromDatabaseUseCase = RemoveMovieFromDatabaseUseCase()
    private val getMoviesFromDatabaseUseCase = GetMoviesFromDatabaseUseCase()

    fun changeMovieWatchedStatus(movie: Movie, isChecked: Boolean) = changeMovieWatchedStatusUseCase(movie, isChecked)

    fun removeMovieFromDatabase(movie: Movie) = removeMovieFromDatabaseUseCase(movie)

    fun getMoviesFromDatabase() = getMoviesFromDatabaseUseCase(_moviesList)

    fun changeSelectedMovie(movie: Movie){
        _selectedMovie.value = movie
    }
}