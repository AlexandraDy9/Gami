package com.university.gami_android.ui.profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Photo
import com.university.gami_android.model.User
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter

class ProfilePresenter : BasePresenter<ProfileContract.View>(), ProfileContract.Presenter {

    private var userRepository: UserRepository = RetrofitClientInstance.retrofitInstance?.create(
        UserRepository::class.java)!!

    var userDetails = MutableLiveData<User>()
    var userPhotos = MutableLiveData<List<Photo>>()

    override fun getUser(context: Context) {
        val call = userRepository.getPrincipal(PreferenceHandler.getAuthorization())
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

    override fun uploadUserPhoto(context: Context, uri: String) {
        val call = userRepository.setUserPhotos(PreferenceHandler.getAuthorization(), PreferenceHandler.getUserName(), Photo(uri))

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {
                if (isBound()) {
                    getView()?.makeToast(context.getString(R.string.edit_successfully), context)
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

    override fun editUser(user: User, context: Context) {
        val call = userRepository.editUser(PreferenceHandler.getAuthorization(), user)

        val request = HttpRequest(object :
            NetworkCallback<User> {

            override fun onSuccess(response: User?) {
                if (isBound()) {
                    getView()?.makeToast(context.getString(R.string.edit_successfully), context)
                    PreferenceHandler.setUserLastName(response?.lastname!!)
                    PreferenceHandler.setUserFirstName(response.firstname)
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