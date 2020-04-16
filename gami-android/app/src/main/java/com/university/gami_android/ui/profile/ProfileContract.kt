package com.university.gami_android.ui.profile

import android.content.Context
import com.university.gami_android.model.User
import com.university.gami_android.ui.base.BaseContract

interface ProfileContract {
    interface View : BaseContract.View

    interface Presenter {
        fun getUser(context: Context)
        fun getPhotos(context: Context)
        fun editUser(user: User, context: Context)
        fun uploadUserPhoto(context: Context, uri: String)
    }
}