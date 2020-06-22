package com.university.gami_android.ui.event_details

import android.content.Context
import com.university.gami_android.ui.base.BaseContract


class EventDetailsContract {
    interface View : BaseContract.View {
        fun progressBarVisibility()
    }

    interface Presenter {
        fun joinEvent(eventName: String, context: Context)
        fun leftEvent(eventName: String, context: Context)
        fun getReviews(eventName: String, context: Context)
        fun getEvent(eventName: String, context: Context)
        fun getHost(eventName: String, context: Context)
        fun getJoinedUsers(eventName: String, context: Context)
    }
}