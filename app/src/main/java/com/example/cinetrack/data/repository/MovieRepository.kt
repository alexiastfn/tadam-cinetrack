package com.example.cinetrack.data.repository

import com.example.cinetrack.data.db.CineTrackDao
import com.example.cinetrack.data.model.TmdbMovie
import com.example.cinetrack.data.db.WatchedItem
import com.example.cinetrack.data.db.WatchlistItem
import com.example.cinetrack.data.api.TmdbApiService
import com.example.cinetrack.data.model.CastMember
import com.example.cinetrack.data.model.Genre
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

    suspend fun getGenres(): List<Genre> =
        withContext(Dispatchers.IO) {
            api.getGenres().genres
        }

    // --- Local (Room) ---

    fun getWatchlist(): Flow<List<WatchlistItem>> = dao.getWatchlist()

    fun getWatchedList(): Flow<List<WatchedItem>> = dao.getWatchedList()

    fun isInWatchlist(tmdbId: Int): Flow<Boolean> = dao.isInWatchlist(tmdbId)

    fun isWatched(tmdbId: Int): Flow<Boolean> = dao.isWatched(tmdbId)

    private fun TmdbMovie.extractGenreIds(): List<Int> {
        return when {
            genreIds != null -> genreIds           // vine din /popular sau /search
            genreObjects != null -> genreObjects.map { it.id }  // vine din /movie/{id}
            else -> emptyList()
        }
    }

    suspend fun addToWatchlist(movie: TmdbMovie) =
        withContext(Dispatchers.IO) {
            android.util.Log.d("REPO", "Saving genreIds: ${movie.genreIds}")
            dao.addToWatchlist(
                WatchlistItem(
                    tmdbId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    genreIds = movie.extractGenreIds()
                )
            )
        }

    suspend fun removeFromWatchlist(tmdbId: Int) =
        withContext(Dispatchers.IO) {
            dao.removeFromWatchlist(tmdbId)
        }

    suspend fun markAsWatched(movie: TmdbMovie, rating: Int, review: String) =
        withContext(Dispatchers.IO) {
            android.util.Log.d("REPO", "Saving genreIds watched: ${movie.genreIds}")
            dao.markAsWatched(
                WatchedItem(
                    tmdbId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    genreIds = movie.extractGenreIds(),
                    rating = rating,
                    review = review
                )
            )
            dao.removeFromWatchlist(movie.id)
        }

    suspend fun getCredits(movieId: Int): List<CastMember> =
        withContext(Dispatchers.IO) {
            api.getCredits(movieId).cast.take(10)
        }

    suspend fun getTrailerKey(movieId: Int): String? =
        withContext(Dispatchers.IO) {
            api.getVideos(movieId).results
                .filter { it.site == "YouTube" && it.type == "Trailer" }
                .maxByOrNull { it.official }
                ?.key
        }
}