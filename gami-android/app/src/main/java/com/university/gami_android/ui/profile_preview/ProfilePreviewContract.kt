package com.university.gami_android.ui.profile_preview

import android.content.Context
import com.university.gami_android.ui.base.BaseContract

interface ProfilePreviewContract {
    interface View : BaseContract.View {
        fun progressBarVisibility()
    }

    interface Presenter {
        fun getUser(context: Context, username: String)
        fun getPhotos(context: Context, username: String)
        fun getHostedEvents(context: Context, username: String)
    }
}