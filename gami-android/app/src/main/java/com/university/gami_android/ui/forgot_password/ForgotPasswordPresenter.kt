package com.university.gami_android.ui.forgot_password

import android.content.Context
import android.util.Patterns
import android.view.View
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.SendMail
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter


class ForgotPasswordPresenter : BasePresenter<ForgotPasswordContract.View>(), ForgotPasswordContract.Presenter {
    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    override fun doForgotPassword(sendMail: SendMail, view: View, context: Context) {
        val call = userRepository.sendMail(sendMail)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {
                if (isBound()) {
                    getView()?.viewSnackbar()
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

    override fun emailValidation(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

}