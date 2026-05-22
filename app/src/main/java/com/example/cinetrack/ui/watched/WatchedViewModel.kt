package com.example.cinetrack.ui.watched

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinetrack.CineTrackApplication
import com.example.cinetrack.data.MovieRepository
import com.example.cinetrack.data.WatchedItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface WatchedUiState {
    object Empty : WatchedUiState
    data class Success(val items: List<WatchedItem>) : WatchedUiState
}

class WatchedViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    val uiState: StateFlow<WatchedUiState> = repository.getWatchedList()
        .map { items ->
            if (items.isEmpty()) WatchedUiState.Empty
            else WatchedUiState.Success(items)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WatchedUiState.Empty
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as CineTrackApplication)
                WatchedViewModel(application.repository)
            }
        }
    }
}