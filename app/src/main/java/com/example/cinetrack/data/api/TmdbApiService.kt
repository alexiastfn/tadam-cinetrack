package com.example.cinetrack.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.cinetrack.BuildConfig
import com.example.cinetrack.data.model.GenreResponse
import com.example.cinetrack.data.model.TmdbMovie
import com.example.cinetrack.data.model.TmdbResponse

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
}

