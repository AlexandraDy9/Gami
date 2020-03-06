package com.university.gami_android.ui.events

import android.content.Context
import com.university.gami_android.model.Event
import com.university.gami_android.ui.base.BaseContract
interface EventListContract {

    interface View : BaseContract.View {
        fun updateEventList(eventList: List<Event>?)
        fun updateBookmarkedEventsList(eventList: List<Event>?)
    }

    interface Presenter {
        fun getEvents(context: Context, type: Boolean)
        fun getBookmarkedEvents(context: Context)
    }
}