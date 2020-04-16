package com.university.gami_android.model

data class ReviewDao(
    var username: String,
    var imageForUser: String?,
    var rating: Double?,
    var review: String?
)