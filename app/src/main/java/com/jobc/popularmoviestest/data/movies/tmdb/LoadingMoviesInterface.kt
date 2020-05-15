package com.jobc.popularmoviestest.data.movies.tmdb

import android.graphics.Bitmap
import com.jobc.popularmoviestest.data.movies.model.movie.ListMovies
import com.jobc.popularmoviestest.data.movies.utils.Result

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