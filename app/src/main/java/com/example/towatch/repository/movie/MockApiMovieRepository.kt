package com.example.towatch.repository.movie

import com.example.towatch.domain.model.Movie

class MockApiMovieRepository: ApiMovieRepository {
    override suspend fun getMoviesByTitle(title: String): List<Movie> {
        return listOf(
            Movie(
                title = "Shrek",
                id = "tt0126029",
                plot = "A mean lord exiles fairytale creatures to the swamp of a grumpy ogre, who must go on a quest and rescue a princess for the lord in order to get his land back.",
                poster = "https://m.media-amazon.com/images/M/MV5BOGZhM2FhNTItODAzNi00YjA0LWEyN2UtNjJlYWQzYzU1MDg5L2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg",
                year = "2001",
                watched = false
                )
        )
    }

    override suspend fun getMovieDescriptionById(movieId: String): String {
        return "A mean lord exiles fairytale creatures to the swamp of a grumpy ogre, " +
                "who must go on a quest and rescue a princess for the lord in order to get his land back."
    }
}