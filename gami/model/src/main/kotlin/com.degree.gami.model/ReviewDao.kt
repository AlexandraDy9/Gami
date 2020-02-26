package com.degree.gami.model

data class ReviewDao(
        var username: String,
        var imageForUser: String?,
        var rating: Double?,
        var review: String?
)