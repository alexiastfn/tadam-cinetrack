package com.example.cinetrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WatchlistItem::class, WatchedItem::class],
    version = 1,
    exportSchema = false
)
abstract class CineTrackDatabase : RoomDatabase() {

    abstract fun cineTrackDao(): CineTrackDao

    companion object {
        @Volatile
        private var Instance: CineTrackDatabase? = null

        fun getDatabase(context: Context): CineTrackDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    CineTrackDatabase::class.java,
                    "cinetrack_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}