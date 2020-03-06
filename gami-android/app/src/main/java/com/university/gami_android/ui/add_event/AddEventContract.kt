package com.university.gami_android.ui.add_event

import android.content.Context
import com.university.gami_android.model.Event
import com.university.gami_android.ui.base.BaseContract

interface AddEventContract {
    interface View : BaseContract.View {
        fun navigateToHomeActivity(context: Context)
    }

    interface Presenter {
        fun addEvent(event: Event, context: Context)

        fun getCategories(context: Context)

        fun getAgeRanges(context: Context)
    }
}