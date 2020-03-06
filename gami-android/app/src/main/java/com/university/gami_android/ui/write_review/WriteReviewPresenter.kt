package com.university.gami_android.ui.write_review

import android.content.Context
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.SendReview
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.EventRepository
import com.university.gami_android.ui.base.BasePresenter


class WriteReviewPresenter  : BasePresenter<WriteReviewContract.View>(),
    WriteReviewContract.Presenter {

    private var eventRepository: EventRepository = RetrofitClientInstance.retrofitInstance?.create(
        EventRepository::class.java)!!


    override fun doCreateReview(nameEvent: String, review: SendReview, context: Context) {
        val call = eventRepository.saveReview(PreferenceHandler.getAuthorization(), nameEvent, review)

        val request = HttpRequest(object :
            NetworkCallback<Void> {

            override fun onSuccess(response: Void?) {
                if (isBound()) {
                    getView()?.makeToast(getView()?.appContext()?.resources?.getString(R.string.post_review)!!, context)
                    getView()?.navigateToEventDetailsActivity(context)
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

    override fun validateReview(review: String): Boolean =
        review.length <= 255
}