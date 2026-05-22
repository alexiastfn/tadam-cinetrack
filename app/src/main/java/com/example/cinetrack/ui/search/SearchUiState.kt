package com.example.cinetrack.ui.search

import com.example.cinetrack.data.model.TmdbMovie

sealed interface SearchUiState {
    object Idle : SearchUiState          // ecran gol, nicio cautare inca
    object Loading : SearchUiState
    object Empty : SearchUiState         // cautare facuta dar 0 rezultate
    data class Success(val movies: List<TmdbMovie>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}