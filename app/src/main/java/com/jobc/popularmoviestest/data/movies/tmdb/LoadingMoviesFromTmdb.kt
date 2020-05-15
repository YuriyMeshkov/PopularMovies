package com.jobc.popularmoviestest.data.movies.tmdb

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.jobc.popularmoviestest.data.movies.model.ConfigurationList
import com.jobc.popularmoviestest.data.movies.model.ConfigurationRequestForPoster
import com.jobc.popularmoviestest.data.movies.model.movie.ListMovies
import com.jobc.popularmoviestest.data.movies.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

private const val API_KEY = API_KEY_TMDB
private const val BASE_URL = "https://api.themoviedb.org/3/movie/popular"
private const val BASE_URL_CONFIGURATION = "https://api.themoviedb.org/3/configuration"
private const val BASE_URL_MOVIE_DETAILS = "https://api.themoviedb.org/3/movie/"

class LoadingMoviesFromTmdb : LoadingMoviesInterface {

    private val countryCode = Locale.getDefault().country
    private val languageCode = Locale.getDefault().language

    override suspend fun loadingMovies(pathCacheMovie: String, page: Int): Result<ListMovies> {
        val jsonString = withContext(Dispatchers.IO) {
            getJsonString(buildUrlPopular(page))
        }
        return when(jsonString is Result.Success) {
            true -> {
                try {
                    val result = Gson().fromJson(jsonString.data, ListMovies::class.java)
                    result.jsonStringPopularMovies = jsonString.data
                    val jsonStringConfigUrl = withContext(Dispatchers.IO) {
                        getJsonString(buildUrlConfiguration())
                    }
                    if(jsonStringConfigUrl is Result.Success) {
                        result.jsonStringConfiguration = jsonStringConfigUrl.data
                    }
                    Result.Success(result)
                } catch (e: JSONException) {
                    Result.Error(e.message.toString())
                } catch (e: IllegalStateException) {
                    Result.Error(e.message.toString())
                }

            }
            false -> {
                Result.Error(jsonString.getTextResult())
            }
        }
    }

    override suspend fun loadingPoster(
        posterNameFile: String,
        jsonString: String,
        posterSize: Int
    ): Result<Bitmap> {
        val result = Gson().fromJson(jsonString, ConfigurationList::class.java)
        return uploadPoster(buildUrlPoster(result.images, posterNameFile, posterSize))
    }

    private suspend fun uploadPoster(url: String) : Result<Bitmap> =
        withContext(Dispatchers.IO) {
            var inputStream: InputStream? = null
            try {
                inputStream = URL(url).openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Result.Success(bitmap)
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
            finally {
                inputStream?.close()
            }
        }

    override suspend fun loadingMovieDetailsJson(idMovie: Int) : Result<String> {
        val baseUrl = StringBuilder()
            .append(BASE_URL_MOVIE_DETAILS)
            .append(idMovie.toString())
            .toString()
        val jsonString = getJsonString(buildUrlMovieDetails(baseUrl))
        return when(jsonString is Result.Success) {
            true -> {
                Result.Success(jsonString.data)
            }
            false -> {
                Result.Error(jsonString.getTextResult())
            }
        }
    }

    private suspend fun getJsonString(buildUrl: String) : Result<String> =
        withContext(Dispatchers.IO) {
            val url = URL(buildUrl)
            val connection = url.openConnection() as HttpURLConnection
            try {
                Result.Success(connection.inputStream.bufferedReader().readText())
            } catch (e: IOException) {
                Result.Error(e.message.toString())
            } finally {
                connection.disconnect()
            }
        }


    private fun buildUrlPopular(page: Int) =
        Uri.parse(BASE_URL)
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("language", languageCode)
            .appendQueryParameter("page", page.toString())
            .appendQueryParameter("region", countryCode)
            .build()
            .toString()

    private fun buildUrlPoster(
        config: ConfigurationRequestForPoster,
        posterFile: String,
        posterSize: Int) : String =
        StringBuilder()
            .append(config.secureBaseUrl)
            .append(config.posterSize[posterSize])
            .append("/")
            .append(posterFile)
            .toString()

    private fun buildUrlConfiguration() =
        Uri.parse(BASE_URL_CONFIGURATION)
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .build()
            .toString()

    private fun buildUrlMovieDetails(baseUrl: String) =
        Uri.parse(baseUrl)
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("language", languageCode)
            .toString()

}