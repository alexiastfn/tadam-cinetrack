package com.example.cinetrack.ui.detail

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.cinetrack.data.model.CastMember
import androidx.compose.foundation.lazy.items

@Composable
fun DetailScreen(
    movieId: Int,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.provideFactory(movieId))
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()
    val cast by viewModel.cast.collectAsState()
    val trailerKey by viewModel.trailerKey.collectAsState()

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
                cast = cast,
                trailerKey = trailerKey,
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
    cast: List<CastMember>,
    trailerKey: String?,
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

            // Trailer
            trailerKey?.let { key ->
                TrailerButton(trailerKey = key,
                    movieTitle = state.movie.title )
            }

            // Cast
            if (cast.isNotEmpty()) {
                CastSection(cast = cast)
            }
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

@Composable
private fun TrailerButton(
    trailerKey: String,
    movieTitle: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${Uri.encode("$movieTitle trailer")}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Niciun browser disponibil", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("▶ Cauta trailer")
    }
}


@Composable
private fun CastSection(
    cast: List<CastMember>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Distributie",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cast, key = { it.id }) { member ->
                CastCard(member = member)
            }
        }
    }
}

@Composable
private fun CastCard(
    member: CastMember,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = if (member.profilePath != null)
                "https://image.tmdb.org/t/p/w200${member.profilePath}"
            else
                "https://via.placeholder.com/200x300?text=N/A",
            contentDescription = member.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = member.character,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}