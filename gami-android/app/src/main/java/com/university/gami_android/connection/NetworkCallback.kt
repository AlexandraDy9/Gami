package com.university.gami_android.connection

interface NetworkCallback<T> {

    fun onSuccess(response: T?)

    fun onError(message: String?)
}