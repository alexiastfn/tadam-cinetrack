package com.example.cinetrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cinetrack.ui.detail.DetailScreen
import com.example.cinetrack.ui.home.HomeScreen
import com.example.cinetrack.ui.search.SearchScreen
import com.example.cinetrack.ui.watched.WatchedScreen
import com.example.cinetrack.ui.watchlist.WatchlistScreen


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Watchlist : Screen("watchlist")
    object Watched : Screen("watched")
    object Detail : Screen("detail/{movieId}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }

    object Search : Screen("search")
}

@Composable
fun CineTrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Detail.createRoute(movieId))
                }
            )
        }

        composable(Screen.Watchlist.route) {
            WatchlistScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Detail.createRoute(movieId))
                }
            )
        }

        composable(Screen.Watched.route) {
            WatchedScreen()
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
                ?: error("movieId cannot be null")
            DetailScreen(movieId = movieId)
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.Detail.createRoute(movieId))
                }
            )
        }
    }
}