package com.example.cinetrack.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.cinetrack.BuildConfig
import com.example.cinetrack.data.model.CreditsResponse
import com.example.cinetrack.data.model.GenreResponse
import com.example.cinetrack.data.model.TmdbMovie
import com.example.cinetrack.data.model.TmdbResponse
import com.example.cinetrack.data.model.VideosResponse

interface TmdbApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): TmdbResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String
    ): TmdbResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int
    ): TmdbMovie

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreResponse

    @GET("movie/{id}/credits")
    suspend fun getCredits(
        @Path("id") id: Int
    ): CreditsResponse

    @GET("movie/{id}/videos")
    suspend fun getVideos(
        @Path("id") id: Int
    ): VideosResponse
}

