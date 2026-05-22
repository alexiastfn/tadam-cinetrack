package com.example.cinetrack.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched")
data class WatchedItem(
    @PrimaryKey
    val tmdbId: Int,
    val title: String,
    val posterPath: String?,
    val rating: Int,
    val review: String,
    val watchedAt: Long = System.currentTimeMillis()
)