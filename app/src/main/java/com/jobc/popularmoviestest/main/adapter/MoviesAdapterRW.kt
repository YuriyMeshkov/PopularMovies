package com.example.popularmoviestest.main.adapter

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.popularmoviestest.R
import com.example.popularmoviestest.data.movies.model.movie.Movie
import com.example.popularmoviestest.main.utils.ResultLoadPoster
import kotlinx.android.synthetic.main.movie_item.view.*

class MoviesAdapterRW (
    moviesList: MutableList<Movie>,
    liveDataLoadPoster: LiveData<List<ResultLoadPoster>>,
    ownerLc: LifecycleOwner,
    private val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<MoviesAdapterRW.MoviesHolder>() {

    private var postersListLoaded: MutableList <ResultLoadPoster> = mutableListOf()
    private var movies = moviesList
    private var isNotifyDataSetChanged = false

    init {
        liveDataLoadPoster.observe(ownerLc, Observer{
            val posterLoad = it ?: return@Observer
            setPostersToMovies(posterLoad)
        })
    }

    fun setAddMovie(movies: List<Movie>) {
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    fun  clearListMovie() {
        movies.clear()
        postersListLoaded.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MoviesHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false))

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MoviesHolder, position: Int) {
        isNotifyDataSetChanged = true
        holder.bind(movies[position], clickListener)
        isNotifyDataSetChanged = false
    }

    private fun setPostersToMovies(postersListLoaded: List<ResultLoadPoster>) {
        movies.forEach{movie ->
            postersListLoaded.forEach {poster ->
                if (movie.id == poster.id) {
                    when(poster.bitmap) {
                        null -> {
                            movie.bitmap = BitmapFactory
                                .decodeResource(
                                    Resources.getSystem(),
                                    R.mipmap.image_loading_error
                                )
                        }
                        else -> {
                            movie.bitmap = poster.bitmap
                        }
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class MoviesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie, clickListener: (Int) -> Unit) {
            with(itemView) {
                tvNameMovie.text = movie.title
                btnDataMovie.setOnClickListener {
                    clickListener(movie.id)
                }
                when(movie.bitmap) {
                    null -> pbLoadPoster.visibility = View.VISIBLE
                    else -> {
                        pbLoadPoster.visibility = View.GONE
                        ivPoster.setImageBitmap(movie.bitmap)
                    }
                }
            }
        }
    }
}