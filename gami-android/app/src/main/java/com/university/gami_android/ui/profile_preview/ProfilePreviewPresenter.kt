package com.university.gami_android.ui.profile_preview

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Event
import com.university.gami_android.model.Photo
import com.university.gami_android.model.User
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter

class ProfilePreviewPresenter : BasePresenter<ProfilePreviewContract.View>(),
    ProfilePreviewContract.Presenter {

    private var userRepository: UserRepository = RetrofitClientInstance.retrofitInstance?.create(
        UserRepository::class.java
    )!!

    var userDetails = MutableLiveData<User>()
    var userPhotos = MutableLiveData<List<Photo>>()
    var hostedEvents = MutableLiveData<List<Event>>()

    override fun getUser(context: Context, username: String) {
        val call = userRepository.getUser(PreferenceHandler.getAuthorization(), username)
        val request = HttpRequest(object :
            NetworkCallback<User> {

            override fun onSuccess(response: User?) {
                if (isBound()) {
                    userDetails.value = response
                    getView()?.progressBarVisibility()
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

    override fun getPhotos(context: Context, username: String) {
        val call = userRepository.getPhotosByUser(PreferenceHandler.getAuthorization(), username)

        val request = HttpRequest(object :
            NetworkCallback<List<Photo>> {

            override fun onSuccess(response: List<Photo>?) {
                if (isBound()) {
                    userPhotos.value = response
                    getView()?.progressBarVisibility()
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

    override fun getHostedEvents(context: Context, username: String) {
        val call = userRepository.getHostedEvents(PreferenceHandler.getAuthorization(), username)

        val request = HttpRequest(object :
            NetworkCallback<List<Event>> {

            override fun onSuccess(response: List<Event>?) {
                if (isBound()) {
                    hostedEvents.value = response
                    getView()?.progressBarVisibility()
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