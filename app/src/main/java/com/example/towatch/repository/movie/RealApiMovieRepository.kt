package com.example.towatch.repository.movie

import com.example.towatch.domain.model.Movie
import com.example.towatch.infrastructure.remote.RemoteRetrofitDataSource

class RealApiMovieRepository(
    private val retrofitDS: RemoteRetrofitDataSource,
) : ApiMovieRepository {
    override suspend fun getMoviesByTitle(title: String): List<Movie> =
        retrofitDS.getMoviesByTitle(searchTitle = title).body()?.movieList ?: emptyList()

    override suspend fun getMovieDescriptionById(movieId: String): String {
        retrofitDS.getMovieWithDetailsById(movieId = movieId).body()?.let {
            return it.plot
        } ?: return ""
    }
}