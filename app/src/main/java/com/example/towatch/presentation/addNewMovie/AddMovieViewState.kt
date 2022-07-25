package com.example.towatch.presentation.addNewMovie

sealed class AddMovieViewState {
    object Started: AddMovieViewState()
    object Loading: AddMovieViewState()
    object Loaded: AddMovieViewState()
    object Error: AddMovieViewState()
    object Finished: AddMovieViewState()
}