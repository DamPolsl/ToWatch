package com.example.towatch.presentation.movieList

sealed class MovieListViewState {
    object Started: MovieListViewState()
    object Loading: MovieListViewState()
    object Loaded: MovieListViewState()
    object Error: MovieListViewState()
}