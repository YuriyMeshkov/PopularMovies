package com.example.popularmoviestest.main.detialsmovie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.popularmoviestest.R
import com.example.popularmoviestest.data.movies.model.movie.Movie
import com.example.popularmoviestest.data.movies.model.moviesdetails.GenresItem
import com.example.popularmoviestest.data.movies.model.moviesdetails.MovieDetails
import com.example.popularmoviestest.data.movies.model.moviesdetails.ProductionCountriesItem
import com.example.popularmoviestest.main.PATH_NAME_MOVIE_CACHE
import kotlinx.android.synthetic.main.details_movie_fragment.*


class DetailsMovieFragment : Fragment() {

    companion object {

        private lateinit var movie: Movie

        private const val MOVIE_DETAILS_FOR_SHOW = "movie_details_for_show"

        fun newInstance(movie: Movie) : DetailsMovieFragment {
            val args = Bundle()
            args.putParcelable(MOVIE_DETAILS_FOR_SHOW, movie)
            val fragment = DetailsMovieFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: DetailsMovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initData()
        return inflater.inflate(R.layout.details_movie_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initLiveData()
        getPoster()
    }

    private fun initData() {
        movie = arguments?.getParcelable(MOVIE_DETAILS_FOR_SHOW)!!
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(DetailsMovieViewModel::class.java)
    }

    private fun initLiveData() {
        initLiveDataLoadPoster()
        initLiveDataLoadMovieDetails()
    }

    private fun initLiveDataLoadPoster() {
        viewModel.loadingPoster.observe(this.viewLifecycleOwner, Observer {
            val posterLoaded = it ?: return@Observer
            getMovieDetails()
            pbLoadingPosterBig.visibility = View.GONE
            when(posterLoaded.success != null) {
                true -> {
                    ivPoster.setImageBitmap(posterLoaded.success)
                }
                false -> {
                    when(movie.bitmap != null) {
                        true -> {
                            ivPoster.setImageBitmap(movie.bitmap)
                        }
                        false -> {
                            ivPoster.setImageResource(R.mipmap.image_loading_error)
                        }
                    }
                }
            }
        })
    }

    private fun initLiveDataLoadMovieDetails() {
        viewModel.loadingMovieDetails.observe(this.viewLifecycleOwner, Observer {
            val movieDetails = it ?: return@Observer
            containerDataMovie.visibility = View.VISIBLE
            when(movieDetails.success != null){
                true -> {
                    setMovieDetailsToViewSuccess(movieDetails.success)
                }
                false-> {
                    setMovieDetailsToViewError()
                }
            }
        })
    }
    private fun setMovieDetailsToViewSuccess(movieDetails: MovieDetails) {
        tvOriginalName.text = movieDetails.originalTitle
        tvDataRelease.text = movieDetails.releaseDate
        tvCountry.text = getCountries(movieDetails.productionCountries)
        when(movieDetails.budget) {
            0 -> tvBudget.setText(R.string.unknown_all)
            else -> tvBudget.text = movieDetails.budget.toString()
        }
        when(movieDetails.revenue) {
            0 -> tvRevenue.setText(R.string.unknown_all)
            else -> tvRevenue.text = movieDetails.revenue.toString()
        }
        when(movieDetails.voteAverage) {
            0.0 -> {
                tvVoteAverage.setText(R.string.unknown_all)
            }
            else -> {
                tvVoteAverage.text = movieDetails.voteAverage.toString()
                if (movieDetails.voteCount != 0) {
                    tvVoteCount.text = movieDetails.voteCount.toString()
                    tvVoteCount.visibility = View.VISIBLE
                    tvLeftBracketTitle.visibility = View.VISIBLE
                    tvRightBracketTitle.visibility = View.VISIBLE
                }
            }
        }
        tvGenres.text = getGenres(movieDetails.genres)
        tvPopularity.text = movieDetails.popularity.toString()
        tvRunTime.text = movieDetails.runtime.toString()
        tvOverView.text = movieDetails.overview
    }

    private fun setMovieDetailsToViewError() {
        tvOriginalName.text = movie.originalTitle
        tvDataRelease.text = movie.releaseDate
        tvCountry.setText(R.string.unknown_all)
        tvBudget.setText(R.string.unknown_all)
        tvRevenue.setText(R.string.unknown_all)
        tvGenres.setText(R.string.unknown_all)
        tvPopularity.text = movie.popularity.toString()
        tvVoteAverage.text = movie.voteAverage.toString()
        tvRunTime.setText(R.string.unknown_all)
        tvOverView.text = movie.overview
        tvMinutesTitle.visibility = View.GONE
    }

    private fun getCountries(countries: List<ProductionCountriesItem>?) : String {
        return when(countries) {
            null -> {
                getString(R.string.unknown_all)
            }
            else -> {
                val listCountries = StringBuilder()
                for (i in countries.indices) {
                    listCountries.append(countries[i].name)
                    if (i != countries.size.minus(1)) {
                        listCountries.append(", ")
                    }
                }
                listCountries.toString()
            }
        }
    }
    private fun getGenres(genres: List<GenresItem>?) : String{
        return when(genres) {
            null -> {
                getString(R.string.unknown_all)
            }
            else -> {
                val listGenres = StringBuilder()
                for (i in genres.indices) {
                    listGenres.append(genres[i].name)
                    if (i != genres.size.minus(1)) {
                        listGenres.append(", ")
                    }
                }
                listGenres.toString()
            }
        }
    }

    private fun getPoster() {
        pbLoadingPosterBig.visibility = View.VISIBLE
        viewModel.loadPoster(movie.posterPath, getCachePath())
    }

    private fun getMovieDetails() {
        viewModel.loadMovieDetails(getCachePath(), movie.id)
    }

    private fun getCachePath() : String =
        StringBuilder()
            .append(activity?.filesDir.toString())
            .append(PATH_NAME_MOVIE_CACHE)
            .toString()

}
