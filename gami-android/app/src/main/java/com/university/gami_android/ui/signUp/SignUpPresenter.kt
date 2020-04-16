package com.university.gami_android.ui.signUp

import android.content.Context
import android.util.Patterns
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.SignUpDao
import com.university.gami_android.model.User
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter


class SignUpPresenter : BasePresenter<SignUpContract.View>(), SignUpContract.Presenter {
    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    override fun doSignUp(user: User, context: Context) {
        val call = userRepository.saveUser(user)

        val request = HttpRequest(object :
            NetworkCallback<User> {

            override fun onSuccess(response: User?) {
                if (isBound()) {
                    PreferenceHandler.setUserLastName(response?.lastname!!)
                    PreferenceHandler.setUserFirstName(response.firstname)
                    PreferenceHandler.setUserEmail(response.email)
                    PreferenceHandler.setUserName(response.user)
                    getView()?.navigateToMainActivity(context)
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast(message!!, context)
                }
            }
        })
        request.execute(call)
    }

    override fun fieldsValidation(signUpData: SignUpDao): Boolean =
        signUpData.firstName.isBlank() ||
                signUpData.lastName.isBlank() ||
                signUpData.userName.isBlank() ||
                signUpData.email.isBlank() ||
                signUpData.password.isBlank() ||
                signUpData.confirmPassword.isBlank() ||
                signUpData.birthday.isBlank()

    override fun validateEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    override fun validatePassword(password: String): Boolean =
        password.trim().length >= 8

    override fun validateConfirmPassword(password: String, confirmPassword: String): Boolean =
        password.trim() == confirmPassword.trim()
}