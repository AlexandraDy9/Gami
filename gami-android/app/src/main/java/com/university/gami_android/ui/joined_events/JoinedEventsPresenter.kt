package com.university.gami_android.ui.joined_events

import android.content.Context
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Event
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter

class JoinedEventsPresenter : BasePresenter<JoinedEventsContract.View>(), JoinedEventsContract.Presenter {

    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    override fun showEvents(context: Context) {
        val call = userRepository.getEvents(PreferenceHandler.getAuthorization())

        val request = HttpRequest(object :
            NetworkCallback<List<Event>> {

            override fun onSuccess(response: List<Event>?) {
                if (isBound()) {
                    getView()?.updateEventsList(response!!)
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

    override fun removeEvent(context: Context, event: Event) {
        val call = userRepository.removeEventJoined(PreferenceHandler.getAuthorization(), event.name)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {}

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast(getView()?.appContext()?.resources?.getString(R.string.failed_action)!!, context)
                }
            }
        })
        request.execute(call)
    }
}