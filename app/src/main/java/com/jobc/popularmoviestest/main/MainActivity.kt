package com.example.popularmoviestest.main

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.popularmoviestest.R

const val FILE_NAME_LIST_MOVIES = "listMovies"
const val FILE_NAME_CONFIGURATION_URL_POSTER ="configUrlPoster"
const val PATH_NAME_MOVIE_CACHE = "/cacheMovies/"
const val PATH_NAME_TO_FILES_POSTERS = "posters"
const val PATH_NAME_TO_FILES_POSTERS_BIG = "postersBig"
const val PATH_NAME_TO_FILES_MOVIES_DETAIL = "moviesDetail"
const val FRAGMENT_POPULAR_MOVIES = "popularMoviesFragment"
const val FRAGMENT_DETAILS_MOVIE = "derailsMovieFragment"


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(FRAGMENT_POPULAR_MOVIES)
        if (fragment == null) {
            fragment = PopularMoviesFragment()
            fragmentManager.beginTransaction()
                .add(
                    R.id.containerFragment, fragment,
                    FRAGMENT_POPULAR_MOVIES
                ).commit()
        }
    }
}
