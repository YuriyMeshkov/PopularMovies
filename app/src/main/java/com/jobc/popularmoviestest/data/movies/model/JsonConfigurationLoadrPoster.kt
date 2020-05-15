package com.jobc.popularmoviestest.data.movies.model

import com.google.gson.annotations.SerializedName

data class ConfigurationRequestForPoster (
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("secure_base_url")
    val secureBaseUrl: String,
    @SerializedName("poster_sizes")
    val posterSize: List<String>
)

data class ConfigurationList(
    @SerializedName("images")
    val images: ConfigurationRequestForPoster
)