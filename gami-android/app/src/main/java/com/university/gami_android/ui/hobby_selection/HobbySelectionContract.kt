package com.university.gami_android.ui.hobby_selection

import android.content.Context
import android.content.Intent
import com.university.gami_android.ui.base.BaseContract

interface HobbySelectionContract {
    interface View : BaseContract.View {

    }

    interface Presenter {
        fun getImages()
        fun navigateToHomeActivity(context: Context): Intent
    }

}