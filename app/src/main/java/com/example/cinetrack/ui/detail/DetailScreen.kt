package com.example.cinetrack.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.material.icons.outlined.Star

@Composable
fun DetailScreen(
    movieId: Int,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.provideFactory(movieId))
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()

    when (val state = uiState) {
        is DetailUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is DetailUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is DetailUiState.Success -> {
            DetailContent(
                state = state,
                onAddToWatchlist = viewModel::addToWatchlist,
                onRemoveFromWatchlist = viewModel::removeFromWatchlist,
                onMarkAsWatched = viewModel::onMarkAsWatchedClick,
                modifier = modifier
            )
        }
    }

    if (dialogState.isVisible) {
        RatingDialog(
            dialogState = dialogState,
            onRatingChange = viewModel::onRatingChange,
            onReviewChange = viewModel::onReviewChange,
            onDismiss = viewModel::onDialogDismiss,
            onSave = viewModel::onSaveWatched
        )
    }
}

@Composable
private fun DetailContent(
    state: DetailUiState.Success,
    onAddToWatchlist: () -> Unit,
    onRemoveFromWatchlist: () -> Unit,
    onMarkAsWatched: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Poster
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${state.movie.posterPath}",
            contentDescription = state.movie.title,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        Column(modifier = Modifier.padding(16.dp)) {

            // Titlu + an
            Text(
                text = state.movie.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.movie.releaseDate.take(4), // doar anul
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating TMDB
            Text(
                text = "⭐ ${"%.1f".format(state.movie.voteAverage)} / 10",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descriere
            Text(
                text = state.movie.overview,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Butoane actiune
            ActionButtons(
                isInWatchlist = state.isInWatchlist,
                isWatched = state.isWatched,
                onAddToWatchlist = onAddToWatchlist,
                onRemoveFromWatchlist = onRemoveFromWatchlist,
                onMarkAsWatched = onMarkAsWatched
            )
        }
    }
}

@Composable
private fun ActionButtons(
    isInWatchlist: Boolean,
    isWatched: Boolean,
    onAddToWatchlist: () -> Unit,
    onRemoveFromWatchlist: () -> Unit,
    onMarkAsWatched: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when {
            // Deja vazut
            isWatched -> {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓ Vizionat")
                }
            }
            // In watchlist: poate marca ca vazut sau scoate din lista
            isInWatchlist -> {
                Button(
                    onClick = onMarkAsWatched,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcheaza ca vizionat")
                }
                OutlinedButton(
                    onClick = onRemoveFromWatchlist,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sterge din watchlist")
                }
            }
            // Nu e niciunde, se poate adauga
            else -> {
                Button(
                    onClick = onAddToWatchlist,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Adauga la watchlist")
                }
                OutlinedButton(
                    onClick = onMarkAsWatched,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcheaza ca vizionat")
                }
            }
        }
    }
}

@Composable
private fun RatingDialog(
    dialogState: RatingDialogState,
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cum ti s-a parut?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Star rating 1-5
                Text("Rating:", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { onRatingChange(star) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= dialogState.rating)
                                    Icons.Filled.Star
                                else
                                    Icons.Outlined.Star,
                                contentDescription = "$star stele",
                                tint = if (star <= dialogState.rating)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Review text
                OutlinedTextField(
                    value = dialogState.review,
                    onValueChange = onReviewChange,
                    label = { Text("Review (optional)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = dialogState.rating > 0  // trebuie cel putin 1 stea
            ) {
                Text("Salveaza")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuleaza")
            }
        }
    )
}