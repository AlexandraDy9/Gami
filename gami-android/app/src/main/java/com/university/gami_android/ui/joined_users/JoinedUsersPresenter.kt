package com.university.gami_android.ui.joined_users

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Event
import com.university.gami_android.model.User
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.EventRepository
import com.university.gami_android.ui.base.BasePresenter

class JoinedUsersPresenter: BasePresenter<JoinedUsersContract.View>(), JoinedUsersContract.Presenter {

    private var eventRepository: EventRepository =
        RetrofitClientInstance.retrofitInstance?.create(EventRepository::class.java)!!

    override fun showJoinedUsers(eventName: String, context: Context) {
        val call = eventRepository.getAllJoinedUsers(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<List<User>> {

            override fun onSuccess(response: List<User>?) {
                if (isBound()) {
                    getView()?.updateJoinedUsersList(response!!)
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