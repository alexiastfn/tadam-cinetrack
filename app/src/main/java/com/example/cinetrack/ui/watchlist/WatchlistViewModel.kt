package com.example.cinetrack.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinetrack.CineTrackApplication
import com.example.cinetrack.data.repository.MovieRepository
import com.example.cinetrack.data.db.WatchlistItem
import com.example.cinetrack.data.model.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _selectedGenreId = MutableStateFlow<Int?>(null)
    val selectedGenreId: StateFlow<Int?> = _selectedGenreId.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    val uiState: StateFlow<WatchlistUiState> = combine(
        repository.getWatchlist(),
        _selectedGenreId,
        _genres
    ) { items, genreId, genres ->
        val filtered = if (genreId == null || genres.isEmpty()) {
            items
        } else {
            items.filter { it.genreIds.contains(genreId) }
        }
        if (filtered.isEmpty()) WatchlistUiState.Empty
        else WatchlistUiState.Success(filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WatchlistUiState.Empty
    )

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            try {
                _genres.value = repository.getGenres()
                android.util.Log.d("GENRES", "Loaded ${_genres.value.size} genres")
            } catch (e: Exception) {
                android.util.Log.e("GENRES", "Error: ${e.message}")

            }
        }
    }

    fun setGenreFilter(genreId: Int?) {
        _selectedGenreId.value = genreId
    }

    fun removeFromWatchlist(item: WatchlistItem) {
        viewModelScope.launch { repository.removeFromWatchlist(item.tmdbId) }
    }

    fun getGenreNames(genreIds: List<Int>): List<String> {
        android.util.Log.d("GENRES", "genreIds for item: $genreIds, genres loaded: ${_genres.value.size}")
        val genreMap = _genres.value.associateBy { it.id }
        return genreIds.mapNotNull { genreMap[it]?.name }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as CineTrackApplication)
                WatchlistViewModel(application.repository)
            }
        }
    }
}