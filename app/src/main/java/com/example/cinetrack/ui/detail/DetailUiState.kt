package com.example.cinetrack.ui.detail

import com.example.cinetrack.data.model.TmdbMovie

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val movie: TmdbMovie,
        val isInWatchlist: Boolean = false,
        val isWatched: Boolean = false
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}