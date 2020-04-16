package com.university.gami_android.repository

import com.university.gami_android.model.*
import com.university.gami_android.model.ReviewDao
import retrofit2.Call
import retrofit2.http.*


interface EventRepository {
    @GET("event/category/{name}")
    fun getEventsByCategory(@Header("Authorization") auth: String, @Path(value = "name") name: String): Call<List<Event>>

    @GET("event/")
    fun getEvents(@Header("Authorization") auth: String): Call<List<Event>>

    @PATCH("event/{name}/review")
    fun saveReview(@Header("Authorization") auth: String, @Path(value = "name") name: String, @Body sendReview: SendReview) : Call<Void>

    @GET("event/{name}/reviews")
    fun getReviews(@Header("Authorization") auth: String, @Path(value = "name") name: String) : Call<List<ReviewDao>>

    @GET("event/byName/{name}")
    fun getEvent(@Header("Authorization") auth: String, @Path(value = "name") name: String) : Call<Event>

    @POST("event")
    fun addEvent(@Header("Authorization") auth: String, @Body event: Event): Call<Void>

    @GET("category")
    fun getCategories(@Header("Authorization") auth: String): Call<List<Category>>

    @GET("event/ageRanges")
    fun getAgeRanges(@Header("Authorization") auth: String): Call<List<AgeRange>>

    @GET("event/{name}/host")
    fun getHost(@Header("Authorization") auth: String, @Path(value = "name") name: String) : Call<Host>

    @GET("event/{name}/joinedUsers")
    fun getNumberOfJoinedUsers(@Header("Authorization") auth: String, @Path(value = "name") name: String) : Call<Int>
}