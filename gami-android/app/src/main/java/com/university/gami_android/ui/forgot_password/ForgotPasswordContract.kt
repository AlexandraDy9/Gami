package com.university.gami_android.ui.forgot_password

import android.content.Context
import com.university.gami_android.model.SendMail
import com.university.gami_android.ui.base.BaseContract

class ForgotPasswordContract {
    interface View : BaseContract.View {
        fun viewSnackbar()
    }

    interface Presenter {
        fun emailValidation(email: String): Boolean
        fun doForgotPassword(sendMail: SendMail, view: android.view.View, context: Context)
    }
}