package com.university.gami_android.ui.base

import android.content.Context
import android.widget.Toast

interface BaseContract {

    interface View {
        fun appContext(): Context

        fun makeToast(text: String, appContext: Context) {
            Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
        }
    }

    interface Presenter<V : View> {

        fun bindView(view: V)

        fun unbindView()
    }
}