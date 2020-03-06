package com.university.gami_android.ui.change_password

import android.content.Context
import android.widget.Toast
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.ChangePassword
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter

class ChangePasswordPresenter : BasePresenter<ChangePasswordContract.View>(),
    ChangePasswordContract.Presenter {

    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    override fun doChangePassword(changePassword: ChangePassword, context: Context) {
        val call = userRepository.changePassword(changePassword)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {
                if (isBound()) {
                    val toast: Toast = Toast.makeText(
                        context,
                        context.getString(R.string.successfully_changed_pass),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                    getView()?.navigateToLoginActivity(context)
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
    override fun passwordValidation(password: String): Boolean =
        password.trim().length >= 8

    override fun confirmPasswordValidation(password: String, confirmPassword: String): Boolean =
        password.trim() == confirmPassword.trim()

}