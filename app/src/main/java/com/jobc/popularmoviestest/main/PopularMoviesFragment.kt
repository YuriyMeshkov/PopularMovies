package com.example.popularmoviestest.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popularmoviestest.*
import com.example.popularmoviestest.data.movies.model.movie.Movie
import com.example.popularmoviestest.main.adapter.MoviesAdapterRW
import com.example.popularmoviestest.main.detialsmovie.DetailsMovieActivity
import com.example.popularmoviestest.main.utils.ItemOffsetDecoration
import kotlinx.android.synthetic.main.popular_movies_fragment.*


class PopularMoviesFragment : Fragment() {

    private lateinit var viewModel: PopularMoviesViewModel
    private var adapterRW: MoviesAdapterRW? = null
    private var downloadPage = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popular_movies_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        initViewModel()
        initLiveData()
        initButtons()
        getPopularMovie(downloadPage)
    }

    private fun initRecyclerView() {
        rvMovies.layoutManager = LinearLayoutManager(activity)
        rvMovies.addItemDecoration(
            ItemOffsetDecoration(
                72
            )
        )
        initListenerScrollRV()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(PopularMoviesViewModel::class.java)
    }

    private fun initLiveData() {
        viewModel.resultRequestMovies.observe(this.viewLifecycleOwner, Observer {
            val listMovie = it ?: return@Observer
            pbLoading.visibility = View.GONE
            when(listMovie.success != null) {
                true -> {
                    val movies: MutableList<Movie> = mutableListOf()
                    movies.addAll(listMovie.success)
                    viewModel.getPosters(getCachePath(), movies)
                    if (adapterRW == null) {
                        adapterRW =
                            MoviesAdapterRW(
                                movies,
                                viewModel.loadingPoster,
                                this
                            ) {movieId ->
                                createDetailsMovieActivity(movies, movieId)
                            }
                        rvMovies.adapter = adapterRW
                    } else {
                        updateAdapterRV(movies)
                    }
                }
                false -> {
                    //tvErrorLoading.text = listMovie.error
                    tvErrorLoading.setText(R.string.error_result)
                    containerError.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun updateAdapterRV(movies: MutableList<Movie>) {
        adapterRW?.clearListMovie()
        adapterRW?.setAddMovie(movies)
        rvMovies.scrollToPosition(0)
        if (downloadPage == 1) {
            btnPreviousPage.visibility = View.GONE
            containerPreviousPage.visibility = View.VISIBLE
        }
    }

    private fun initListenerScrollRV() {
        rvMovies.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val positionFirst = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()
                val positionLast = (recyclerView.layoutManager as LinearLayoutManager)
                    .findLastVisibleItemPosition()
                if (positionFirst == 0) {
                    showTopWindowLoadingPage()
                } else {
                    doNotShowTopWindowLoadingPage()
                }
                if(positionLast == rvMovies.adapter?.itemCount?.minus(1)) {
                    btnNextPage.visibility = View.VISIBLE
                } else {
                    btnNextPage.visibility = View.GONE
                }
            }
        })
    }

    private fun showTopWindowLoadingPage () {
        if(downloadPage == 1) {
            btnPreviousPage.visibility = View.GONE
            containerPreviousPage.visibility = View.VISIBLE
        } else {
            btnPreviousPage.visibility = View.VISIBLE
            containerPreviousPage.visibility = View.GONE
        }
    }

    private fun doNotShowTopWindowLoadingPage() {
        btnPreviousPage.visibility = View.GONE
        containerPreviousPage.visibility = View.GONE
    }

    private fun getPopularMovie(page: Int) {
        if (page == 1) {
            pbLoading.visibility = View.VISIBLE
        }
        viewModel.getPopularMovie(getCachePath(), page)
    }

    private fun initButtons() {
        initBtnTryLoading()
        initBtnNextPage()
        initBtnPreviousPage()
    }

    private fun initBtnTryLoading() {
        btnTryLoading.setOnClickListener {
            containerError.visibility = View.GONE
            //containerRV.setBackgroundResource(R.drawable.bg_splash)
            getPopularMovie(downloadPage)
        }
    }

    private fun initBtnNextPage() {
        btnNextPage.setOnClickListener {
            downloadPage++
            getPopularMovie(downloadPage)
        }
    }

    private fun initBtnPreviousPage() {
        btnPreviousPage.setOnClickListener {
            if(downloadPage != 1) {
                downloadPage --
                getPopularMovie(downloadPage)
            }
        }
    }

    private fun getCachePath() : String =
        StringBuilder()
            .append(activity?.filesDir.toString())
            .append(PATH_NAME_MOVIE_CACHE)
            .append("/")
            .toString()

    private fun createDetailsMovieActivity(movies: List<Movie>, movieId: Int) {
        var movie: Movie? = null
        movies.forEach {
            if (it.id == movieId) {
                movie = it
            }
        }
        movie?.let {
            val intent = DetailsMovieActivity.newIntent(activity?.applicationContext!!, it)
            startActivity(intent)
        }
    }

}