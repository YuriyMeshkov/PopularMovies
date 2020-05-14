package com.example.popularmoviestest.main.detialsmovie

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.popularmoviestest.data.movies.model.moviesdetails.MovieDetails
import com.example.popularmoviestest.data.movies.repository.RepositoryMoviesInternalMemory
import com.example.popularmoviestest.data.movies.repository.RepositoryMoviesTmdb
import com.example.popularmoviestest.data.movies.utils.Result
import com.example.popularmoviestest.main.FILE_NAME_CONFIGURATION_URL_POSTER
import com.example.popularmoviestest.main.PATH_NAME_TO_FILES_MOVIES_DETAIL
import com.example.popularmoviestest.main.PATH_NAME_TO_FILES_POSTERS_BIG
import com.example.popularmoviestest.main.detialsmovie.utils.ResultLoadBigPoster
import com.example.popularmoviestest.main.detialsmovie.utils.ResultMovieDetails
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import org.json.JSONException

class DetailsMovieViewModel : ViewModel() {

    private val scope = viewModelScope
    private val repositoryMoviesTmdb = RepositoryMoviesTmdb()
    private val repositoryMoviesInternalMemory = RepositoryMoviesInternalMemory()
    private lateinit var jsonStringConfigLoadPoster: String

    private val _loadingPoster = MutableLiveData<ResultLoadBigPoster>()
    val loadingPoster: LiveData<ResultLoadBigPoster> = _loadingPoster

    private val _loadingMovieDetails = MutableLiveData<ResultMovieDetails>()
    val loadingMovieDetails: LiveData<ResultMovieDetails> = _loadingMovieDetails

    fun loadPoster(fileName: String?, path: String) {
        if (fileName == null) {
            _loadingPoster.value =
                ResultLoadBigPoster(error = "error - Bitmap = null")
            return
        }
        scope.launch {
            delay(1)
            val coroutineContext = Dispatchers.IO
            val resultTry1 = withContext(coroutineContext) {
                repositoryMoviesInternalMemory.loadingPoster(fileName, getPosterBigPath(path))
            }
            when(resultTry1 is Result.Success) {
                true -> {
                    _loadingPoster.value =
                        ResultLoadBigPoster(success = resultTry1.data)
                }
                false -> {
                    val resultLoadedConfigPoster = withContext(coroutineContext) {
                        repositoryMoviesInternalMemory
                            .loadingJsonStringFromMemory(
                                path,
                                FILE_NAME_CONFIGURATION_URL_POSTER
                            )
                    }
                    when(resultLoadedConfigPoster is Result.Success) {
                        true -> {
                            jsonStringConfigLoadPoster = resultLoadedConfigPoster.data
                            downLoadPosterFromInternet(getPosterBigPath(path), fileName, 3)
                            val resultTry2 = withContext(coroutineContext) {
                                repositoryMoviesInternalMemory
                                    .loadingPoster(fileName, getPosterBigPath(path))
                            }
                            if (resultTry2 is Result.Success) {
                                _loadingPoster.value =
                                    ResultLoadBigPoster(success = resultTry2.data)
                            } else {
                                _loadingPoster.value =
                                    ResultLoadBigPoster(error = resultTry1.getTextResult())
                            }
                        }
                        false -> {
                            _loadingPoster.value =
                                ResultLoadBigPoster(error = resultTry1.getTextResult())
                        }
                    }
                }
            }
            coroutineContext.cancel()
        }
    }

    fun loadMovieDetails(path: String, idMovie: Int) {
        scope.launch {
            delay(1)
            val coroutineContext = Dispatchers.IO
            val resultTry1 = withContext(coroutineContext) {
                repositoryMoviesInternalMemory
                    .loadingJsonStringFromMemory(getMovieDetailPath(path), idMovie.toString())
            }
            when(resultTry1 is Result.Success) {
                true -> {
                    val resultJsonToObject = withContext(coroutineContext){
                        getMovieDetailFromJson(resultTry1.data)
                    }
                    when(resultJsonToObject is Result.Success){
                        true -> {
                            _loadingMovieDetails.value =
                                ResultMovieDetails(success = resultJsonToObject.data)
                        }
                        false -> {
                            _loadingMovieDetails.value =
                                ResultMovieDetails(error = resultJsonToObject.getTextResult())
                        }
                    }
                }
                false -> {
                    withContext(coroutineContext) {
                        downloadMovieDataFromInternet(path, idMovie)
                    }
                    val resultTry2 = withContext(coroutineContext) {
                        repositoryMoviesInternalMemory
                            .loadingJsonStringFromMemory(getMovieDetailPath(path), idMovie.toString())
                    }
                    when(resultTry2 is Result.Success) {
                        true -> {
                            val resultJsonToObject = getMovieDetailFromJson(resultTry2.data)
                            when(resultJsonToObject is Result.Success){
                                true -> {
                                    _loadingMovieDetails.value =
                                        ResultMovieDetails(success = resultJsonToObject.data)
                                }
                                false -> {
                                    _loadingMovieDetails.value =
                                        ResultMovieDetails(error = resultJsonToObject.getTextResult())
                                }
                            }
                        }
                        false -> {
                            _loadingMovieDetails.value =
                                ResultMovieDetails(error = resultTry1.getTextResult())
                        }
                    }
                }
            }
            coroutineContext.cancel()
        }
    }

    private suspend fun downloadMovieDataFromInternet(path: String, idMovie: Int) {
        val result = repositoryMoviesTmdb.loadingMovieDetailsJson(idMovie)
        if (result is Result.Success) {
            writeJsonStringsToMemory(result.data, getMovieDetailPath(path), idMovie.toString())
        }
    }

    private suspend fun downLoadPosterFromInternet(
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

    private fun getMovieDetailFromJson(
        jsonString: String
    ) : Result<MovieDetails> {
        return try {
            val result = Gson().fromJson(jsonString, MovieDetails::class.java)
            Result.Success(result)
        } catch (e: JSONException) {
            Result.Error(e.message.toString())
        } catch (e: IllegalStateException) {
            Result.Error(e.message.toString())
        } catch (e: JsonSyntaxException) {
            Result.Error(e.message.toString())
        }
    }
}
