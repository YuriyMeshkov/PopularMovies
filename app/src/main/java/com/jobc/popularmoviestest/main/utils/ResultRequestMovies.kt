package com.jobc.popularmoviestest.main.utils

import com.jobc.popularmoviestest.data.movies.model.movie.Movie

class ResultRequestMovies(
    val success: List<Movie>? = null,
    val error: String? = null
)