package com.jobc.popularmoviestest.main.utils

import com.example.popularmoviestest.data.movies.model.movie.Movie

interface Callbacks {
    fun onMovieSelected(movie: Movie)
}