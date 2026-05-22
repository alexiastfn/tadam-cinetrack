package com.example.cinetrack.ui.watched

import com.example.cinetrack.data.db.WatchedItem

sealed interface WatchedUiState {
    object Empty : WatchedUiState
    data class Success(val items: List<WatchedItem>) : WatchedUiState
}