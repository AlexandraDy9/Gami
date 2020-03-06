package com.university.gami_android.ui.main

import android.content.Context
import com.university.gami_android.ui.base.BaseContract

interface MainContract {

    interface View : BaseContract.View {
        fun navigateToLoginActivity(context: Context)
        fun navigateToProfileActivity(context: Context)
    }

    interface Presenter {
        fun doLogout(context: Context)
        fun getPhotos(context: Context)
    }
}