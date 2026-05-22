package com.example.cinetrack.data.model

data class Video(
    val key: String,       // ID-ul YouTube
    val site: String,      // "YouTube"
    val type: String,      // "Trailer", "Teaser", etc.
    val official: Boolean
)