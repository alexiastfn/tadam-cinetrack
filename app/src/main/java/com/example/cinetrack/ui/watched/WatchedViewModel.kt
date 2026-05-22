package com.example.cinetrack.ui.watched

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinetrack.CineTrackApplication
import com.example.cinetrack.data.repository.MovieRepository
import com.example.cinetrack.data.db.WatchedItem
import com.example.cinetrack.data.model.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class WatchedViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _selectedGenreId = MutableStateFlow<Int?>(null)
    val selectedGenreId: StateFlow<Int?> = _selectedGenreId.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    val uiState: StateFlow<WatchedUiState> = combine(
        repository.getWatchedList(),
        _selectedGenreId,
        _genres
    ) { items, genreId, genres ->
        val filtered = if (genreId == null || genres.isEmpty()) {
            items
        } else {
            items.filter { it.genreIds.contains(genreId) }
        }
        if (filtered.isEmpty()) WatchedUiState.Empty
        else WatchedUiState.Success(filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WatchedUiState.Empty
    )


    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            try {
                _genres.value = repository.getGenres()
            } catch (e: Exception) {

            }
        }
    }

    fun setGenreFilter(genreId: Int?) {
        _selectedGenreId.value = genreId
    }

    fun getGenreNames(genreIds: List<Int>): List<String> {
        val genreMap = _genres.value.associateBy { it.id }
        return genreIds.mapNotNull { genreMap[it]?.name }
    }

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