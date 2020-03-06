package com.university.gami_android.ui.signUp

import android.content.Context
import com.university.gami_android.model.User
import com.university.gami_android.ui.base.BaseContract

interface SignUpContract {

    interface View : BaseContract.View {
        fun hasErrorForEmail() : Boolean

        fun hasErrorForPassword() : Boolean

        fun hasErrorForConfirmPassword() : Boolean

        fun navigateToHobbySelectionActivity(context: Context)
    }

    interface Presenter {
        fun fieldsValidation(signUpData: SignUpDao) : Boolean

        fun validateEmail(email: String) : Boolean

        fun validatePassword(password: String) : Boolean

        fun validateConfirmPassword(password: String, confirmPassword: String) : Boolean

        fun doSignUp(user: User, context: Context)
    }
}