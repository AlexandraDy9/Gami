package com.university.gami_android.ui.login

import android.content.Context
import android.util.Base64
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.LoginDao
import com.university.gami_android.model.User
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter


class LoginPresenter : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    override fun doLogin(loginDao: LoginDao, context: Context) {

        val base: String = loginDao.username + ":" + loginDao.password
        val authString: String = "Basic " + Base64.encodeToString(base.toByteArray(), Base64.NO_WRAP)

        val call = userRepository.login(authString)
        val request = HttpRequest(object :
            NetworkCallback<User> {
            override fun onSuccess(response: User?) {
                if (isBound()) {
                    PreferenceHandler.setUserLastName(response?.lastname!!)
                    PreferenceHandler.setUserFirstName(response.firstname)
                    PreferenceHandler.setUserEmail(response.email)
                    PreferenceHandler.setUserName(response.user)
                    getView()?.changeProgressaBarVisibility(false)
                    getView()?.navigateToMainActivity(context)
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.changeProgressaBarVisibility(true)
                    getView()?.makeToast(getView()?.appContext()?.resources?.getString(R.string.login_failed)!!, context)
                }
            }

        })
        request.execute(call)
    }

    override fun validateInput(loginDao: LoginDao): Boolean =
        loginDao.username.isBlank() || loginDao.password.isBlank()
}