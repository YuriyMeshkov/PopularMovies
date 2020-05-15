package com.jobc.popularmoviestest.data.movies.model.movie

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class Movie(
    @SerializedName("poster_path")
    val posterPath: String? = null,
    val adult: Boolean = false,
    val overview: String = "",
    @SerializedName("release_date")
    val releaseDate: String = "",
    /*@SerializedName("genre_ids")
    val genreIds: List<Int>? = null,*/
    val id: Int = 0,
    @SerializedName("original_title")
    val originalTitle: String = "",
    @SerializedName("original_language")
    val originalLanguage: String = "",
    val title: String = "",
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
    val popularity: Double = 0.0,
    @SerializedName("vote_count")
    val voteCount: Int = 0,
    val video: Boolean = false,
    @SerializedName("vote_average")
    val voteAverage: Double = 0.0,
    var bitmap: Bitmap? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        1 == source.readInt(),
        source.readString()!!,
        source.readString()!!,
        source.readInt(),
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString(),
        source.readDouble(),
        source.readInt(),
        1 == source.readInt(),
        source.readDouble(),
        source.readParcelable<Bitmap>(Bitmap::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(posterPath)
        writeInt((if (adult) 1 else 0))
        writeString(overview)
        writeString(releaseDate)
        writeInt(id)
        writeString(originalTitle)
        writeString(originalLanguage)
        writeString(title)
        writeString(backdropPath)
        writeDouble(popularity)
        writeInt(voteCount)
        writeInt((if (video) 1 else 0))
        writeDouble(voteAverage)
        writeParcelable(bitmap, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Movie> = object : Parcelable.Creator<Movie> {
            override fun createFromParcel(source: Parcel): Movie =
                Movie(
                    source
                )
            override fun newArray(size: Int): Array<Movie?> = arrayOfNulls(size)
        }
    }
}