package com.example.towatch.mapper.domain

import com.example.towatch.infrastructure.model.response.MovieListResponse

class MovieMapper {
    fun toMovieList(response: MovieListResponse) = response.movieList
}