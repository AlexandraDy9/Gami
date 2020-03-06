package com.university.gami_android

import android.app.Application
import android.content.Context

class GamiApplication : Application() {

    companion object {
        lateinit var instance: GamiApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun get(context: Context): GamiApplication {
        return context.applicationContext as GamiApplication
    }
}
