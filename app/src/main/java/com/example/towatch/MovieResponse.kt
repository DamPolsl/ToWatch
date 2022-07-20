package com.example.towatch

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("Search") val movieList: List<Movie>,
    val totalResults: Long,
    @SerializedName("Response") val response: Boolean
)