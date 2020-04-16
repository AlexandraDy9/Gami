package com.university.gami_android.ui.bookmarks

import android.content.Context
import com.university.gami_android.R
import com.university.gami_android.connection.HttpRequest
import com.university.gami_android.connection.NetworkCallback
import com.university.gami_android.connection.RetrofitClientInstance
import com.university.gami_android.model.Event
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.repository.UserRepository
import com.university.gami_android.ui.base.BasePresenter

class BookmarkPresenter : BasePresenter<BookmarkContract.View>(), BookmarkContract.Presenter {

    private var userRepository: UserRepository = RetrofitClientInstance.retrofitInstance?.create(UserRepository::class.java)!!

    override fun showBookmarks(context: Context) {
        val call = userRepository.getBookmarks(PreferenceHandler.getAuthorization())

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

    override fun removeBookmark(context: Context, event: Event) {
        val call = userRepository.removeBookmark(PreferenceHandler.getAuthorization(), event.name)

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