package com.example.cinetrack.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinetrack.CineTrackApplication
import com.example.cinetrack.data.MovieRepository
import com.example.cinetrack.data.TmdbMovie

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch



@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {

        _query
            .debounce(400)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query -> search(query) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    private suspend fun search(query: String) {
        _uiState.value = SearchUiState.Loading
        try {
            val results = repository.searchMovies(query)
            _uiState.value = if (results.isEmpty()) {
                SearchUiState.Empty
            } else {
                SearchUiState.Success(results)
            }
        } catch (e: Exception) {
            _uiState.value = SearchUiState.Error(e.message ?: "Eroare la cautare")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as CineTrackApplication)
                SearchViewModel(application.repository)
            }
        }
    }
}