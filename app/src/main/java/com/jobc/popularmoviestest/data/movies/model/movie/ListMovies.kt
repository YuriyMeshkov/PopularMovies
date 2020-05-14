package com.example.popularmoviestest.data.movies.model.movie

import com.google.gson.annotations.SerializedName

data class ListMovies (
    @SerializedName("results")
    val results: List<Movie>,
    var jsonStringPopularMovies: String?,
    var jsonStringConfiguration: String?
)