package com.example.towatch.addmovie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddMovieViewModel : ViewModel() {
    var isMovieSelected = MutableLiveData(false)
}