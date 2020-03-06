package com.university.gami_android.ui.event_details

import android.content.Context
import com.university.gami_android.ui.base.BaseContract


class EventDetailsContract {
    interface View : BaseContract.View {
    }

    interface Presenter {
        fun joinEvent(eventName: String, context: Context)

        fun getReviews(eventName: String, context: Context)

        fun getEvent(eventName: String, context: Context)

        fun getHost(eventName: String, context: Context)

        fun getNumberOfJoinedUsers(eventName: String, context: Context)
    }
}