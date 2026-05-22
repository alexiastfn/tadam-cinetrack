package com.example.cinetrack

import android.app.Application
import com.example.cinetrack.data.CineTrackDatabase
import com.example.cinetrack.data.MovieRepository
import com.example.cinetrack.data.RetrofitInstance

class CineTrackApplication : Application() {
    val database: CineTrackDatabase by lazy {
        CineTrackDatabase.getDatabase(this)
    }

    val repository: MovieRepository by lazy {
        MovieRepository(
            dao = database.cineTrackDao(),
            api = RetrofitInstance.api
        )
    }
}