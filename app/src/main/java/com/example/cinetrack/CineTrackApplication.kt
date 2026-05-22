package com.example.cinetrack

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.example.cinetrack.data.db.CineTrackDatabase
import com.example.cinetrack.data.repository.MovieRepository
import com.example.cinetrack.data.api.RetrofitInstance
import com.example.cinetrack.data.settings.SettingsRepository

class CineTrackApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .crossfade(true)
                .build()
        )
    }
    val database: CineTrackDatabase by lazy {
        CineTrackDatabase.getDatabase(this)
    }

    val repository: MovieRepository by lazy {
        MovieRepository(
            dao = database.cineTrackDao(),
            api = RetrofitInstance.api
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }
}