package com.example.popularmoviestest.data.movies.repository

import android.graphics.Bitmap
import com.example.popularmoviestest.data.movies.model.movie.ListMovies
import com.example.popularmoviestest.data.movies.tmdb.LoadingMoviesFromTmdb
import com.example.popularmoviestest.data.movies.tmdb.LoadingMoviesInterface
import com.example.popularmoviestest.data.movies.utils.Result

class RepositoryMoviesTmdb {

    private val dataMovies: LoadingMoviesInterface = LoadingMoviesFromTmdb()

    suspend fun getPopularMovies(pathCacheMovie: String, page: Int) : Result<ListMovies> =
        dataMovies.loadingMovies(pathCacheMovie, page)

    suspend fun getPoster(
        posterNameFile: String,
        jsonString: String,
        posterSize: Int
    ) : Result<Bitmap> =
        dataMovies.loadingPoster(posterNameFile, jsonString, posterSize)

    suspend fun loadingMovieDetailsJson(idMovie: Int) : Result<String> =
        dataMovies.loadingMovieDetailsJson(idMovie)
}