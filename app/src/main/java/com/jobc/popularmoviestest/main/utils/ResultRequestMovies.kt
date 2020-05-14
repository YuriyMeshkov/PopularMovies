package com.example.popularmoviestest.main.utils

import com.example.popularmoviestest.data.movies.model.movie.Movie

class ResultRequestMovies(
    val success: List<Movie>? = null,
    val error: String? = null
)