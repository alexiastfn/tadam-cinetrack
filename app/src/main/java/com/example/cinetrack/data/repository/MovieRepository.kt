package com.example.cinetrack.data.repository

import com.example.cinetrack.data.db.CineTrackDao
import com.example.cinetrack.data.model.TmdbMovie
import com.example.cinetrack.data.db.WatchedItem
import com.example.cinetrack.data.db.WatchlistItem
import com.example.cinetrack.data.api.TmdbApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MovieRepository(
    private val dao: CineTrackDao,
    private val api: TmdbApiService
) {

    // --- Remote (Retrofit) ---
    
    suspend fun getPopularMovies(): List<TmdbMovie> =
        withContext(Dispatchers.IO) {
            api.getPopularMovies().results
        }

    suspend fun searchMovies(query: String): List<TmdbMovie> =
        withContext(Dispatchers.IO) {
            api.searchMovies(query = query).results
        }

    suspend fun getMovieDetails(id: Int): TmdbMovie =
        withContext(Dispatchers.IO) {
            api.getMovieDetails(id)
        }

    // --- Local (Room) ---

    fun getWatchlist(): Flow<List<WatchlistItem>> = dao.getWatchlist()

    fun getWatchedList(): Flow<List<WatchedItem>> = dao.getWatchedList()

    fun isInWatchlist(tmdbId: Int): Flow<Boolean> = dao.isInWatchlist(tmdbId)

    fun isWatched(tmdbId: Int): Flow<Boolean> = dao.isWatched(tmdbId)

    suspend fun addToWatchlist(movie: TmdbMovie) =
        withContext(Dispatchers.IO) {
            dao.addToWatchlist(
                WatchlistItem(
                    tmdbId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath
                )
            )
        }

    suspend fun removeFromWatchlist(tmdbId: Int) =
        withContext(Dispatchers.IO) {
            dao.removeFromWatchlist(tmdbId)
        }

    suspend fun markAsWatched(movie: TmdbMovie, rating: Int, review: String) =
        withContext(Dispatchers.IO) {
            dao.markAsWatched(
                WatchedItem(
                    tmdbId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    rating = rating,
                    review = review
                )
            )
            dao.removeFromWatchlist(movie.id)
        }
}