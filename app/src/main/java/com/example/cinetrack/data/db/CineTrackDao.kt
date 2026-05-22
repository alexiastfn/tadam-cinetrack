package com.example.cinetrack.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CineTrackDao {

    // --- Watchlist ---

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun addToWatchlist(item: WatchlistItem)

    @Delete
    suspend fun removeFromWatchlist(item: Int)

    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getWatchlist(): Flow<List<WatchlistItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE tmdbId = :tmdbId)")
    fun isInWatchlist(tmdbId: Int): Flow<Boolean>

    // --- Watched ---

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun markAsWatched(item: WatchedItem)

    @Delete
    suspend fun removeFromWatched(item: Int)

    @Query("SELECT * FROM watched ORDER BY watchedAt DESC")
    fun getWatchedList(): Flow<List<WatchedItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM watched WHERE tmdbId = :tmdbId)")
    fun isWatched(tmdbId: Int): Flow<Boolean>
}