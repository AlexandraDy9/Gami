package com.university.gami_android.ui.joined_users

import android.content.Context
import com.university.gami_android.model.User
import com.university.gami_android.ui.base.BaseContract

interface JoinedUsersContract {
    interface View : BaseContract.View {
        fun updateJoinedUsersList(usersList: List<User>?)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun showJoinedUsers(eventName: String, context: Context)
    }
}