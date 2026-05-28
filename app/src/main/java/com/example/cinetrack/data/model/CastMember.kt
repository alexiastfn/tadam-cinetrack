package com.example.cinetrack.data.model

import com.google.gson.annotations.SerializedName

// json -> obiecte kotlin
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    @SerializedName("profile_path")
    val profilePath: String?
)