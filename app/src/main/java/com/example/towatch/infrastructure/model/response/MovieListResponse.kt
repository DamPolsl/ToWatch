package com.example.towatch.infrastructure.model.response

import com.example.towatch.domain.model.Movie
import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    @SerializedName("Search") val movieList: List<Movie>,
    val totalResults: Long,
    @SerializedName("Response") val response: Boolean
)