package com.university.gami_android.ui.change_password

import android.content.Context
import com.university.gami_android.model.ChangePassword
import com.university.gami_android.ui.base.BaseContract

class ChangePasswordContract {
    interface View : BaseContract.View {

        fun navigateToLoginActivity(context: Context)
    }

    interface Presenter {

        fun passwordValidation(password: String): Boolean

        fun confirmPasswordValidation(password: String, confirmPassword: String): Boolean

        fun doChangePassword(changePassword: ChangePassword, context: Context)
    }
}