package com.example.popularmoviestest.data.movies.utils


sealed class Result<out T : Any?> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()

    fun getTextResult(): String {
        return when (this) {
            is Success<*> -> "Ok!"
            is Error -> error
        }
    }
}