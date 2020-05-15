package com.jobc.popularmoviestest.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobc.popularmoviestest.data.movies.model.movie.Movie
import com.jobc.popularmoviestest.data.movies.repository.RepositoryMoviesInternalMemory
import com.jobc.popularmoviestest.data.movies.repository.RepositoryMoviesTmdb
import com.jobc.popularmoviestest.data.movies.utils.Result
import com.jobc.popularmoviestest.main.utils.ResultLoadPoster
import com.jobc.popularmoviestest.main.utils.ResultRequestMovies
import kotlinx.coroutines.*

class PopularMoviesViewModel : ViewModel() {

    private val scope = viewModelScope
    private val repositoryMoviesTmdb = RepositoryMoviesTmdb()
    private val repositoryMoviesInternalMemory = RepositoryMoviesInternalMemory()
    private lateinit var jsonStringConfigLoadPoster: String

    private val _resultRequest = MutableLiveData<ResultRequestMovies>()
    val resultRequestMovies: LiveData<ResultRequestMovies> = _resultRequest

    private val _loadingPoster = MutableLiveData<List<ResultLoadPoster>>()
    val loadingPoster: LiveData<List<ResultLoadPoster>> = _loadingPoster

    fun getPopularMovie(pathCacheMovie: String, page: Int) {
        scope.launch {
            delay(1)
            val coroutineContext = Dispatchers.IO
            val result = withContext (coroutineContext) {
                repositoryMoviesTmdb.getPopularMovies(pathCacheMovie, page)
            }
            when (result is Result.Success) {
                true -> {
                    withContext (coroutineContext) {
                        repositoryMoviesInternalMemory.deleteAllFilesFromDir(
                            getPosterPath(
                                pathCacheMovie
                            )
                        )
                        repositoryMoviesInternalMemory.deleteAllFilesFromDir(
                            getPosterBigPath(
                                pathCacheMovie
                            )
                        )
                        repositoryMoviesInternalMemory.deleteAllFilesFromDir(
                            getMovieDetailPath(
                                pathCacheMovie
                            )
                        )
                        writeJsonStringsToMemory(
                            result.data.jsonStringPopularMovies,
                            pathCacheMovie,
                            FILE_NAME_LIST_MOVIES
                        )
                        jsonStringConfigLoadPoster = result.data.jsonStringConfiguration!!
                        writeJsonStringsToMemory(
                            result.data.jsonStringConfiguration,
                            pathCacheMovie,
                            FILE_NAME_CONFIGURATION_URL_POSTER
                        )
                    }
                    _resultRequest.value =
                        ResultRequestMovies(
                            success = result.data.results,
                            error = null
                        )
                }
                false -> {
                    val resultFromMemory = withContext (coroutineContext) {
                        repositoryMoviesInternalMemory
                            .loadingDataFromMemory(
                                pathCacheMovie,
                                FILE_NAME_LIST_MOVIES
                            )
                    }
                    when(resultFromMemory is Result.Success) {
                        true -> {
                            _resultRequest.value =
                                ResultRequestMovies(
                                    success = resultFromMemory.data.results,
                                    error = null
                                )
                        }
                        false -> {
                            _resultRequest.value =
                                ResultRequestMovies(
                                    success = null,
                                    error = resultFromMemory.getTextResult()
                                )
                        }
                    }
                }
            }
            coroutineContext.cancel()
        }
    }

    fun getPosters (path: String, listMovies: List<Movie>) {
        scope.launch {
            delay(1)
            val coroutineContext = Dispatchers.IO
            val listPosters = mutableListOf<ResultLoadPoster>()
            listMovies.forEach {movie ->
                listPosters.add(
                    ResultLoadPoster(
                        posterFileName = movie.posterPath,
                        id = movie.id
                    )
                )
            }
            listPosters.forEach { movie ->
                if (movie.posterFileName != null) {
                    val resultPoster = withContext(coroutineContext) {
                        getPoster(path, movie.posterFileName)
                    }
                    if (resultPoster is Result.Success) {
                        movie.bitmap = resultPoster.data
                    }
                }
                _loadingPoster.value = listPosters
            }
            coroutineContext.cancel()
        }
    }

    private suspend fun getPoster(
        path: String,
        posterNameFile: String
    ) : Result<Bitmap> {
        val posterPath = getPosterPath(path)
        val resultLoadingFromMemory = repositoryMoviesInternalMemory
            .loadingPoster(posterNameFile, posterPath)
        return when(resultLoadingFromMemory is Result.Success) {
            true -> {
                resultLoadingFromMemory
            }
            false -> {
                getPosterFromInternet(getPosterPath(path), posterNameFile, 0)
            }
        }
    }

    private suspend fun getPosterFromInternet(
        path: String,
        posterNameFile: String,
        posterSize: Int
    ) : Result<Bitmap> {
        val result = repositoryMoviesTmdb
            .getPoster(posterNameFile, jsonStringConfigLoadPoster, posterSize)
        if (result is Result.Success) {
            repositoryMoviesInternalMemory.writePoster(
                result.data,
                posterNameFile,
                path
            )
        }
        return result
    }

    private suspend fun writeJsonStringsToMemory(
        jsonString: String?,
        path: String,
        fileName: String
    ) {
        jsonString?.let {
            repositoryMoviesInternalMemory.writeDataResultToMemory(
                jsonString,
                path,
                fileName
            )
        }
    }

    private fun getPosterPath(path: String) =
        StringBuilder()
            .append(path)
            .append(PATH_NAME_TO_FILES_POSTERS)
            .toString()

    private fun getPosterBigPath(path: String) =
        StringBuilder()
            .append(path)
            .append(PATH_NAME_TO_FILES_POSTERS_BIG)
            .toString()

    private fun getMovieDetailPath(path: String) =
        StringBuilder()
            .append(path)
            .append(PATH_NAME_TO_FILES_MOVIES_DETAIL)
            .toString()
}
