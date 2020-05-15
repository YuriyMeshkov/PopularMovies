package com.jobc.popularmoviestest.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobc.popularmoviestest.*
import com.jobc.popularmoviestest.data.movies.model.movie.Movie
import com.jobc.popularmoviestest.main.adapter.MoviesAdapterRW
import com.jobc.popularmoviestest.main.utils.ItemOffsetDecoration
import com.jobc.popularmoviestest.main.utils.MovieSelectedCallback
import kotlinx.android.synthetic.main.popular_movies_fragment.*


class PopularMoviesFragment : Fragment() {

    private lateinit var viewModel: PopularMoviesViewModel
    private var adapterRW: MoviesAdapterRW? = null
    private var downloadPage = 1
    private var movieSelectedCallback: MovieSelectedCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        movieSelectedCallback = context as MovieSelectedCallback
    }

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

    override fun onDetach() {
        super.onDetach()
        movieSelectedCallback = null
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
                                createDetailsMovieActivityOrFragment(movies, movieId)
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
                @Suppress
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

    private fun createDetailsMovieActivityOrFragment(movies: List<Movie>, movieId: Int) {
        var movie: Movie? = null
        movies.forEach {
            if (it.id == movieId) {
                movie = it
            }
        }
        movie?.let {
            movieSelectedCallback?.onMovieSelected(it)
        }
    }
}
