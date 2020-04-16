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

    override fun getEventsByCategory(context: Context, game: String) {
        val call = eventRepository.getEventsByCategory(PreferenceHandler.getAuthorization(), game)

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

    override fun getEvents(context: Context) {
        val call = eventRepository.getEvents(PreferenceHandler.getAuthorization())

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