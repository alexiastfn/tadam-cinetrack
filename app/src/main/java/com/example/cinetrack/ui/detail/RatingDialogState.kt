package com.example.cinetrack.ui.detail

data class RatingDialogState(
    val isVisible: Boolean = false,
    val rating: Int = 0,
    val review: String = ""
)