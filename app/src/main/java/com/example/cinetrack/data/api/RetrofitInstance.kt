package com.example.cinetrack.data.api

import com.example.cinetrack.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// config client http
object RetrofitInstance {

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .build()
        chain.proceed(originalRequest.newBuilder().url(newUrl).build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: TmdbApiService by lazy {
        retrofit.create(TmdbApiService::class.java)
    }
}