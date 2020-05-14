package com.example.popularmoviestest.main.detialsmovie

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.popularmoviestest.R
import com.example.popularmoviestest.data.movies.model.movie.Movie
import com.example.popularmoviestest.main.FRAGMENT_DETAILS_MOVIE

class DetailsMovieActivity : AppCompatActivity(R.layout.activity_details_movie) {

    private lateinit var movie: Movie

    companion object {
        private const val EXTRA_MOVIE = "extra_movie"

        fun newIntent(context: Context, movie: Movie) : Intent {
            val intent = Intent(context, DetailsMovieActivity::class.java)
            intent.putExtra(EXTRA_MOVIE, movie)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        createFragment()
    }

    private fun initData() {
        movie = intent?.getParcelableExtra(EXTRA_MOVIE)!!
    }

    private fun createFragment() {
        val fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(FRAGMENT_DETAILS_MOVIE)
        if (fragment == null) {
            fragment = DetailsMovieFragment.newInstance(movie)
            fragmentManager.beginTransaction()
                .add(
                    R.id.containerFragmentDetailsMovie, fragment,
                    FRAGMENT_DETAILS_MOVIE
                ).commit()
        }
    }

}
