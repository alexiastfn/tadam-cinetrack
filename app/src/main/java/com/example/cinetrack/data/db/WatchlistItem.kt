package com.example.cinetrack.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistItem(
    @PrimaryKey
    val tmdbId: Int,
    val title: String,
    val posterPath: String?,
    val genreIds: List<Int> = emptyList(),
    val addedAt: Long = System.currentTimeMillis()
)