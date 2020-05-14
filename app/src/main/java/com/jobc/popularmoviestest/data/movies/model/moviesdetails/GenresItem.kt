package com.example.popularmoviestest.data.movies.model.moviesdetails

import com.google.gson.annotations.SerializedName

data class GenresItem(@SerializedName("name")
                      val name: String = "",
                      @SerializedName("id")
                      val id: Int = 0)