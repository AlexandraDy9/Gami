package com.university.gami_android.ui.event_details

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Event
import com.university.gami_android.model.Host
import com.university.gami_android.model.ReviewDao
import com.university.gami_android.model.User
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.EventRepository
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter

class EventDetailsPresenter : BasePresenter<EventDetailsContract.View>(),
    EventDetailsContract.Presenter {

    private var eventRepository: EventRepository =
        RetrofitClientInstance.retrofitInstance?.create(EventRepository::class.java)!!

    private var userRepository: UserRepository =
        RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    var event = MutableLiveData<Event>()
    var host = MutableLiveData<Host>()
    var joinedUsers = MutableLiveData<List<User>>()
    var reviews = MutableLiveData<List<ReviewDao>>()

    override fun joinEvent(eventName: String, context: Context) {
        val call = userRepository.joinEvent(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<Boolean> {

            override fun onSuccess(response: Boolean?) {
                if (isBound()) {
                    if (response == true) {
                        getView()?.makeToast(
                            getView()?.appContext()?.resources?.getString(R.string.join_message)!!,
                            context
                        )
                    } else if (response == false) {
                        getView()?.makeToast(
                            getView()?.appContext()?.resources?.getString(R.string.already_join_message)!!,
                            context
                        )
                    }
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast( getView()?.appContext()?.resources?.getString(R.string.unauthorized)!!, context)
                }
            }
        })
        request.execute(call)
    }


    override fun leftEvent(eventName: String, context: Context) {
        val call = userRepository.leftEvent(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {
                if (isBound()) {
                    getView()?.makeToast(
                            getView()?.appContext()?.resources?.getString(R.string.left_message)!!,
                            context)
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast( getView()?.appContext()?.resources?.getString(R.string.unauthorized)!!, context)
                }
            }
        })
        request.execute(call)
    }

    override fun getReviews(eventName: String, context: Context) {
        val call = eventRepository.getReviews(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<List<ReviewDao>> {

            override fun onSuccess(response: List<ReviewDao>?) {
                if (isBound()) {
                    reviews.value = response
                    getView()?.progressBarVisibility()
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast( getView()?.appContext()?.resources?.getString(R.string.unauthorized)!!, context)
                }
            }
        })
        request.execute(call)
    }

    override fun getEvent(eventName: String, context: Context) {
        val call = eventRepository.getEvent(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<Event> {

            override fun onSuccess(response: Event?) {
                if (isBound()) {
                    event.value = response
                    getView()?.progressBarVisibility()
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast( getView()?.appContext()?.resources?.getString(R.string.unauthorized)!!, context)
                }
            }
        })
        request.execute(call)
    }

    override fun getHost(eventName: String, context: Context) {
        val call = eventRepository.getHost(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<Host> {

            override fun onSuccess(response: Host?) {
                if (isBound()) {
                    host.value = response
                    getView()?.progressBarVisibility()
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast( getView()?.appContext()?.resources?.getString(R.string.unauthorized)!!, context)
                }
            }
        })
        request.execute(call)
    }

    override fun getJoinedUsers(eventName: String, context: Context) {
        val call = eventRepository.getAllJoinedUsers(PreferenceHandler.getAuthorization(), eventName)

        val request = HttpRequest(object :
            NetworkCallback<List<User>> {

            override fun onSuccess(response: List<User>?) {
                if (isBound()) {
                    joinedUsers.value = response
                    getView()?.progressBarVisibility()
                }
            }

            override fun onError(message: String?) {
                if (isBound()) {
                    getView()?.makeToast( getView()?.appContext()?.resources?.getString(R.string.unauthorized)!!, context)
                }
            }
        })
        request.execute(call)
    }
}