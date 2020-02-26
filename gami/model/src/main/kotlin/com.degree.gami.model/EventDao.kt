package com.degree.gami.model

import java.time.LocalDateTime

data class EventDao (
        var name: String,
        var description: String,
        var numberOfAttendees: Int,
        var categoryName: String,
        var longitude: Double,
        var latitude: Double,
        var ageMin: Int,
        var ageMax: Int,
        var startTime: LocalDateTime,
        var endTime: LocalDateTime
)