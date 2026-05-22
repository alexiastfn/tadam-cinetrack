package com.example.cinetrack.ui.watchlist

import com.example.cinetrack.data.db.WatchlistItem

sealed interface WatchlistUiState {
    object Empty : WatchlistUiState
    data class Success(val items: List<WatchlistItem>) : WatchlistUiState
}