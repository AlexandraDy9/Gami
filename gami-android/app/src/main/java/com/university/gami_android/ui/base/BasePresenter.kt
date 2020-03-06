package com.university.gami_android.ui.base

import androidx.annotation.CallSuper

abstract class BasePresenter<V : BaseContract.View> : BaseContract.Presenter<V> {

    private var view: V? = null

    @CallSuper
    override fun bindView(view: V) {
        this.view = view
    }

    @CallSuper
    override fun unbindView() {
        this.view = null
    }

    fun getView(): V? {
        return view
    }

    fun isBound(): Boolean {
        return view != null
    }
}