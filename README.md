# CineTrack 🎬

**Alexia Stefa, SCPD**

A personal movie tracker for Android built with Kotlin and Jetpack Compose. Search for films, build your watchlist, mark movies as watched, and rate them with a personal review.

---

## Features

- **Browse popular movies**: Home screen displays trending films fetched from the TMDB API
- **Search**: Search for any film by title 
- **Watchlist**: Add movies to your personal watchlist 
- **Mark as watched**: Once you've seen a film, mark it as watched and give it a star rating (1–5) and an optional written review
- **Watched history**: A dedicated screen lists all films you've seen, with ratings and reviews visible at a glance
- **Genre filtering**: Filter your watchlist and watched list by genre
- **Cast & crew**: View the main cast for each film directly on the detail screen
- **Trailer search**: Search for a film's trailer via Google Search 
- **Dark / Light mode**: Toggle between themes from the Settings screen

---

## Architecture

The app follows the recommended Android MVVM architecture with three distinct layers.

The **UI layer** consists of Jetpack Compose screens that collect `StateFlow` objects
from their respective ViewModels and render the current state. No business logic lives
in the screens.

The **ViewModel layer** holds and exposes UI state, handles user interactions, and
delegates data operations to the repository. Each screen has its own ViewModel.

The **data layer** contains a single `MovieRepository` that acts as the single source
of truth. It coordinates two data sources: the TMDB API (accessed via Retrofit) for
remote movie data, and a local Room database for the user's watchlist and watched
history. ViewModels are unaware of where data comes from, they only talk to the
repository.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Navigation | Jetpack Navigation Compose |
| Local database | Room |
| Networking | Retrofit + Gson |
| Image loading | Coil |
| Preferences | DataStore |
| Architecture | MVVM + Repository pattern |
| Movie data | TMDB API |

---

## Screens

| Screen | Route | Description |
|---|---|---|
| Home | `home` | Grid of popular movies (`LazyVerticalGrid`) |
| Search | `search` | Search bar with real-time results (`LazyColumn`) |
| Watchlist | `watchlist` | Personal watchlist with genre filter chips |
| Watched | `watched` | Watched history with ratings, reviews, and genre filters |
| Movie Detail | `detail/{movieId}` | Poster, overview, cast, trailer search |
| Settings | `settings` | Dark/light mode toggle |

The trailer search feature uses an explicit Android `Intent` with
`ACTION_VIEW` to open the device's default browser with a Google Search
query for the selected film's trailer.

---

## Data Storage

Two Room entities are stored locally on the device:

**WatchlistItem** -> movies the user wants to watch:
```
tmdbId, title, posterPath, genreIds, addedAt
```

**WatchedItem** -> movies the user has watched:
```
tmdbId, title, posterPath, genreIds, rating, review, watchedAt
```

---

## API

This app uses the [TMDB API v3](https://developer.themoviedb.org/docs/getting-started). The following endpoints are used:

| Endpoint | Purpose |
|---|---|
| `GET /movie/popular` | Home screen — trending movies |
| `GET /search/movie?query=` | Search screen |
| `GET /movie/{id}` | Movie detail screen |
| `GET /movie/{id}/credits` | Cast section on detail screen |
| `GET /movie/{id}/videos` | Trailer key for search |
| `GET /genre/movie/list` | Genre names for filter chips |

The API key is stored in `local.properties` and injected via `BuildConfig` 

---

## Getting Started

### Prerequisites

- Android SDK 35
- A TMDB API key from [themoviedb.org](https://www.themoviedb.org/settings/api)

### Setup

1. Clone the repository:
   ```bash
   git clone git@github.com:alexiastfn/tadam-cinetrack.git
   ```

2. Open the project in Android Studio and let Gradle sync.

3. Add your TMDB API key to `local.properties`:
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```

4. Run the app on an emulator or physical device (I used Pixel 8 API 35).


---
