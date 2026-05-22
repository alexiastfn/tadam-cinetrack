package com.example.cinetrack.data.model

import com.google.gson.annotations.SerializedName

data class TmdbMovie(
    val id: Int,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,      // din /popular si /search
    @SerializedName("genres")
    val genreObjects: List<GenreObject>? = null  // din /movie/{id}
)