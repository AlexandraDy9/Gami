package com.university.gami_android.ui.add_event

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.AgeRange
import com.university.gami_android.model.Category
import com.university.gami_android.model.Event
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.EventRepository
import com.university.gami_android.ui.base.BasePresenter


class AddEventPresenter : BasePresenter<AddEventContract.View>(), AddEventContract.Presenter {
    private var eventRepository: EventRepository =
        RetrofitClientInstance.retrofitInstance?.create(EventRepository::class.java)!!

    var categories = MutableLiveData<List<Category>>()
    var ageRanges = MutableLiveData<List<AgeRange>>()

    override fun addEvent(event: Event, context: Context) {
        val call = eventRepository.addEvent(PreferenceHandler.getAuthorization(), event)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {
                if (isBound()) {
                    getView()?.navigateToHomeActivity(context)
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

    override fun getCategories(context: Context) {
        val call = eventRepository.getCategories(PreferenceHandler.getAuthorization())

        val request = HttpRequest(object :
            NetworkCallback<List<Category>> {

            override fun onSuccess(response: List<Category>?) {
                if (isBound()) {
                    categories.value = response
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

    override fun getAgeRanges(context: Context) {
        val call = eventRepository.getAgeRanges(PreferenceHandler.getAuthorization())

        val request = HttpRequest(object :
            NetworkCallback<List<AgeRange>> {

            override fun onSuccess(response: List<AgeRange>?) {
                if (isBound()) {
                    ageRanges.value = response
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