package com.university.gami_android.ui.write_review

import android.content.Context
import com.university.gami_android.model.SendReview
import com.university.gami_android.ui.base.BaseContract

class WriteReviewContract {
    interface View : BaseContract.View {
        fun hasErrorForReview() : Boolean
        fun navigateToEventDetailsActivity(context: Context)
    }

    interface Presenter {
        fun validateReview(review: String): Boolean
        fun doCreateReview(nameEvent: String, review: SendReview, context: Context)
    }
}