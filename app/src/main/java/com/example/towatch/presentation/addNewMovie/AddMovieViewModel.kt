package com.example.towatch.presentation.addNewMovie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.towatch.domain.model.Movie
import com.example.towatch.domain.usecase.AddMovieToDatabaseUseCase
import com.example.towatch.domain.usecase.GetMovieDescriptionUseCase
import com.example.towatch.domain.usecase.GetMoviesByTitleUseCase
import retrofit2.HttpException
import java.io.IOException

class AddMovieViewModel : ViewModel() {
    private val _state = MutableLiveData<AddMovieViewState>(AddMovieViewState.Started)
    val state: LiveData<AddMovieViewState> = _state

    val getMoviesByTitleUseCase = GetMoviesByTitleUseCase()
    val getMovieDescriptionUseCase = GetMovieDescriptionUseCase()
    val addMovieToDatabaseUseCase = AddMovieToDatabaseUseCase()

    val movieWatched = MutableLiveData(false)
    val lastShownMoviePosition = MutableLiveData(-1)
    val lastSelectedMoviePosition = MutableLiveData(-1)
    val selectedMovie = MutableLiveData<Movie>()

    val isMovieSelected = MutableLiveData(false)
    val moviesList = MutableLiveData<ArrayList<Movie>>()

    suspend fun fetchMoviesByTitle(searchTitle: String){
        clearList()
        _state.value = AddMovieViewState.Loading

        val response: List<Movie>? = try {
            getMoviesByTitleUseCase(searchTitle)
        } catch (e: IOException){
            Log.e("AddMovieFragment", "IOException: ${e.localizedMessage}")
            _state.value = AddMovieViewState.Error
            return
        } catch (e: HttpException){
            Log.e("AddMovieFragment", "HttpException: ${e.localizedMessage}")
            _state.value = AddMovieViewState.Error
            return
        }
        response?.let {
            moviesList.value = ArrayList(it)
            _state.value = AddMovieViewState.Loaded
        }
    }

    private fun clearList(){
        lastShownMoviePosition.value = -1
        lastSelectedMoviePosition.value = -1
        selectedMovie.value = Movie()
        moviesList.value?.clear()
    }

    suspend fun addMovieToDatabase(movie: Movie) {
        _state.value = AddMovieViewState.Loading

        movie.apply {
            this.plot = getMovieDescriptionUseCase(movie)
            this.watched = movieWatched.value ?: false
            addMovieToDatabaseUseCase(this)
        }

        _state.value = AddMovieViewState.Finished
    }
}