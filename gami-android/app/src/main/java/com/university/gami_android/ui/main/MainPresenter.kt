package com.university.gami_android.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Photo
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter

class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {
    private var userRepository: UserRepository = RetrofitClientInstance.retrofitInstance?.create(
        UserRepository::class.java)!!

    override fun doLogout(context: Context) {
        val call = userRepository.logout()

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {
                if (isBound()) {
                    PreferenceHandler.setUserLastName("")
                    PreferenceHandler.setUserFirstName("")
                    PreferenceHandler.setUserEmail("")
                    PreferenceHandler.setAuthorization("")
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

    var userPhotos = MutableLiveData<List<Photo>>()

    override fun getPhotos(context: Context) {
        val call = userRepository.getPhotosByUser(
            PreferenceHandler.getAuthorization(),
            PreferenceHandler.getUserName()
        )

        val request = HttpRequest(object :
            NetworkCallback<List<Photo>> {

            override fun onSuccess(response: List<Photo>?) {
                if (isBound()) {
                    userPhotos.value = response
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
}