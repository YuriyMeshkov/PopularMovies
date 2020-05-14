package com.example.popularmoviestest.data.movies.tmdb

import android.graphics.Bitmap
import com.example.popularmoviestest.data.movies.model.movie.ListMovies
import com.example.popularmoviestest.data.movies.utils.Result

interface LoadingMoviesInterface {
    suspend fun loadingMovies(
        pathCacheMovie: String,
        page: Int
    ) : Result <ListMovies>

    suspend fun loadingPoster(
        posterNameFile: String,
        jsonString: String,
        posterSize: Int
    ) : Result <Bitmap>

    suspend fun loadingMovieDetailsJson(idMovie: Int) : Result<String>
}