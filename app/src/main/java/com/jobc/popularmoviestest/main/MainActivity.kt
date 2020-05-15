package com.jobc.popularmoviestest.main

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.jobc.popularmoviestest.R
import com.jobc.popularmoviestest.data.movies.model.movie.Movie
import com.jobc.popularmoviestest.main.detialsmovie.DetailsMovieActivity
import com.jobc.popularmoviestest.main.detialsmovie.DetailsMovieFragment
import com.jobc.popularmoviestest.main.utils.MovieSelectedCallback

const val FILE_NAME_LIST_MOVIES = "listMovies"
const val FILE_NAME_CONFIGURATION_URL_POSTER ="configUrlPoster"
const val PATH_NAME_MOVIE_CACHE = "/cacheMovies/"
const val PATH_NAME_TO_FILES_POSTERS = "posters"
const val PATH_NAME_TO_FILES_POSTERS_BIG = "postersBig"
const val PATH_NAME_TO_FILES_MOVIES_DETAIL = "moviesDetail"
const val FRAGMENT_POPULAR_MOVIES = "popularMoviesFragment"
const val FRAGMENT_DETAILS_MOVIE = "derailsMovieFragment"


class MainActivity : AppCompatActivity(), MovieSelectedCallback {

    @LayoutRes
    private fun getLayoutResId() =
        R.layout.activity_master_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        setOrientationScreen()
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

    override fun onMovieSelected(movie: Movie) {
        if(findViewById<View>(R.id.containerFragmentDetailsMovie) == null) {
            val intent = DetailsMovieActivity.newIntent(this, movie)
            startActivity(intent)
        } else {
            val fragmentManager = supportFragmentManager
            var fragment = fragmentManager.findFragmentByTag(FRAGMENT_DETAILS_MOVIE)
            if (fragment == null) {
                fragment = DetailsMovieFragment.newInstance(movie)
                fragmentManager.beginTransaction()
                    .add(
                        R.id.containerFragmentDetailsMovie, fragment,
                        FRAGMENT_DETAILS_MOVIE
                    ).commit()
            } else {
                onMovieUpdate(movie)
            }
        }
    }

    private fun onMovieUpdate(movie: Movie) {
        val fragment = supportFragmentManager
            .findFragmentByTag(FRAGMENT_DETAILS_MOVIE) as DetailsMovieFragment
        fragment.updateUI(movie)
    }

    private fun setOrientationScreen() {
        if(findViewById<View>(R.id.containerFragmentDetailsMovie) == null) {
            @Suppress
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
