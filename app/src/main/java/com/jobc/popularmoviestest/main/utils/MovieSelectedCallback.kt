package com.jobc.popularmoviestest.main.utils

import com.jobc.popularmoviestest.data.movies.model.movie.Movie

interface MovieSelectedCallback {
    fun onMovieSelected(movie: Movie)
}