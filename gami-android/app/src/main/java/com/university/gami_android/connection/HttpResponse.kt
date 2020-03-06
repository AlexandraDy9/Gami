package com.university.gami_android.connection

class HttpResponse<T> {
    var response: T? = null
    var error: String? = null
    var isSuccessful: Boolean = false
}