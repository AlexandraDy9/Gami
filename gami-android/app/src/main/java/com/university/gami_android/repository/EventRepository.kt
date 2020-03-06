package com.university.gami_android.repository

import com.university.gami_android.model.*
import com.university.gami_android.ui.event_details.ReviewDao
import retrofit2.Call
import retrofit2.http.*


interface EventRepository {
    @GET("event/{isIndoor}")
    fun getEvents(@Header("Authorization") auth: String, @Path(value = "isIndoor") isIndoor: Boolean): Call<List<Event>>

    @PATCH("event/review/{eventName}")
    fun saveReview(@Header("Authorization") auth: String, @Path(value = "eventName") eventName: String,
                   @Body sendReview: SendReview
    ) : Call<Void>

    @GET("event/reviews/{name}")
    fun getReviews(@Header("Authorization") auth: String, @Path(value = "name") name: String) : Call<List<ReviewDao>>

    @GET("event/byName/{name}")
    fun getEvent(@Header("Authorization") auth: String, @Path(value = "name") name: String) : Call<Event>


    @POST("event")
    fun addEvent(@Header("Authorization") auth: String, @Body event: Event): Call<Void>

    @GET("category")
    fun getCategories(@Header("Authorization") auth: String): Call<List<Category>>

    @GET("event/ageRanges")
    fun getAgeRanges(@Header("Authorization") auth: String): Call<List<AgeRange>>

    @GET("event/host/{eventName}")
    fun getHost(@Header("Authorization") auth: String, @Path(value = "eventName") eventName: String) : Call<Host>

    @GET("event/joinedUsers/{eventName}")
    fun getNumberOfJoinedUsers(@Header("Authorization") auth: String, @Path(value = "eventName") eventName: String) : Call<Int>
}