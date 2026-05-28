package com.example.cinetrack.ui.home

import com.example.cinetrack.data.model.TmdbMovie

sealed interface HomeUiState {
    data class Success(val movies: List<TmdbMovie>) : HomeUiState
    object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
}