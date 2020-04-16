package com.university.gami_android.ui.joined_events

import android.content.Context
import com.university.gami_android.model.Event
import com.university.gami_android.ui.base.BaseContract

interface JoinedEventsContract {
    interface View : BaseContract.View {
        fun navigateToMainActivity(context: Context)
        fun updateEventsList(events: List<Event>?)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun showEvents(context: Context)
        fun removeEvent(context: Context, event: Event)
    }
}