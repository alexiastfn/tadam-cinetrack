package com.example.cinetrack.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinetrack.CineTrackApplication
import com.example.cinetrack.data.MovieRepository
import com.example.cinetrack.data.WatchlistItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WatchlistUiState {
    object Empty : WatchlistUiState
    data class Success(val items: List<WatchlistItem>) : WatchlistUiState
}

class WatchlistViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    // stateIn converteste Flow din Room in StateFlow pentru Compose
    // SharingStarted.WhileSubscribed(5000) = opreste colectarea dupa 5s
    // fara subscribers (cand app e in background) pt economie de resurse
    val uiState: StateFlow<WatchlistUiState> = repository.getWatchlist()
        .map { items ->
            if (items.isEmpty()) WatchlistUiState.Empty
            else WatchlistUiState.Success(items)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WatchlistUiState.Empty
        )

    fun removeFromWatchlist(item: WatchlistItem) {
        viewModelScope.launch {
            repository.removeFromWatchlist(item.tmdbId)
        }
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