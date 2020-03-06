package com.university.gami_android.ui.events

import android.content.Context
import com.university.gami_android.model.Event
import com.university.gami_android.ui.base.BaseContract

interface EventMapContract {

    interface View : BaseContract.View {
        fun updateEventList(eventList: List<Event>?)
    }

    interface Presenter {
        fun getEvents(context: Context, type: Boolean)
    }
}