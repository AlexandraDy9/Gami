package com.university.gami_android.ui.events

import android.content.Context
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Event
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.EventRepository
import com.university.gami_android.ui.base.BasePresenter

class EventMapPresenter : BasePresenter<EventMapContract.View>(), EventMapContract.Presenter {
    private var eventRepository: EventRepository =
        RetrofitClientInstance.retrofitInstance?.create(EventRepository::class.java)!!

    override fun getEvents(context: Context, type: Boolean) {
        val call = eventRepository.getEvents(PreferenceHandler.getAuthorization(), type)

        val request = HttpRequest(object :
            NetworkCallback<List<Event>> {

            override fun onSuccess(response: List<Event>?) {
                if (isBound()) {
                    getView()?.updateEventList(response)
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