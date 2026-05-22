package com.example.cinetrack.data.repository

import com.example.cinetrack.data.db.CineTrackDao
import com.example.cinetrack.data.model.TmdbMovie
import com.example.cinetrack.data.db.WatchedItem
import com.example.cinetrack.data.db.WatchlistItem
import com.example.cinetrack.data.api.TmdbApiService
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val dao: CineTrackDao,
    private val api: TmdbApiService
) {

    // --- Remote (TMDB) ---

    suspend fun getPopularMovies(): List<TmdbMovie> =
        api.getPopularMovies().results

    suspend fun searchMovies(query: String): List<TmdbMovie> =
        api.searchMovies(query = query).results

    suspend fun getMovieDetails(id: Int): TmdbMovie =
        api.getMovieDetails(id)

    // --- Local (Room) ---

    fun getWatchlist(): Flow<List<WatchlistItem>> = dao.getWatchlist()

    fun getWatchedList(): Flow<List<WatchedItem>> = dao.getWatchedList()

    fun isInWatchlist(tmdbId: Int): Flow<Boolean> = dao.isInWatchlist(tmdbId)

    fun isWatched(tmdbId: Int): Flow<Boolean> = dao.isWatched(tmdbId)

    suspend fun addToWatchlist(movie: TmdbMovie) {
        dao.addToWatchlist(
            WatchlistItem(
                tmdbId = movie.id,
                title = movie.title,
                posterPath = movie.posterPath
            )
        )
    }

    suspend fun removeFromWatchlist(tmdbId: Int) {
        dao.removeFromWatchlist(WatchlistItem(tmdbId = tmdbId, title = "", posterPath = null))
    }

    suspend fun markAsWatched(movie: TmdbMovie, rating: Int, review: String) {
        dao.markAsWatched(
            WatchedItem(
                tmdbId = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                rating = rating,
                review = review
            )
        )

        dao.removeFromWatchlist(WatchlistItem(tmdbId = movie.id, title = "", posterPath = null))
    }
}