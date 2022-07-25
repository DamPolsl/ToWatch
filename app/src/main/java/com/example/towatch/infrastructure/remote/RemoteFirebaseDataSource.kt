package com.example.towatch.infrastructure.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.towatch.BuildConfig
import com.example.towatch.domain.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RemoteFirebaseDataSource {
    private val firebaseAuthInstance = FirebaseAuth.getInstance()
    private val database = Firebase.database(BuildConfig.DB_URL)

    fun getCurrentUser(): FirebaseUser? = firebaseAuthInstance.currentUser

    fun getMoviesFromDatabase(moviesList: MutableLiveData<ArrayList<Movie>>) {
        getCurrentUser()?.let { currentUser ->
            database.getReference("users")
                .child(currentUser.uid)
                .child("movies")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val items: List<Movie> = snapshot.children.map {
                            it.getValue(Movie::class.java) ?: Movie()
                        }
                        ArrayList(items).let { list ->
                            list.sortWith(compareBy({it.watched}, {it.id}))
                            moviesList.postValue(list)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // do nothing :)
                    }

                })
        } ?: Log.e("RemoteFirebaseDataSource", "No user found.")
    }

    fun addMovieToDatabase(movie: Movie) {
        getCurrentUser()?.let { currentUser ->
            database.getReference("users")
                .child(currentUser.uid)
                .child("movies")
                .child(movie.id)
                .setValue(movie)
        } ?: Log.e("RemoteFirebaseDataSource", "No user found.")
    }

    fun removeMovieFromDatabase(movie: Movie){
        getCurrentUser()?.let { currentUser ->
            database.getReference("users")
                .child(currentUser.uid)
                .child("movies")
                .child(movie.id)
                .addListenerForSingleValueEvent(object: ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) { snapshot.ref.removeValue() }

                    override fun onCancelled(error: DatabaseError) {}

                })
        } ?: Log.e("RemoteFirebaseDataSource", "No user found.")
    }

    fun changeMovieWatchedStatus(movie: Movie, isChecked: Boolean){
        getCurrentUser()?.let { currentUser ->
            database.getReference("users")
                .child(currentUser.uid)
                .child("movies")
                .child(movie.id)
                .child("watched")
                .setValue(isChecked)
        }
    }
}