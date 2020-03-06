package com.university.gami_android.ui.events

import android.content.Context
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Event
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.EventRepository
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter


class EventListPresenter : BasePresenter<EventListContract.View>(), EventListContract.Presenter {
    private var eventRepository: EventRepository =
        RetrofitClientInstance.retrofitInstance?.create(EventRepository::class.java)!!

    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!


    override fun getBookmarkedEvents(context: Context){
        val call = userRepository.getBookmarks(PreferenceHandler.getAuthorization())

        val request = HttpRequest(object :
            NetworkCallback<List<Event>> {

            override fun onSuccess(response: List<Event>?) {
                if (isBound()) {
                    getView()?.updateBookmarkedEventsList(response)
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

    fun addBookmark(context: Context, eventName: String) {
        val call = userRepository.addBookmark(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {}

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast(message!!, context)
                }
            }
        })
        request.execute(call)
    }

    fun removeBookmark(context: Context, eventName: String) {
        val call = userRepository.removeBookmark(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {}

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast(message!!, context)
                }
            }
        })
        request.execute(call)
    }

}