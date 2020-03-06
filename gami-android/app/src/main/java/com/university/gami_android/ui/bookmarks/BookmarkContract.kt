package com.university.gami_android.ui.bookmarks

import android.content.Context
import com.university.gami_android.model.Event
import com.university.gami_android.ui.base.BaseContract

interface BookmarkContract {
    interface View : BaseContract.View {
        fun navigateToMainActivity(context: Context)
        fun updateEventsList(events: List<Event>?)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun showBookmarks(context: Context)
        fun removeBookmark(context: Context, event: Event)
    }
}