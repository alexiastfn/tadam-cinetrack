package com.example.cinetrack.ui.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cinetrack.CineTrackApplication
import com.example.cinetrack.data.repository.MovieRepository
import com.example.cinetrack.data.model.TmdbMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val movie: TmdbMovie,
        val isInWatchlist: Boolean = false,
        val isWatched: Boolean = false
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

data class RatingDialogState(
    val isVisible: Boolean = false,
    val rating: Int = 0,
    val review: String = ""
)

class DetailViewModel(
    private val repository: MovieRepository,
    private val movieId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
//    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    //

    private val _movie = MutableStateFlow<TmdbMovie?>(null)
    private val _error = MutableStateFlow<String?>(null)

    // Combina cele 3 surse de date intr-un singur UiState
    val uiState: StateFlow<DetailUiState> = combine(
        _movie,
        repository.isInWatchlist(movieId),
        repository.isWatched(movieId)
    ) { movie, inWatchlist, watched ->
        when {
            _error.value != null -> DetailUiState.Error(_error.value!!)
            movie == null -> DetailUiState.Loading
            else -> DetailUiState.Success(
                movie = movie,
                isInWatchlist = inWatchlist,
                isWatched = watched
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailUiState.Loading
    )

    private val _dialogState = MutableStateFlow(RatingDialogState())
    val dialogState: StateFlow<RatingDialogState> = _dialogState.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            try {
                val movie = repository.getMovieDetails(movieId)
                _movie.value = movie
            } catch (e: Exception) {
                _error.value = e.message ?: "Eroare necunoscuta"
            }
        }
    }

    fun addToWatchlist() {
        val state = uiState.value as? DetailUiState.Success ?: return
        viewModelScope.launch {
            repository.addToWatchlist(state.movie)
        }
    }

    fun removeFromWatchlist() {
        viewModelScope.launch {
            repository.removeFromWatchlist(movieId)
        }
    }

    fun onMarkAsWatchedClick() {
        val state = uiState.value as? DetailUiState.Success ?: return
        _dialogState.value = RatingDialogState(isVisible = true)
    }

    fun onRatingChange(rating: Int) {
        _dialogState.value = _dialogState.value.copy(rating = rating)
    }

    fun onReviewChange(review: String) {
        _dialogState.value = _dialogState.value.copy(review = review)
    }

    fun onDialogDismiss() {
        _dialogState.value = RatingDialogState()
    }

    fun onSaveWatched() {
        val state = uiState.value as? DetailUiState.Success ?: return
        val dialog = _dialogState.value
        viewModelScope.launch {
            repository.markAsWatched(
                movie = state.movie,
                rating = dialog.rating,
                review = dialog.review
            )
            _dialogState.value = RatingDialogState()
        }
    }

    companion object {
        fun provideFactory(movieId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as CineTrackApplication)
                DetailViewModel(application.repository, movieId)
            }
        }
    }
}