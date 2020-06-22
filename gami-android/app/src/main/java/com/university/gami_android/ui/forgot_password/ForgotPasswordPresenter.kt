package com.university.gami_android.ui.forgot_password

import android.content.Context
import android.util.Patterns
import android.view.View
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.SendMail
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter


class ForgotPasswordPresenter : BasePresenter<ForgotPasswordContract.View>(), ForgotPasswordContract.Presenter {
    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    override fun doForgotPassword(sendMail: SendMail, context: Context) {
        val call = userRepository.sendMail(sendMail)

        val request = HttpRequest(object :
            NetworkCallback<Boolean> {

            override fun onSuccess(response: Boolean?) {
                if (isBound()) {
                    getView()?.changeProgressaBarVisibility(false)
                    if(response == true) {
                        getView()?.changeProgressaBarVisibility(true)
                        getView()?.viewSnackbar()
                    }
                    else {
                        getView()?.changeProgressaBarVisibility(true)
                        getView()?.makeToast(getView()?.appContext()?.resources?.getString(R.string.invalid_email)!!, context)
                    }
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.changeProgressaBarVisibility(true)
                    getView()?.makeToast(message!!, context)
                }
            }
        })
        request.execute(call)
    }

    override fun emailValidation(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

}