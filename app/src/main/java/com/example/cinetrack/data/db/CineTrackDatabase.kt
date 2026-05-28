package com.example.cinetrack.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cinetrack.Converters


@Database(
    entities = [WatchlistItem::class, WatchedItem::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CineTrackDatabase : RoomDatabase() {

    abstract fun cineTrackDao(): CineTrackDao

    companion object {
        @Volatile   // aceeasi valoare vizibila intre thread-uri
        private var Instance: CineTrackDatabase? = null

        fun getDatabase(context: Context): CineTrackDatabase {
            // synchronized pt evitare race condition
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    CineTrackDatabase::class.java,
                    "cinetrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}