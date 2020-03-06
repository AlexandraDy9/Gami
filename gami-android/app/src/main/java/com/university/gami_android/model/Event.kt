package com.university.gami_android.model


data class Event (
    var name: String = "",
    var description: String = "",
    var numberOfAttendees: Int = 0,
    var categoryName: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var ageMin: Int = 0,
    var ageMax: Int = 0,
    var startTime: String = "",
    var endTime: String = ""
)